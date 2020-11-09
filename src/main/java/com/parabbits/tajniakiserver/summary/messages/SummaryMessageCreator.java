package com.parabbits.tajniakiserver.summary.messages;

import com.parabbits.tajniakiserver.game.end_game.EndGameHelper;
import com.parabbits.tajniakiserver.game.end_game.EndGameInfo;
import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.SummaryAnswer;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.summary.SummaryCard;
import com.parabbits.tajniakiserver.summary.SummaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SummaryMessageCreator {

    public static SummaryMessage create(Game game){
        SummaryMessage message = new SummaryMessage();
        EndGameInfo info = EndGameHelper.getEndGameInfo(game);
        message.setWinner(info.getWinner());
        message.setCause(info.getCause());
        message.setBluePoints(game.getState().getPointsBlue());
        message.setRedPoints(game.getState().getPointsRed());
        message.setBlueRemaining(game.getState().getRemainingBlue());
        message.setRedRemaining(game.getState().getRemainingRed());
        message.setProcess(game.getHistory().getEntries());
        message.setCards(prepareSummaryCards(game));
        return message;
    }

    private static List<SummaryCard> prepareSummaryCards(Game game){
        // TODO: zrobić refaktoryzację
        List<SummaryCard> results = new ArrayList<>();
        List<Card> cards = game.getCards(true);
        for(Card card: cards){
            SummaryCard summaryCard = new SummaryCard();
            summaryCard.setId(card.getId());
            summaryCard.setWord(card.getWord());
            summaryCard.setColor(card.getColor());

            for(SummaryEntry entry : game.getHistory().getEntries()){
                for(SummaryAnswer answer: entry.getAnswers()){
                    if(answer.getWord().equals(card.getWord())){
                        summaryCard.setTeam(entry.getTeam());
                        summaryCard.setQuestion(entry.getQuestion());
                    }
                }
            }
            results.add(summaryCard);
        }
        return results;
    }
}
