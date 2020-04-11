package com.parabbits.tajniakiserver.game;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    // TODO: pobranie mapy 
    // TODO: zaznaczanie pola
    // TODO: 

    // TODO: zrobić potrzebne metody
   
    @MessageMapping("game/start")
    @SendTo("/words")
    public StartGameMessage startGame(String nickName) throws Exception{
        System.out.println(nickName);
        StartGameMessage message = new StartGameMessage();
        String[] words = {"jeden", "dwa", "trzy", "cztery", "pięć", "sześć"};
        int[] colors = {-1, 0, 1, 0, -1, 1};
        int startGroup = 1;
        message.setWords(words);
        message.setColors(colors);
        message.setFirstGroup(startGroup);
        message.setType("START");

        return message;
    }
}