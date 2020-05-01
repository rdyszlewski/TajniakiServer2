package com.parabbits.tajniakiserver.boss;

import com.parabbits.tajniakiserver.shared.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
public class BossController {

    private final String VOTING_START = "/boss/start";
    private final String VOTING_VOTE = "/boss/vote";
    private final String VOTING_END = "/boss/end";

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
        game.startVoting(); // TODO: zrobić to lepiej, żeby wykonywało się to tylko raz
//        Team team = player.getTeam(); // TODO: odkomencić to później
        Team team = Team.BLUE;

        List<BossCandidatePlayer> candidate = game.getVotingService(team).getCandidates();
        messageManager.sendToTeam(candidate, team, VOTING_START, game);
    }

    @MessageMapping("/boss/vote")
    public void vote(@Payload long id, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Player votedPlayer = game.getPlayerById(id);
        VotingService voting = game.getVotingService(player.getTeam());
        voting.vote(player.getSessionId(), votedPlayer.getSessionId());

        List<BossCandidatePlayer> candidates = voting.getCandidates();
        messageManager.sendToTeam(candidates, player.getTeam(), VOTING_VOTE, game);
    }

    public void endVoting(){
        messageManager.sendToAll("END", VOTING_END, game);
        setRoles();

    }

    // TODO: przenieść to do innej klasy
    private void setRoles(){
        BossCandidatePlayer redBoss = game.getVotingService(Team.RED).getWinner();
        BossCandidatePlayer blueBoss = game.getVotingService(Team.BLUE).getWinner();

        for(Player player: game.getPlayers()){
            if(player.getId()==redBoss.getId() || player.getId()==blueBoss.getId()){
                player.setRole(Role.BOSS);
            } else {
                player.setRole(Role.PLAYER);
            }
        }
    }
}
