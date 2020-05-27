package com.parabbits.tajniakiserver.game;

import com.google.gson.Gson;

import com.parabbits.tajniakiserver.game.end_game.EndGameHelper;
import com.parabbits.tajniakiserver.game.end_game.EndGameInfo;
import com.parabbits.tajniakiserver.game.messages.*;
import com.parabbits.tajniakiserver.game.models.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GameController {

    private final String START_MESSAGE_RESPONSE = "/queue/game/start";
    private final String ANSWER_MESSAGE_RESPONSE = "/queue/game/answer";
    private final String CLICK_MESSAGE_RESPONSE = "/queue/game/click";
    private final String QUESTION_MESSAGE_RESPONSE = "/queue/game/question";
    private final String END_MESSAGE_RESPONSE = "/queue/game/end_game";
    private final String POSSIBLE_MESSAGE_RESPONSE = "/queue/game/possible_game";

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
        game.getHistory().setWords(getWordsFromCards(CardColor.BLUE, game), Team.BLUE);
        game.getHistory().setWords(getWordsFromCards(CardColor.RED, game), Team.RED);
        game.getHistory().setKiller(getWordsFromCards(CardColor.KILLER, game).get(0));
    }

    private List<String> getWordsFromCards(CardColor color, Game game){
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
            game.useCard(card);
            handleAnswerMessage(player, card);
        } else {
            handleClickMessage(player);
        }
    }

    private Card findCard(Integer cardId){
        return game.getBoard().getCard(cardId);
    }

    private boolean isAllPlayersAnswer(Player player, int answerForCard) {
        return answerForCard == game.getTeamSize(player.getTeam()) - 1;
    }

    private void handleAnswerMessage(Player player, Card card) {
        sendAnswerMessage(player, card, isCorrect(card, player));
        if (!game.getState().isGameActive()) {
            sendEndGameMessage();
        }
    }

    private void sendEndGameMessage() {
        EndGameMessage endGameMessage = getEndGameMessage();
        messageManager.sendToAll(endGameMessage, END_MESSAGE_RESPONSE, game);
    }

    private void sendAnswerMessage(Player player, Card card, boolean correct) {
        List<Card> cardsToUpdate = game.getBoard().getAnswerManager().popCardsToUpdate(player);
        AnswerMessage bossMessage = buildAnswerMessage(cardsToUpdate, correct, player, Role.BOSS);
        messageManager.sendToPlayersWithRole(bossMessage, Role.BOSS, ANSWER_MESSAGE_RESPONSE, game);
        AnswerMessage playerMessage = buildAnswerMessage(cardsToUpdate, correct, player, Role.PLAYER);
        messageManager.sendToPlayersWithRole(playerMessage, Role.PLAYER, ANSWER_MESSAGE_RESPONSE, game);
    }

    private boolean isCorrect(Card card, Player player){
        return (card.getColor()== CardColor.BLUE && player.getTeam() == Team.BLUE) || (card.getColor() == CardColor.RED && player.getTeam() == Team.RED);
    }

    private void handleIncorrectMessage(Card card, Player player) {
        handleCorrectMessage(card, false, player);
    }

    private void handleCorrectMessage(Card card, boolean correct, Player player) {
        sendAnswerMessage(player, card, correct);
    }

    private EndGameMessage getEndGameMessage(){
        EndGameMessage message = new EndGameMessage();
        EndGameInfo info = EndGameHelper.getEndGameInfo(game);
        message.setCause(info.getCause());
        message.setWinner(info.getWinner());
        return message;
    }

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

    private AnswerMessage buildAnswerMessage(List<Card> cardsToUpdate, boolean correct, Player player,Role role) {
//        ClientCard clientCard = ClientCardCreator.createCard(card, game, player.getRole(), player.getTeam());
        // TODO: sprawdzić po co jest rola i drużyna
        List<ClientCard> cards = ClientCardCreator.createCards( cardsToUpdate, game, role, player.getTeam());
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

    @MessageMapping("/game/possible_game")
    public void checkFreeGame(@Payload String message, SimpMessageHeaderAccessor headerAccessor){
        boolean value = game.getState().getCurrentStep() == null
                || game.getState().getCurrentStep() == GameStep.MAIN
                || game.getState().getCurrentStep() == GameStep.LOBBY;
        messageManager.send(value, headerAccessor.getSessionId(), POSSIBLE_MESSAGE_RESPONSE);
    }
}