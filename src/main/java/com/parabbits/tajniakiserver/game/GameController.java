package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.connection.DisconnectController;
import com.parabbits.tajniakiserver.game.messages.*;
import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.game.parameters.QuestionParam;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.shared.parameters.IdParam;
import com.parabbits.tajniakiserver.shared.parameters.IntParam;
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

    private final String START_MESSAGE_RESPONSE = "/queue/game/start";
    private final String ANSWER_MESSAGE_RESPONSE = "/queue/game/answer";
    private final String CLICK_MESSAGE_RESPONSE = "/queue/game/click";
    private final String QUESTION_MESSAGE_RESPONSE = "/queue/game/question";
    private final String END_MESSAGE_RESPONSE = "/queue/game/end_game";
    private final String POSSIBLE_MESSAGE_RESPONSE = "/queue/game/possible_game";

    @Autowired
    private GameManager gameManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init() {
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/game/start")
    public void startGame(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Game game = gameManager.findGame(param.getGameId());
        // TODO: sprawdzenie poprawności stanu
        game.getState().setCurrentStep(GameStep.GAME);
        if(!game.isStarted()){
            game.initializeGame();
        }
        sendStartGameMessage(headerAccessor.getSessionId(), game);
    }

    private void sendStartGameMessage(String playerSessionId, Game game) {
        Player player = game.getPlayers().getPlayer(playerSessionId);
        StartGameMessage message = StartGameMessageCreator.create(player.getRole(), player, game);
        messageManager.send(message, playerSessionId, START_MESSAGE_RESPONSE);
    }

    @MessageMapping("/game/click")
    public void servePlayersAnswer(@Payload IntParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Card card = findCard(param.getValue(), game);
        if (!canClick(player, card, game)) {
            return;
        }
        handleAnswer(game, player, card);
    }

    private void handleAnswer(Game game, Player player, Card card) {
        // TODO: przenieść wszystko do klasy Game, return - czy wszyscy odpowiedzieli
        AnswerManager answerManager = game.getBoard().getAnswerManager();
        answerManager.setAnswer(card, player);
        int answerForCard = answerManager.getCounter(card);
        if (isAllPlayersAnswer(player, answerForCard, game)) {
            game.getHistory().addAnswer(card.getWord(), card.getColor());
            game.useCard(card);
            handleAnswerMessage(player, card, game);
        } else {
            handleClickMessage(player, game);
        }
    }

    private boolean canClick(Player player, Card card, Game game) {
        return isPlayerTurn(player, game) && !card.isChecked();
    }

    private boolean isPlayerTurn(Player player, Game game) {
        return player.getTeam() == game.getState().getCurrentTeam()
                && player.getRole() == game.getState().getCurrentStage();
    }

    private Card findCard(Integer cardId, Game game){
        return game.getBoard().getCard(cardId);
    }

    private boolean isAllPlayersAnswer(Player player, int answerForCard, Game game) {
        return answerForCard == game.getPlayers().getTeamSize(player.getTeam()) - 1;
    }

    private void handleAnswerMessage(Player player, Card card, Game game) {
        sendAnswerMessage(player, card, isCorrect(card, player), game);
        if (!game.getState().isGameActive()) { // TODO: może jakoś inaczej zrobić zarządzanie grą
            sendEndGameMessage(game);
        }
    }

    private void handleClickMessage(Player player, Game game) {
        ClickMessage message = ClickMessageCreator.create(player, game);
        messageManager.sendToRoleFromTeam(message, Role.PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    private void sendEndGameMessage(Game game) {
        EndGameMessage endGameMessage = EndGameMessageCreator.create(game);
        messageManager.sendToAll(endGameMessage, END_MESSAGE_RESPONSE, game);
    }

    private void sendAnswerMessage(Player player, Card card, boolean correct, Game game) {
        List<Card> cardsToUpdate = game.getBoard().getAnswerManager().popCardsToUpdate(player);
        sendAnswerMessageToRole(player, correct, game, Role.BOSS, cardsToUpdate);
        sendAnswerMessageToRole(player, correct, game, Role.PLAYER, cardsToUpdate);
    }

    private void sendAnswerMessageToRole(Player player, boolean correct, Game game, Role role, List<Card> cardsToUpdate){
        // TODO: sprawdzić, czy wysyłanie tej wiadomości jest dobrze zrobione
        AnswerMessage message = AnswerMessageCreator.create(cardsToUpdate, correct, player, role, game);
        messageManager.sendToPlayersWithRole(message, role, ANSWER_MESSAGE_RESPONSE, game);
    }

    private boolean isCorrect(Card card, Player player){
        return (card.getColor()== CardColor.BLUE && player.getTeam() == Team.BLUE) || (card.getColor() == CardColor.RED && player.getTeam() == Team.RED);
    }

    @MessageMapping("/game/question")
    public void setQuestion(@Payload QuestionParam param, SimpMessageHeaderAccessor headerAccessor) {
        // TODO: mocna refaktoryzacja metody
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        if (!isPlayerTurn(player, game)) {
            return;
        }

        // TODO: to powinno być załatwione za jendym zamachem
        String word = param.getQuestion();
        int number = param.getNumber();
        game.getState().setAnswerState(word, number);
        game.getHistory().addQuestion(word, number, player.getTeam());
        if(!WordValidator.validate(word)){ // TODO: sprawdzić co to robi
            return;
        }
        BossMessage message = BossMessageCreator.create(param.getQuestion(), param.getNumber(), game);
        messageManager.sendToAll(message, QUESTION_MESSAGE_RESPONSE, game);
    }

    @MessageMapping("/game/flag")
    public void setFlag(@Payload IntParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Card card = game.getBoard().getCard(param.getValue());
        if (!canClick(player, card, game)) {
            return;
        }
        game.getBoard().getFlagsManager().addFlag(player, card);
        ClickMessage message = FlagMessageCreator.create(player, card, game);
        messageManager.sendToRoleFromTeam(message, Role.PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    @MessageMapping("/game/quit")
    public void quit(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor){
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        game.getPlayers().removePlayer(player.getSessionId());
        DisconnectController.disconnectPlayer(player, game, messageManager);
    }
}