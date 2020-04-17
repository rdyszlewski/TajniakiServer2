package com.parabbits.tajniakiserver.game;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.parabbits.tajniakiserver.connection.HeaderUtils;
import com.parabbits.words_utils.TeamsRandom;
import com.parabbits.words_utils.WordsHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameController{

    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/start")
    // @SendTo("/words")
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
        message.setRole(role);
        message.setWords(game.getWords());
        message.setFirstGroup(game.getFirstTeam());
        return message;
    }

    private StartGameMessage createStartMessageForPlayer(){
        return createStartGameMessage(Role.PLAYER);
    }


}