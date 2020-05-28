package com.parabbits.tajniakiserver.summary;

import com.parabbits.tajniakiserver.game.end_game.EndGameHelper;
import com.parabbits.tajniakiserver.game.end_game.EndGameInfo;
import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.shared.Game;
import com.parabbits.tajniakiserver.shared.GameStep;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SummaryController {

    private final String SUMMARY_PATH = "/queue/summary/summary";

    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/summary/summary")
    public void getSummary(@Payload String message, SimpMessageHeaderAccessor headerAccessor) throws IOException {
        if(!isCorrectStep(game)){
            return;
        }
        setSummaryStep(game);

        Player player = game.getPlayer(headerAccessor.getSessionId());
        game.usePlayer(player);
//        SummaryMessage summaryMessage = getMockSummaryMessage();
        SummaryMessage summaryMessage = createSummaryMessage(game);
        messageManager.send(summaryMessage, player.getSessionId(), SUMMARY_PATH);

        if(game.areAllPlayerUsed()){
            SummarySaver.saveHistory(game.getHistory(), "/media/roman/414054776F940E4C/TajniakiOutput/" + new Date().toString() + ".txt");
            game.getState().setCurrentStep(GameStep.LOBBY);
            game.reset();
            game.clearUsedPlayer();
            // TODO: użycie tego w tym miejscu może powodować niezapisanie się podsumowania. np. jeżeli jakiś gracz zamknie aplikacje
        }
    }

    private boolean isCorrectStep(Game game){
        return game.getState().getCurrentStep().equals(GameStep.GAME) || game.getState().getCurrentStep().equals(GameStep.SUMMARY);
    }

    private void setSummaryStep(Game game){
        game.getState().setCurrentStep(GameStep.SUMMARY);
    }


    private SummaryMessage createSummaryMessage(Game game){
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

    private SummaryMessage getMockSummaryMessage(){
        SummaryMessage message = new SummaryMessage();
        message.setWinner(Team.BLUE);
        message.setBlueRemaining(0);
        message.setRedRemaining(4);
        message.setProcess(getMockSummaryEntries());
        message.setCause(EndGameCause.ALL_FOUND);
        message.setCards(getMockSummaryCards());
        return message;
    }

    private List<SummaryCard> getMockSummaryCards(){
        SummaryCard card1 = new SummaryCard(1, "Pies", CardColor.KILLER, null, null);
        SummaryCard card2 = new SummaryCard(2, "Kot", CardColor.RED, Team.BLUE, "Kaszanka");
        SummaryCard card3 = new SummaryCard(3, "Osioł", CardColor.NEUTRAL, Team.RED, "Siema");
        SummaryCard card4 = new SummaryCard(4, "Dwa", CardColor.BLUE, Team.BLUE, "Dwa");
        SummaryCard card5 = new SummaryCard(5, "Trzy", CardColor.NEUTRAL, null, null);
        return Arrays.asList(card1, card2, card3, card4, card5);
    }



    private List<SummaryCard> prepareSummaryCards(Game game){
        List<SummaryCard> results = new ArrayList<>();
        List<Card> cards = game.getBoard().getCards().stream().filter(x->x.getId() >=0).collect(Collectors.toList());
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



    private List<SummaryEntry> getMockSummaryEntries(){
        SummaryEntry entry1 = new SummaryEntry();
        entry1.setQuestion("Kaszanka");
        entry1.setNumber(3);
        entry1.addAnswer("Osioł", CardColor.BLUE);
        entry1.addAnswer("Kaszana", CardColor.NEUTRAL);
        entry1.setTeam(Team.BLUE);

        SummaryEntry entry2 = new SummaryEntry();
        entry2.setQuestion("Osiem");
        entry2.setNumber(2);
        entry2.addAnswer("Jeden", CardColor.RED);
        entry2.addAnswer("Dwa", CardColor.BLUE);
        entry2.setTeam(Team.RED);

        SummaryEntry entry3 = new SummaryEntry();
        entry3.setQuestion("Radar");
        entry3.setNumber(1);
        entry3.addAnswer("Talerz", CardColor.RED);
        entry3.setTeam(Team.BLUE);

        return Arrays.asList(entry1, entry2, entry3);
    }

}
