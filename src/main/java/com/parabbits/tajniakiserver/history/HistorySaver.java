package com.parabbits.tajniakiserver.history;

import com.parabbits.tajniakiserver.game.models.SummaryAnswer;
import com.parabbits.tajniakiserver.game.models.Team;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class HistorySaver {

    public static void saveHistory(GameHistory history, String outputPath) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outputPath);
        printWords(writer, history, Team.BLUE);
        printWords(writer, history, Team.RED);
        printKiller(writer, history);
        printProcess(writer, history);
    }

    private static void printWords(PrintWriter writer, GameHistory history, Team team){
        List<String> words = history.getWords(team);
        String teamName = getTeamName(team);
        writer.println(teamName);
        for(String word: words){
            writer.print(word);
            writer.print(';');
        }
        writer.println("");
    }

    private static String getTeamName(Team team){
        return team==Team.BLUE? "Zieleni": "Czerwieni";
    }

    private static void printKiller(PrintWriter writer, GameHistory history){
        writer.println("Zabójca");
        writer.println(history.getKiller());
    }

    private static void printProcess(PrintWriter writer, GameHistory history){
        writer.println("Przebieg rozgrywki");
        for(HistoryEntry entry: history.getEntries()){
            writer.write(entry.getQuestion());
            writer.write("-");
            writer.write(entry.getNumber());
            writer.write(":");
            for(SummaryAnswer answer: entry.getAnswers()){
                writer.write(answer.getWord());
                writer.write("|");
                writer.write(answer.getColor().toString());
                writer.write(",");
            }
        }
    }
}
