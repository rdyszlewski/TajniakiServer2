package com.parabbits.tajniakiserver.boss;

import com.parabbits.tajniakiserver.ConnectionListener;
import com.parabbits.tajniakiserver.game.Game;
import com.parabbits.tajniakiserver.game.Player;
import com.parabbits.tajniakiserver.game.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BossController {

    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/boss/start")
    public void startVoting(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        game.startVoting(); // TODO: zrobić to lepiej, żeby wykonywało się to tylko raz
//        Team team = player.getTeam(); // TODO: odkomencić to później
        Team team = Team.BLUE;

        List<BossCandidatePlayer> candidate = game.getVotingService(team).getCandidates();
        for (Player p : game.getPlayers(team)) {
            System.out.println("Wysyłam do " + p.getNickname());
            messagingTemplate.convertAndSendToUser(p.getSessionId(), "/boss/start", candidate, createHeaders(p.getSessionId()));
        }
    }

    @MessageMapping("/boss/vote")
    public void vote(@Payload long id, SimpMessageHeaderAccessor headerAccessor) {
        Player player = game.getPlayer(headerAccessor.getSessionId());
        Player votedPlayer = game.getPlayerById(id);
        VotingService voting = game.getVotingService(player.getTeam());
        voting.vote(player.getSessionId(), votedPlayer.getSessionId());

        List<BossCandidatePlayer> candidates = voting.getCandidates();
        for (Player p : game.getPlayers(player.getTeam())) {
            messagingTemplate.convertAndSendToUser(p.getSessionId(), "/boss/start", candidates, createHeaders(p.getSessionId()));
        }
    }

    // TODO: przerzucić to do oddzielnej klasy
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
