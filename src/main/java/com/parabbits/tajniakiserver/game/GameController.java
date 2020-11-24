package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.connection.DisconnectController;
import com.parabbits.tajniakiserver.connection.PlayersManager;
import com.parabbits.tajniakiserver.game.messages.*;
import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.parameters.QuestionParam;
import com.parabbits.tajniakiserver.shared.game.ClickResult;
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
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController {

    private final String START_MESSAGE_RESPONSE = "/queue/game/start";
    private final String ANSWER_MESSAGE_RESPONSE = "/queue/game/answer";
    private final String CLICK_MESSAGE_RESPONSE = "/queue/game/click";
    private final String QUESTION_MESSAGE_RESPONSE = "/queue/game/question";
    private final String END_MESSAGE_RESPONSE = "/queue/game/end_game";

    @Autowired
    private GameManager gameManager;

    @Autowired
    private PlayersManager playersManager;

    @Autowired
    private MessageManager messageManager;

    @Autowired
    private DisconnectController disconnectController;

    @MessageMapping("/game/start")
    public void startGame(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        // TODO: sprawdzenie poprawności stanu
        game.getState().setCurrentStep(GameStep.GAME);
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
        ClickResult result = game.click(param.getValue(), player);
        handleClickAction(game, player, result);
    }

    private void handleClickAction(Game game, Player player, ClickResult result) {
        switch (result.getType()) {
            case CLICK:
                handleClickMessage(player, result.getUpdatedCards(), game);
                break;
            case ANSWER:
                handleAnswerMessage(player, CorrectnessUtil.isCorrect(result.getCorrectness()), game, result.getUpdatedCards());
                break;
            case END_GAME:
                // TODO: na razie zostawić to w ten spsobó
                sendEndGameMessage(game);
                break;
        }
    }

    private void handleAnswerMessage(Player player, boolean correct, Game game, List<Card> cardsToUpdate) {
        sendAnswerMessageToRole(player, correct, game, Role.SPYMASTER, cardsToUpdate);
        sendAnswerMessageToRole(player, correct, game, Role.ORDINARY_PLAYER, cardsToUpdate);
    }

    private void sendAnswerMessageToRole(Player player, boolean correct, Game game, Role role, List<Card> cardsToUpdate) {
        AnswerMessage message = AnswerMessageCreator.create(cardsToUpdate, correct, player, role, game);
        messageManager.sendToPlayersWithRole(message, role, ANSWER_MESSAGE_RESPONSE, game);
    }

    private void handleClickMessage(Player player, List<Card> updatedCards, Game game) {
        ClickMessage message = ClickMessageCreator.create(player, updatedCards, game);
        messageManager.sendToRoleFromTeam(message, Role.ORDINARY_PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
    }

    private void sendEndGameMessage(Game game) {
        EndGameMessage endGameMessage = EndGameMessageCreator.create(game);
        messageManager.sendToAll(endGameMessage, END_MESSAGE_RESPONSE, game);
    }

    @MessageMapping("/game/question")
    public void setQuestion(@Payload QuestionParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        boolean correct = game.setQuestion(param, headerAccessor.getSessionId());
        if (correct) {
            SpymasterMessage message = SpymasterMessageCreator.create(param.getQuestion(), param.getNumber(), game);
            messageManager.sendToAll(message, QUESTION_MESSAGE_RESPONSE, game);
        }
    }

    @MessageMapping("/game/flag")
    public void setFlag(@Payload IntParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Card card = game.flag(param.getValue(), player);
        if (card != null) {
            ClickMessage message = FlagMessageCreator.create(player, card, game);
            messageManager.sendToRoleFromTeam(message, Role.ORDINARY_PLAYER, player.getTeam(), CLICK_MESSAGE_RESPONSE, game);
        }
    }

    @MessageMapping("/game/quit")
    public void quit(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) {
        disconnectController.disconnectPlayer(headerAccessor.getSessionId(), param.getGameId());
    }
}