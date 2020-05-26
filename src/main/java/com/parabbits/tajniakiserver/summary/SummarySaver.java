package com.parabbits.tajniakiserver.summary;

import com.parabbits.tajniakiserver.game.models.SummaryAnswer;
import com.parabbits.tajniakiserver.game.models.Team;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SummarySaver {

    public static void saveHistory(GameHistory history, String outputPath) throws IOException {
        FileWriter writer = new FileWriter(outputPath);
        printWords(writer, history, Team.BLUE);
        printWords(writer, history, Team.RED);
        printKiller(writer, history);
        printProcess(writer, history);
        writer.close();
    }

    private static void printWords(FileWriter writer, GameHistory history, Team team) throws IOException {
        List<String> words = history.getWords(team);
        String teamName = getTeamName(team);
        writer.write(teamName);
        writer.write("\n");;
        for(String word: words){
            writer.write(word);
            writer.write(';');
        }
        writer.write("\n");
    }

    private static String getTeamName(Team team){
        return team==Team.BLUE? "Zieleni": "Czerwieni";
    }

    private static void printKiller(FileWriter writer, GameHistory history) throws IOException {
        writer.write("Zabójca: ");
        writer.write(history.getKiller());
        writer.write("\n");
    }

    private static void printProcess(FileWriter writer, GameHistory history) throws IOException {
        writer.write("Przebieg rozgrywki\n");
        for(SummaryEntry entry: history.getEntries()){
            writer.write(entry.getQuestion());
            writer.write("-");
            writer.write(String.valueOf(entry.getNumber()));
            writer.write(":");
            for(SummaryAnswer answer: entry.getAnswers()){
                writer.write(answer.getWord());
                writer.write("|");
                writer.write(answer.getColor().toString());
                writer.write(",");
            }
            writer.write("\n");
        }
    }
}
