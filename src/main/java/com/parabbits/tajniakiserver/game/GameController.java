package com.parabbits.tajniakiserver.game;

import com.google.gson.Gson;

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

@Controller
public class GameController {

    // TODO: refaktoryzacja tego

    private final String START_MESSAGE_RESPONSE = "/queue/game/start";
    private final String ANSWER_MESSAGE_RESPONSE = "/queue/game/answer";
    private final String CLICK_MESSAGE_RESPONSE = "/queue/game/click";
    private final String QUESTION_MESSAGE_RESPONSE = "/queue/game/question";
    private final String END_MESSAGE_RESPONSE = "/queue/game/question";

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
    public void startGame(@Payload String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        game.getState().setCurrentStep(GameStep.GAME); // TODO: to też powinno znajdować się w innym miejscu
        game.initializeGame();
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if (player.getRole() == Role.BOSS) {
            StartGameMessage bossMessage = createStartGameMessage(Role.BOSS, player, game);
            messageManager.send(bossMessage, player.getSessionId(), START_MESSAGE_RESPONSE);
        } else {
            StartGameMessage playersMessage = createStartGameMessage(Role.PLAYER, player, game);
            messageManager.send(playersMessage, player.getSessionId(), START_MESSAGE_RESPONSE);
        }
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
    public void servePlayersAnswer(@Payload String word, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Card card = game.getBoard().getCard(word);
        if (!isPlayerTurn(player) || card.isChecked() || player.getRole()==Role.BOSS) {
            return;
        }
        game.getBoard().getAnswerManager().setAnswer(card, player);
        int answerForCard = game.getBoard().getAnswerManager().getCounter(card);
        if (isAllPlayersAnswer(player, answerForCard)) {
            handleAnswerMessage(player, card);
        } else {
            handleClickMessage(player);
        }
    }

    private boolean isAllPlayersAnswer(Player player, int answerForCard) {
        return answerForCard == game.getTeamSize(player.getTeam()) - 1;
    }

    private void handleAnswerMessage(Player player, Card card) {
        // TODO: refaktoryzacja
        game.useCard(card);
        // TODO: zlikwidować to jakoś
        AnswerCorrectness.Correctness correctness = AnswerCorrectness.checkCorrectness(card.getColor(), player.getTeam());
        switch (correctness) {
            case CORRECT:
                handleCorrectMessage(card, true, player);
                break;
            case INCORRECT:
                handleIncorrectMessage(card, player);
                break;
            case KILLER:
                handleEndGameMessage(player, EndGameCause.KILLER, player.getTeam() == Team.BLUE ? Team.RED : Team.BLUE);
                break;
        }
        if (!game.getState().isGameActive()) {
            handleEndGameMessage(player, EndGameCause.ALL, player.getTeam());
        }
    }

    private void handleIncorrectMessage(Card card, Player player) {
        // TODO: poinformowanie gry o błędnym wyborze
        handleCorrectMessage(card, false, player);
    }

    private void handleCorrectMessage(Card card, boolean correct, Player player) {
        AnswerMessage answerResult = buildAnswerMessage(card, correct, player);
        messageManager.sendToAll(answerResult, ANSWER_MESSAGE_RESPONSE, game);
    }

    private void handleEndGameMessage(Player player, EndGameCause cause, Team winnerTeam) {
        EndGameMessage message = new EndGameMessage();
//        Team winner = player.getTeam() == Team.BLUE? Team.RED : Team.BLUE;
        message.setWinner(winnerTeam);
        message.setCause(cause);
        message.setRemainingBlue(game.getState().getRemainingBlue());
        message.setRemainingRed(game.getState().getRemainingRed());
        messageManager.sendToAll(message, END_MESSAGE_RESPONSE, game);
        // TODO: zrobić komunikat o zakończeniu gry
    }

    private void handleClickMessage(Player player) {
        ClickMessage message = buildClickMessage(player);
        messageManager.sendToRoleFromTeam(message, Role.PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    private ClickMessage buildClickMessage(Player player) {
        List<Card> editedCards = game.getBoard().getAnswerManager().popCardsToUpdate(player);
        List<ClientCard> clientCards = prepareClientCards(editedCards, player);
        return new ClickMessage(clientCards);
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
        ClientCard clientCard = ClientCardCreator.createCard(card, game, player.getRole(), player.getTeam());
        return new AnswerMessage(clientCard, correct, game.getState());
    }

    private boolean isPlayerTurn(Player player) {
        return player.getTeam() == game.getState().getCurrentTeam() && player.getRole() == game.getState().getCurrentStage();
    }

    @MessageMapping("/game/question")
    public void setQuestion(@Payload String messsageText, SimpMessageHeaderAccessor headerAccessor) {
        // TODO: można dodać jakąś historię podawanych haseł
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if (!isPlayerTurn(player) || player.getRole()==Role.PLAYER) {
            return;
        }
        BossMessage message = buildBossMessage(messsageText, new Gson());
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
    public void setFlag(@Payload String word, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Card card = game.getBoard().getCard(word);
        if (card.isChecked() || player.getRole()==Role.BOSS) {
            return;
        }
        game.getBoard().getFlagsManager().addFlag(player, card);
        ClickMessage message = buildFlagMessage(player, card);
        messageManager.sendToRoleFromTeam(message, Role.PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    private ClickMessage buildFlagMessage(Player player, Card card) {
        List<Card> editedCards = Collections.singletonList(card);
        List<ClientCard> clientCards = prepareClientCards(editedCards, player);
        return new ClickMessage(clientCards);
    }
}