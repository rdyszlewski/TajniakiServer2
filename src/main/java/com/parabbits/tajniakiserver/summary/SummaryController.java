package com.parabbits.tajniakiserver.summary;

import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.shared.parameters.IdParam;
import com.parabbits.tajniakiserver.shared.state.StateControl;
import com.parabbits.tajniakiserver.summary.messages.SummaryMessage;
import com.parabbits.tajniakiserver.summary.messages.SummaryMessageCreator;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;

@Controller
public class SummaryController {

    private final String SUMMARY_PATH = "/queue/summary/summary";

    @Autowired
    private GameManager gameManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/summary/summary")
    public void getSummary(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) throws IOException {
        Game game = gameManager.findGame(param.getGameId());
        if(!StateControl.isCorrectState(GameStep.SUMMARY, true, game)){
            return;
        }
        game.getState().setCurrentStep(GameStep.SUMMARY);

        SummaryMessage summaryMessage = SummaryMessageCreator.create(game);
        messageManager.send(summaryMessage, headerAccessor.getSessionId(), SUMMARY_PATH);

        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        game.getPlayers().usePlayer(player);
        if(game.getPlayers().areAllPlayerUsed()){
            handleEndGame(game);
        }
    }

    private void handleEndGame(Game game) throws IOException {
        // TODO: zrobić to raczej w ten sposób, że zapisuje od razu po skończeniu rozgrywki (może być w GameController)
        // TODO: ścieżkę przenieść gdzieś do konfiguracji
        SummarySaver.saveHistory(game.getHistory(), "/output/" + new Date().toString() + ".txt");
        game.getState().setCurrentStep(GameStep.LOBBY);
        game.reset();
        game.getPlayers().clearUsedPlayer(); // TODO: to powinno znaleźć się w reset
        // TODO: przemyśleć, jak powinna wyglądać ponowna rozgrywka
    }
}
