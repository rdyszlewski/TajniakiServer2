package com.parabbits.tajniakiserver.summary.messages;

import com.parabbits.tajniakiserver.game.end_game.EndGameHelper;
import com.parabbits.tajniakiserver.game.end_game.EndGameInfo;
import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.summary.SummaryCard;
import com.parabbits.tajniakiserver.summary.SummaryEntry;

import java.util.ArrayList;
import java.util.List;

public class SummaryMessageCreator {

    public static SummaryMessage create(Game game){
        SummaryMessage message = new SummaryMessage();
        EndGameInfo info = EndGameHelper.getEndGameInfo(game);
        message.setWinner(info.getWinner());
        message.setCause(info.getCause());
        message.setBluePoints(game.getState().getPointsBlue());
        message.setRedPoints(game.getState().getPointsRed());
        message.setBlueRemaining(game.getState().getRemainings(Team.BLUE));
        message.setRedRemaining(game.getState().getRemainings(Team.RED));
        message.setProcess(game.getHistory().getEntries());
        message.setCards(prepareSummaryCards(game));
        return message;
    }

    private static List<SummaryCard> prepareSummaryCards(Game game){
        List<SummaryCard> results = new ArrayList<>();
        List<Card> cards = game.getCards(true);
        for(Card card: cards){
            SummaryCard summaryCard = new SummaryCard(card.getId(), card.getWord(), card.getColor());
            SummaryEntry summaryEntry = game.getHistory().getEntries().stream().filter(
                    entry->entry.getAnswers().stream().anyMatch(answer->answer.getWord().equals(card.getWord()))).findAny().get();
            summaryCard.setTeam(summaryEntry.getTeam());
            summaryCard.setQuestion(summaryEntry.getQuestion());
            results.add(summaryCard);
        }
        return results;
    }
}
