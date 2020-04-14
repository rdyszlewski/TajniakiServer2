package com.parabbits.tajniakiserver.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GameConnector {

    // TODO: w przyszłości przerobić to tak, aby obsługiwało wiele gier
    @Autowired
    private Game game;

    @MessageMapping("/game/connect")
    public void connectToGame(@Payload String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception{
        System.out.println("connectToGame");
        String sessionId = headerAccessor.getSessionId();
        System.out.println(sessionId);
        game.addPlayer(sessionId, nickname);
        game.testPrint();
    }

    public void disconnectPlayer(String sessionId) {
        game.removePlayer(sessionId);
    }
}
