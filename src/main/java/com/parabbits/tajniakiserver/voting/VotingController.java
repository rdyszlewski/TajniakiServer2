package com.parabbits.tajniakiserver.voting;

import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.shared.parameters.IdParam;
import com.parabbits.tajniakiserver.shared.parameters.LongParam;
import com.parabbits.tajniakiserver.shared.state.StateControl;
import com.parabbits.tajniakiserver.shared.timer.ITimerCallback;
import com.parabbits.tajniakiserver.shared.timer.TimerService;
import com.parabbits.tajniakiserver.utils.MessageManager;
import com.parabbits.tajniakiserver.voting.messages.StartVotingMessage;
import com.parabbits.tajniakiserver.voting.messages.StartVotingMessageCreator;
import com.parabbits.tajniakiserver.voting.messages.VotingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
public class VotingController {

    private final String VOTING_START = "/boss/start";
    private final String VOTING_VOTE = "/boss/vote";
    private final String VOTING_END = "/boss/end";
    private final String VOTING_TIMER = "/boss/timer";

    private final int VOTING_TIME = 10;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private VotingManager votingManager;

    @Autowired
    private TimerService timerService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    private void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/boss/start")
    public void startVoting(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        if(!StateControl.isCorrectState(GameStep.VOTING, true, game)){
            return;
        }
        game.getState().setCurrentStep(GameStep.VOTING);
        // TODO: zastanowić się, kiedy powinno się włączyć odliczanie
        // TODO: tutaj powinna być jakaś synchronizacja, ponieważ wiele graczy jednocześnie może utworzyć głosowanie
        Voting voting = votingManager.getVoting(game);
        if(!voting.isStarted()){
            startVoting(voting, game);
        }

        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        StartVotingMessage votingMessage = StartVotingMessageCreator.create(player.getTeam(), voting, VOTING_TIME);
        messageManager.send(votingMessage, player.getSessionId(), VOTING_START);
    }

    private void startVoting(Voting voting, Game game){
        voting.startVoting();
        timerService.startTimer(game.getID(), VOTING_TIME, new ITimerCallback() {
            @Override
            public void onFinish() {
                endVoting(game, voting);
            }

            @Override
            public void onTick(Long time) {
                messageManager.sendToAll(time, VOTING_TIMER, game);
            }
        });
    }

    @MessageMapping("/boss/vote")
    public void vote(@Payload LongParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Player votedPlayer = game.getPlayers().getPlayerById(param.getValue());
        Voting voting = votingManager.getVoting(game);
        List<VotingPlayer> playersToUpdate = voting.vote(player.getSessionId(), votedPlayer.getSessionId(), player.getTeam());
        if(playersToUpdate != null){
            messageManager.sendToTeam(playersToUpdate, player.getTeam(), VOTING_VOTE, game);
        }
    }

    public void endVoting(Game game, Voting voting){
        PlayerRole.setRole(game, voting);
        // TODO: już tutaj można wysłąć role do graczy
        messageManager.sendToAll("END", VOTING_END, game);
    }
}
