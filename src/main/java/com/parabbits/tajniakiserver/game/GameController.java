package com.parabbits.tajniakiserver.game;

import com.google.gson.Gson;

import com.parabbits.tajniakiserver.game.messages.AnswerResult;
import com.parabbits.tajniakiserver.game.messages.BossMessage;
import com.parabbits.tajniakiserver.game.messages.StartGameMessage;
import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
public class GameController{

    private final String START_MESSAGE_RESPONSE = "/queue/game/start";
    private final String CLICK_MESSAGE_RESPONSE = "/queue/game/answer";
    private final String QUESTION_MESSAGE_RESPONSE = "/queue/game/question";
    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/game/start")
    public void startGame(@Payload  String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception{
        game.initializeGame();

        StartGameMessage bossMessage = createStartGameMessage(Role.BOSS);
        StartGameMessage playersMessage = createStartGameMessage(Role.PLAYER);

        messageManager.sendToPlayersWithRole(bossMessage, Role.BOSS, START_MESSAGE_RESPONSE, game);
        messageManager.sendToPlayersWithRole(playersMessage, Role.PLAYER, START_MESSAGE_RESPONSE, game);
    }

    // TODO: można przenieść do oodzielnej klasy
    private StartGameMessage createStartGameMessage(Role role){
        StartGameMessage message = new StartGameMessage();
        message.setPlayerRole(role);
        message.setGameState(game.getState());
        message.setCards(game.getBoard().getCards(role));
        return message;
    }

    @MessageMapping("/game/click")
    public void servePlayersAnswer(@Payload String word, SimpMessageHeaderAccessor headerAccessor){
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if(!isPlayerTurn(player)){
            return;
        }
        Card card = game.getBoard().getCard(word);
        AnswerCorrectness.Correctness correctness = AnswerCorrectness.checkCorrectness(card.getColor(), player.getTeam());
        AnswerResult answerResult;
        switch(correctness){
            case CORRECT:
                answerResult = buildAnswerMessage(card, true);
                messageManager.sendToAll(answerResult, CLICK_MESSAGE_RESPONSE, game);
            case INCORRECT:
                // TODO: poinformowanie gry o błędnym wyborze
                game.getState().nextTeam(); // TODO: to raczej nie powinno być w tym miejscu
                answerResult = buildAnswerMessage(card, false);
                messageManager.sendToAll(answerResult, CLICK_MESSAGE_RESPONSE, game);
                break;
            case KILLER:
                // TODO: wysłać komunikat o końcu gry
                break;

        }
    }

    private AnswerResult buildAnswerMessage(Card card, boolean correct) {
        AnswerResult result = new AnswerResult(card.getWord(), card.getColor());
        result.setCorrect(correct);
        game.getState().useCard(card.getColor());
        card.setChecked(true);

        result.setGameState(game.getState());
        return result;
    }

    private boolean isPlayerTurn(Player player){
        return player.getTeam() == game.getState().getCurrentTeam() && player.getRole() == game.getState().getCurrentStage();
    }

    @MessageMapping("/game/question")
    public void setQuestion(@Payload String messsageText, SimpMessageHeaderAccessor headerAccessor){
        // TODO: można dodać jakąś historię podawanych haseł
        // TODO: pomyśleć, co będzie w przypadku, jeżeli szef nie zdąży podać hasła w wyznaczonym terminie
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if(player.getRole()==Role.PLAYER || player.getTeam() != game.getState().getCurrentTeam()){
            return;
        }
        Gson gson = new Gson();
        BossMessage message = buildBossMessage(messsageText, gson);
        messageManager.sendToAll(message, QUESTION_MESSAGE_RESPONSE, game);

    }

    private BossMessage buildBossMessage(@Payload String messsageText, Gson gson) {
        BossMessage message = gson.fromJson(messsageText, BossMessage.class);
        game.getState().setAnswerState(message.getWord(),message.getNumber());
        message.setGameState(game.getState());
        return message;
    }

}