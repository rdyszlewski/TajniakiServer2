package com.parabbits.tajniakiserver.game;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.parabbits.words_utils.Group;
import com.parabbits.words_utils.GroupsRandom;
import com.parabbits.words_utils.WordsHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameController{
    
    private final String wordsPath = "resources/words.txt";
    private final int wordsInFirstGroup = 9;

    private Group startGroup;
    private String[] words;
    private int[] groupsWords;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("game/start")
    // @SendTo("/words") 
    public void startGame(String nickname) throws Exception{
        System.out.println(nickname);
        initializeGame();
        StartGameMessage bossMessage = createStartGameMessageForBoss();
        StartGameMessage playerMessage = createStartMesageForPlayer();
        messagingTemplate.convertAndSend("/boss/words", bossMessage);
        messagingTemplate.convertAndSend("/player/words", playerMessage);
    }
 
    private void initializeGame() throws IOException {
        words = getRandomWords();
        startGroup = randomFirstGroup();
        groupsWords = GroupsRandom.createGamePattern(words.length, wordsInFirstGroup, startGroup);
    }

    private Group randomFirstGroup(){
        int randValue = new Random().nextInt(100);
        if (randValue < 50){
            return Group.BLUE;
        } else {
            return Group.RED;
        }
    }
 
    private String[] getRandomWords() throws IOException{
        List<String> allWords = WordsHelper.readWords(wordsPath);
        List<String> randomWords = WordsHelper.randomWords(allWords, 25); 
        return randomWords.toArray(new String[0]);
    }

    private StartGameMessage createStartGameMessageForBoss(){
        StartGameMessage message = new StartGameMessage("START");
        message.setWords(words);
        message.setColors(groupsWords);
        message.setFirstGroup(startGroup.value);

        return message;
    }

    private StartGameMessage createStartMesageForPlayer(){
        StartGameMessage message = new StartGameMessage("START");
        message.setWords(words);
        message.setFirstGroup(startGroup.value);
        
        return message;
    }
}