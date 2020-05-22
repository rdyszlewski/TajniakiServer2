package com.parabbits.tajniakiserver.voting;

import com.parabbits.tajniakiserver.shared.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.GameStep;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
public class VotingController {

    private final String VOTING_START = "/boss/start";
    private final String VOTING_VOTE = "/boss/vote";
    private final String VOTING_END = "/boss/end";
    private final String VOTING_TIMER = "/boss/timer";

    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    private void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/boss/start")
    public void startVoting(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        game.getState().setCurrentStep(GameStep.VOTING); // TODO: to też może znajdować się w innym miejscu
        game.startVoting(); // TODO: zrobić to lepiej, żeby wykonywało się to tylko raz
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Team team = player.getTeam();

        List<VotingPlayer> candidates = game.getVotingService(team).getCandidates();
        messageManager.send(candidates, player.getSessionId(), VOTING_START);

        if(!game.isVotingTimerStarted()){
            game.setVotingTimerStarted(true);
            startTimer();
        }
        // TODO: uruchomienie licznika
    }

    private void startTimer(){
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        int time = 15; // TODO: ustawić poprawny czas
        scheduler.scheduleAtFixedRate(new Runnable() {

            private int votingTime = time;

            @Override
            public void run() {
                messageManager.sendToAll(votingTime, VOTING_TIMER, game);
                System.out.println(votingTime);
                votingTime --;
                if(votingTime==-1){
//                    endVoting(); // TODO: koniecznie to odkomentować
                    scheduler.shutdown();
                }
            }
        }, 1,1, TimeUnit.SECONDS);
    }



    @MessageMapping("/boss/vote")
    public void vote(@Payload long id, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Player votedPlayer = game.getPlayerById(id);
        VotingService voting = game.getVotingService(player.getTeam());
        List<VotingPlayer> playersToUpdate = voting.vote(player.getSessionId(), votedPlayer.getSessionId());
        if(playersToUpdate != null){
            messageManager.sendToTeam(playersToUpdate, player.getTeam(), VOTING_VOTE, game);
        }
    }

    public void endVoting(){
        messageManager.sendToAll("END", VOTING_END, game);
        setRoles();

    }

    // TODO: przenieść to do innej klasy
    private void setRoles(){
        VotingPlayer redBoss = game.getVotingService(Team.RED).getWinner();
        VotingPlayer blueBoss = game.getVotingService(Team.BLUE).getWinner();

        for(Player player: game.getPlayers()){
            if(player.getId()==redBoss.getId() || player.getId()==blueBoss.getId()){
                player.setRole(Role.BOSS);
            } else {
                player.setRole(Role.PLAYER);
            }
        }
    }
}
