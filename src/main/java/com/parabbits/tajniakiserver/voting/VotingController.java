package com.parabbits.tajniakiserver.voting;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.shared.messeges.Message;
import com.parabbits.tajniakiserver.shared.parameters.IdParam;
import com.parabbits.tajniakiserver.shared.parameters.LongParam;
import com.parabbits.tajniakiserver.shared.state.StateControl;
import com.parabbits.tajniakiserver.shared.timer.ITimerCallback;
import com.parabbits.tajniakiserver.shared.timer.TimerService;
import com.parabbits.tajniakiserver.utils.MessageManager;
import com.parabbits.tajniakiserver.voting.messages.StartVotingMessage;
import com.parabbits.tajniakiserver.voting.messages.StartVotingMessageCreator;
import com.parabbits.tajniakiserver.voting.messages.VotingManager;
import com.parabbits.tajniakiserver.voting.service.Voting;
import com.parabbits.tajniakiserver.voting.service.VotingPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class VotingController {

    private final String VOTING_START = "/voting/start";
    private final String VOTING_VOTE = "/voting/vote";
    private final String VOTING_END = "/voting/end";
    private final String VOTING_TIMER = "/voting/timer";

    private final int VOTING_TIME = 10;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private VotingManager votingManager;

    @Autowired
    private TimerService timerService;

    @Autowired
    private MessageManager messageManager;


    @MessageMapping("/voting/start")
    public void startVoting(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        if (!StateControl.isCorrectState(GameStep.VOTING, true, game)) {
            return;
        }
        Voting voting = votingManager.getVoting(game);
        synchronized (game.getID()) {
            if (!voting.isStarted()) {
                game.getState().setCurrentStep(GameStep.VOTING);
                startVoting(voting, game);
            }
        }

        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        StartVotingMessage votingMessage = StartVotingMessageCreator.create(player.getTeam(), voting, VOTING_TIME);
        messageManager.send(votingMessage, player.getSessionId(), VOTING_START);
    }

    private void startVoting(Voting voting, Game game) {
        voting.startVoting();
        timerService.startTimer(game.getID(), VOTING_TIME, new ITimerCallback() {
            @Override
            public void onFinish() {
                endVoting(game, voting);
            }

            @Override
            public void onTick(Long time) {
                messageManager.sendToAll(new Message(time.toString()), VOTING_TIMER, game);
            }
        });
    }

    @MessageMapping("/voting/vote")
    public void vote(@Payload LongParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Player votedPlayer = game.getPlayers().getPlayerById(param.getValue());
        Voting voting = votingManager.getVoting(game);
        List<VotingPlayer> playersToUpdate = voting.vote(player.getSessionId(), votedPlayer.getSessionId(), player.getTeam());
        if (playersToUpdate != null) {
            messageManager.sendToTeam(playersToUpdate, player.getTeam(), VOTING_VOTE, game);
        }
    }

    public void endVoting(Game game, Voting voting) {
        PlayerRole.setRole(game, voting);
        messageManager.sendToAll(new Message("END"), VOTING_END, game);
    }
}