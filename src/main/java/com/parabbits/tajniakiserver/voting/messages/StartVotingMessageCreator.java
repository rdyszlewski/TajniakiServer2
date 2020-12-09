package com.parabbits.tajniakiserver.voting.messages;

import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.voting.service.Voting;

public class StartVotingMessageCreator {

    public static StartVotingMessage create(Team team, Voting voting, int votingTime) {
        StartVotingMessage votingMessage = new StartVotingMessage();
        votingMessage.setTime(votingTime);
        votingMessage.setPlayers(voting.getCandidates(team));
        return votingMessage;
    }
}
