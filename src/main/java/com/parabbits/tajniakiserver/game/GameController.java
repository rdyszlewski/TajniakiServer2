package com.parabbits.tajniakiserver.game;

import com.google.gson.Gson;
import com.parabbits.tajniakiserver.connection.HeaderUtils;

import com.parabbits.tajniakiserver.game.messages.AnswerResult;
import com.parabbits.tajniakiserver.game.messages.BossMessage;
import com.parabbits.tajniakiserver.game.messages.StartGameMessage;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.WordColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameController{

    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/start")
    public void startGame(@Payload  String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception{
        game.initializeGame();

        StartGameMessage bossMessage = createStartGameMessageForBoss();
        StartGameMessage playerMessage = createStartMessageForPlayer();

        sendGameDataToPlayers(bossMessage, playerMessage);
    }

    private void sendGameDataToPlayers(StartGameMessage bossMessage, StartGameMessage playerMessage) {
        for(Player player: game.getPlayers()){
            System.out.println(player.getSessionId());
            StartGameMessage message = player.getRole()== Role.BOSS? bossMessage : playerMessage;
            messagingTemplate.convertAndSendToUser(player.getSessionId(), "/queue/game/start", message, HeaderUtils.createHeaders(player.getSessionId()));
        }
    }

    private StartGameMessage createStartGameMessageForBoss(){
        StartGameMessage message = createStartGameMessage(Role.BOSS);
        message.setColors(game.getWordsColors());
        return message;
    }

    private StartGameMessage createStartGameMessage(Role role){
        StartGameMessage message = new StartGameMessage();
        message.setPlayerRole(role);
        message.setWords(game.getWords());
        message.setFirstTeam(game.getFirstTeam());
        return message;
    }

    private StartGameMessage createStartMessageForPlayer(){
        return createStartGameMessage(Role.PLAYER);
    }

    @MessageMapping("/game/click")
    public void answer(@Payload String word, SimpMessageHeaderAccessor headerAccessor){
        Player player = game.getPlayer(headerAccessor.getSessionId());
        WordColor color  = game.getWordColor(word);
        AnswerResult result = new AnswerResult(word, color);
        result.setCorrect(isCorrect(color, player.getTeam()));
        // TODO: obliczenie pozostałych słów do odgadnięcia
        result.setRemainingAnswers(1);
        // TODO: ustawienie stanu
        sendAnswerResultToAll(result);
    }

    private void sendAnswerResultToAll(AnswerResult result){
        for(Player player: game.getPlayers()){
            messagingTemplate.convertAndSendToUser(player.getSessionId(), "/queue/game/answer", result, HeaderUtils.createHeaders(player.getSessionId()));
        }
    }

    private boolean isCorrect(WordColor color, Team team){
        return (color == WordColor.BLUE && team == Team.BLUE) || (color == WordColor.RED && team == Team.RED);
    }

    @MessageMapping("/game/question")
    public void setQuestion(@Payload String messsageText, SimpMessageHeaderAccessor headerAccessor){
        // TODO: można dodać jakąś historię podawanych haseł
        // TODO: pomyśleć, co będzie w przypadku, jeżeli szef nie zdąży podać hasła w wyznaczonym terminie
        Player player = game.getPlayer(headerAccessor.getSessionId());
        if(player.getRole()==Role.PLAYER || player.getTeam() != game.getState().getCurrentTeam()){
            return;
        }
        assert player.getTeam() == game.getState().getCurrentTeam() && player.getRole() == Role.BOSS;
        Gson gson = new Gson();
        BossMessage data = gson.fromJson(messsageText, BossMessage.class);
        game.getState().setAnswerState(data.getNumber());

        for(Player p: game.getPlayers()){
            messagingTemplate.convertAndSendToUser(p.getSessionId(), "/queue/game/question", data, HeaderUtils.createHeaders(p.getSessionId()));
        }
    }
}