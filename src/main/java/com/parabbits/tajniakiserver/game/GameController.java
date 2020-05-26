package com.parabbits.tajniakiserver.game;

import com.google.gson.Gson;

import com.parabbits.tajniakiserver.game.messages.*;
import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.history.HistoryEntry;
import com.parabbits.tajniakiserver.shared.Game;
import com.parabbits.tajniakiserver.shared.GameStep;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.awt.event.FocusEvent;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GameController {

    // TODO: refaktoryzacja tego

    private final String START_MESSAGE_RESPONSE = "/queue/game/start";
    private final String ANSWER_MESSAGE_RESPONSE = "/queue/game/answer";
    private final String CLICK_MESSAGE_RESPONSE = "/queue/game/click";
    private final String QUESTION_MESSAGE_RESPONSE = "/queue/game/question";
    private final String END_MESSAGE_RESPONSE = "/queue/game/end_game";

    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init() {
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/game/start")
    public void startGame(@Payload String message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        game.getState().setCurrentStep(GameStep.GAME); // TODO: to też powinno znajdować się w innym miejscu
        // TODO: prawdopodobnie chodzi o to, że gra ni kończy inicjalizacji dopóki gra się nie otrzowy
        synchronized (this){
            // TODO: to może zadziałać, ale może rodzić problemy w przypadku wielu gier. Wymyslić lepszy sposób
            // TODO: to może zadziałać, ale może rodzić problemy w przypadku wielu gier. Wymyslić lepszy sposób
            game.initializeGame();
        }
//        game.initializeGame();
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if (player.getRole() == Role.BOSS) {
            StartGameMessage bossMessage = createStartGameMessage(Role.BOSS, player, game);
            messageManager.send(bossMessage, player.getSessionId(), START_MESSAGE_RESPONSE);
        } else {
            StartGameMessage playersMessage = createStartGameMessage(Role.PLAYER, player, game);
            messageManager.send(playersMessage, player.getSessionId(), START_MESSAGE_RESPONSE);
        }

        initHistory(game);
    }

    private void initHistory(Game game){
        game.getHistory().setWords(getWordsFromCards(WordColor.BLUE, game), Team.BLUE);
        game.getHistory().setWords(getWordsFromCards(WordColor.RED, game), Team.RED);
        game.getHistory().setKiller(getWordsFromCards(WordColor.KILLER, game).get(0));
    }

    private List<String> getWordsFromCards(WordColor color, Game game){
        return game.getBoard().getCards().stream().filter(card -> card.getColor() == color).map(Card::getWord).collect(Collectors.toList());
    }

    // TODO: można przenieść do oodzielnej klasy
    public static StartGameMessage createStartGameMessage(Role role, Player player, Game game) {
        StartGameMessage message = new StartGameMessage();
        message.setNickname(player.getNickname());
        message.setPlayerRole(role);
        message.setPlayerTeam(player.getTeam());
        message.setGameState(game.getState());
        List<ClientCard> cards = ClientCardCreator.createCards(game.getBoard().getCards(), game, role, player.getTeam());
        message.setCards(cards);
        message.setPlayers(new ArrayList<>(game.getPlayers()));

        return message;
    }

    @MessageMapping("/game/click")
    public void servePlayersAnswer(@Payload Integer cardId, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Card card = findCard(cardId);
        // TODO: zrobić refaktoryzację z tym
        if (!isPlayerTurn(player) || card.isChecked() || player.getRole()==Role.BOSS) {
            return;
        }
        game.getBoard().getAnswerManager().setAnswer(card, player);
        int answerForCard = game.getBoard().getAnswerManager().getCounter(card);
        if (isAllPlayersAnswer(player, answerForCard)) {
            game.getHistory().addAnswer(card.getWord(), card.getColor());
            handleAnswerMessage(player, card);
        } else {
            handleClickMessage(player);
        }
    }

    private Card findCard(Integer cardId){
        Card card = game.getBoard().getCard(cardId);
        return card;
    }

    private boolean isAllPlayersAnswer(Player player, int answerForCard) {
        return answerForCard == game.getTeamSize(player.getTeam()) - 1;
    }

    private void handleAnswerMessage(Player player, Card card) {
        // TODO: refaktoryzacja
        game.useCard(card);

        // TODO: zlikwidować to jakoś
//        AnswerCorrectness.Correctness correctness = AnswerCorrectness.checkCorrectness(card.getColor(), player.getTeam());
        AnswerMessage message = buildAnswerMessage(card, isCorrect(card, player), player);
        messageManager.sendToAll(message, ANSWER_MESSAGE_RESPONSE, game);
//        switch (correctness) {
//            case CORRECT:
//                handleCorrectMessage(card, true, player);
//                break;
//            case INCORRECT:
//                handleIncorrectMessage(card, player);
//                break;
//            case KILLER:
//                handleEndGameMessage(player, EndGameCause.KILLER, player.getTeam() == Team.BLUE ? Team.RED : Team.BLUE);
//                break;
//        }
        if (!game.getState().isGameActive()) { // TODO: zrobić lepsze zakończenie gry
//            handleEndGameMessage(EndGameCause.ALL, player.getTeam());
                EndGameMessage endGameMessage = getEndGameMessage();
                messageManager.sendToAll(endGameMessage, END_MESSAGE_RESPONSE, game);
        }
    }

    private boolean isCorrect(Card card, Player player){
        return (card.getColor()==WordColor.BLUE && player.getTeam() == Team.BLUE) || (card.getColor() == WordColor.RED && player.getTeam() == Team.RED);
    }

    private void handleIncorrectMessage(Card card, Player player) {
        handleCorrectMessage(card, false, player);
    }

    private void handleCorrectMessage(Card card, boolean correct, Player player) {
        AnswerMessage answerResult = buildAnswerMessage(card, correct, player);
        messageManager.sendToAll(answerResult, ANSWER_MESSAGE_RESPONSE, game);
    }

    // PRZENIEŚĆ W INNE MIEJSCE-----------------------------------------------------------------------

    // TODO: przenieść do innej klasy. Można zrobić metodę, która zwraca powód i drużynę
    private EndGameMessage getEndGameMessage(){
        // TODO: sprawdzenie, powodu i drużyny
        // 1. wszystko zgadnięte
        System.out.println("Koniec gry panowie");
       EndGameMessage message = new EndGameMessage();
        if(game.getState().getRemainingBlue() == 0){
            message.setCause(EndGameCause.ALL);
            message.setWinner(Team.BLUE);
        } else if(game.getState().getRemainingRed()==0){
            message.setCause(EndGameCause.ALL);
            message.setWinner(Team.RED);
        } else if(isKillerChecked(game)){
            message.setCause(EndGameCause.KILLER);
            message.setWinner(game.getState().getCurrentTeam().opposite()); // TODO: sprawdzić, czy na pewno tak będzie
        } else {
            message.setCause(EndGameCause.UNKNOWN);
            // TODO: zrobić jeszcze remis
            message.setWinner(game.getState().getRemainingBlue() > game.getState().getRemainingRed()? Team.BLUE : Team.RED);
        }
        return message;
        // TODO: tutaj można jeszcze
    }

    private boolean isKillerChecked(Game game){
        for(Card card: game.getBoard().getCards()){
            if(card.getColor()==WordColor.KILLER && card.isChecked()){
                return true;
            }
        }
        return false;
    }

    private void handleEndGameMessage(EndGameCause cause, Team winnerTeam) {
        // TODO: trzeba poprawnie wyświetlić powód i wygranego
        System.out.println("Koniec gry panowie");
        EndGameMessage message = new EndGameMessage();
        message.setWinner(winnerTeam);
        message.setCause(cause);
        message.setRemainingBlue(game.getState().getRemainingBlue());
        message.setRemainingRed(game.getState().getRemainingRed());
        messageManager.sendToAll(message, END_MESSAGE_RESPONSE, game);
    }

    //-------------------------------------------------------------------------------------

    private void handleClickMessage(Player player) {
        ClickMessage message = buildClickMessage(player);
        messageManager.sendToRoleFromTeam(message, Role.PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    private ClickMessage buildClickMessage(Player player) {
        List<Card> editedCards = game.getBoard().getAnswerManager().popCardsToUpdate(player);
        List<ClientCard> clientCards = prepareClientCards(editedCards, player);
        ClickMessage message = new ClickMessage(clientCards);
        List<Card> passCard = editedCards.stream().filter(x->x.getId() < 0).collect(Collectors.toList());
        if(!passCard.isEmpty()){
            ClientCard passClientCard = ClientCardCreator.createCard(passCard.get(0), game, player.getRole(), player.getTeam());
            message.setPass(passClientCard.getAnswers().size());
        }
        // TODO: dodac liczbę pominiętych
        return message;
    }

    private List<ClientCard> prepareClientCards(List<Card> cards, Player player) {
        List<ClientCard> clientCards = new ArrayList<>();
        for (Card card : cards) {
            ClientCard clientCard = ClientCardCreator.createCard(card, game, player.getRole(), player.getTeam());
            clientCards.add(clientCard);
        }
        return clientCards;
    }

    private AnswerMessage buildAnswerMessage(Card card, boolean correct, Player player) {
//        ClientCard clientCard = ClientCardCreator.createCard(card, game, player.getRole(), player.getTeam());
        // TODO: sprawdzić po co jest rola i drużyna
        List<ClientCard> cards = ClientCardCreator.createCards(game.getBoard().getAnswerManager().popCardsToUpdate(player), game, player.getRole(), player.getTeam());
        return new AnswerMessage(cards, correct, game.getState());
    }

    private boolean isPlayerTurn(Player player) {
        return player.getTeam() == game.getState().getCurrentTeam() && player.getRole() == game.getState().getCurrentStage();
    }

    @MessageMapping("/game/question")
    public void setQuestion(@Payload String messsageText, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if (!isPlayerTurn(player) || player.getRole()==Role.PLAYER) {
            return;
        }
        BossMessage message = buildBossMessage(messsageText, new Gson());
        game.getHistory().addQuestion(message.getWord(), message.getNumber(), player.getTeam());
        if(!WordValidator.validate(message.getWord())){
            return;
        }
        messageManager.sendToAll(message, QUESTION_MESSAGE_RESPONSE, game);
    }

    private BossMessage buildBossMessage(@Payload String messsageText, Gson gson) {
        BossMessage message = gson.fromJson(messsageText, BossMessage.class);
        game.getState().setAnswerState(message.getWord(), message.getNumber());
        message.setGameState(game.getState());
        return message;
    }

    @MessageMapping("/game/flag")
    public void setFlag(@Payload Integer cardId, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Card card = game.getBoard().getCard(cardId);
        if (card.isChecked() || player.getRole()==Role.BOSS || isPassCard(card)) {
            return;
        }
        game.getBoard().getFlagsManager().addFlag(player, card);
        ClickMessage message = buildFlagMessage(player, card);
        messageManager.sendToRoleFromTeam(message, Role.PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    // TODO: metodę można przenieść w inne miejsce (np do karty)
    private boolean isPassCard(Card card){
        return card.getId() == -1;
    }

    private ClickMessage buildFlagMessage(Player player, Card card) {
        List<Card> editedCards = Collections.singletonList(card);
        List<ClientCard> clientCards = prepareClientCards(editedCards, player);
        return new ClickMessage(clientCards);
    }

    @MessageMapping("/game/summary")
    public void getSummary(@Payload String message, SimpMessageHeaderAccessor headerAccessor){
//        if(!game.isStarted()){ // TODO: później to odkomentować
//            return;
//        } // TODO: spróbować zrobić to jakoś lepiej
        SummaryMessage summary = new SummaryMessage();
        // TODO: powstawiać odpowiednie wartości
        summary.setWinner(Team.BLUE);
        summary.setBlueRemaining(0);
        summary.setRedRemaining(4);
//        List<HistoryEntry> blueHistory = game.getHistory().getEntries().stream().filter(x->x.getTeam() == Team.BLUE).collect(Collectors.toList());
//        List<HistoryEntry> redHistory = game.getHistory().getEntries().stream().filter(x->x.getTeam() == Team.RED).collect(Collectors.toList());
//        List<HistoryEntry> blueHistory = getBlueHistory();
//        List<HistoryEntry> redHistory = getRedHistory();

        summary.setProcess(getHistory());
        summary.setCause(WinnerCause.ALL_FOUND);

        messageManager.sendToAll(summary, "queue/game/summary", game);

        // po utworzeniu
        game.reset();
    }

    private List<HistoryEntry> getHistory(){
        HistoryEntry entry1 = new HistoryEntry();
        entry1.setQuestion("Kaszanka");
        entry1.setNumber(3);
        entry1.addAnswer("Osioł", WordColor.BLUE);
        entry1.addAnswer("Kaszana", WordColor.NEUTRAL);
        entry1.setTeam(Team.BLUE);

        HistoryEntry entry2 = new HistoryEntry();
        entry2.setQuestion("Osiem");
        entry2.setNumber(2);
        entry2.addAnswer("Jeden", WordColor.RED);
        entry2.addAnswer("Dwa", WordColor.BLUE);
        entry2.setTeam(Team.RED);

        HistoryEntry entry3 = new HistoryEntry();
        entry3.setQuestion("Radar");
        entry3.setNumber(1);
        entry3.addAnswer("Talerz", WordColor.RED);
        entry3.setTeam(Team.BLUE);

        return Arrays.asList(entry1, entry2, entry3);
    }
}