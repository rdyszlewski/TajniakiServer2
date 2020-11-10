package com.parabbits.tajniakiserver.words;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordsHelper {
    public static List<String> readWords(String filepath) throws IOException {
        List<String> resultWords = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
        String line;
        while((line = bufferedReader.readLine()) != null){
            resultWords.add(line);
        }
        bufferedReader.close();
        return resultWords;
    }

    public static List<String> randomWords(List<String> allWords, int numberOfWords){
        Collections.shuffle(allWords);
        return allWords.subList(0, numberOfWords);
    }
}