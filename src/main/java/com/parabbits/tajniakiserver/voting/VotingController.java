package com.parabbits.tajniakiserver.voting;

import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.shared.timer.TimerService;
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

    private final int VOTING_TIME = 20; // TODO: przenieść to do odpowiedniego miejsca

    @Autowired
    private Game game;

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
    public void startVoting(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        if(!isCorrectStep()){
            return;
        }
        setVotingStep(game);
        game.startVoting(); // TODO: zrobić to lepiej, żeby wykonywało się to tylko raz
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Team team = player.getTeam();

        StartVotingMessage votingMessage = createStartVotingMessage(team);
        messageManager.send(votingMessage, player.getSessionId(), VOTING_START);

        // TODO: przepisać to odpowiednio
//        if(!game.isVotingTimerStarted()){
//            game.setVotingTimerStarted(true);
//            startTimer();
//        }
    }

    private StartVotingMessage createStartVotingMessage(Team team) {
        StartVotingMessage votingMessage = new StartVotingMessage();
        // TODO: tutaj powinno być pobieranie aktualnego czasu. Być może nie będzie to konieczne
        votingMessage.setTime(VOTING_TIME);
        votingMessage.setPlayers(game.getVoting().getCandidates(team));
        return votingMessage;
    }

    private boolean isCorrectStep(){
        return game.getState().getCurrentStep().equals(GameStep.LOBBY) || game.getState().getCurrentStep().equals(GameStep.VOTING);
    }

    private void setVotingStep(Game game ){
        game.getState().setCurrentStep(GameStep.VOTING);
    }

    private void startTimer(){
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // TODO: zrobić oddzielną klasę dziedziczącą po Runnable z tym zadaniem
        scheduler.scheduleAtFixedRate(new Runnable() {
            private int votingTime = VOTING_TIME;

            @Override
            public void run() {
                System.out.println(votingTime);
                messageManager.sendToAll(votingTime, VOTING_TIMER, game);
                votingTime --;
                // TODO: to też jakoś ogarnać
//                if(votingTime==-1 || !game.isVotingTimerStarted()){
//                    endVoting();
//                    scheduler.shutdown();
//                }
            }
        }, 1,1, TimeUnit.SECONDS);
    }

    @MessageMapping("/boss/vote")
    public void vote(@Payload long id, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Player votedPlayer = game.getPlayers().getPlayerById(id);
        List<VotingPlayer> playersToUpdate = game.getVoting().vote(player.getSessionId(), votedPlayer.getSessionId(), player.getTeam());
        // TODO: można zrobić tak, że graczowi wysyłany jest dodatkowo komunikat o zaznaczeniu
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
        VotingPlayer redBoss = game.getVoting().getWinner(Team.RED);
        VotingPlayer blueBoss = game.getVoting().getWinner(Team.BLUE);
        if(redBoss==null || blueBoss==null){
            // TODO: to później usunąć. Potrzebne do testowania widoku
            redBoss = blueBoss;
            // TODO: wysłać informacje o błędzie
//            return; // TODO: odkomentować to
        }
        for(Player player: game.getPlayers().getAllPlayers()){
            if(player.getId()==blueBoss.getId()){
                player.setRole(Role.BOSS);
            } else {
                player.setRole(Role.PLAYER);
            }

            if(player.getId()==redBoss.getId() || player.getId()==blueBoss.getId()){
                player.setRole(Role.BOSS);
            } else {
                player.setRole(Role.PLAYER);
            }
        }
    }
}
