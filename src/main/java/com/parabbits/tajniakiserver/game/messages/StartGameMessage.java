package com.parabbits.tajniakiserver.game.messages;
import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.ClientCard;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;

import java.util.List;

public class StartGameMessage {

    private Role playerRole;
    private Team playerTeam;
    private GameState gameState;
    private List<ClientCard> cards;

    public Role getPlayerRole(){
        return playerRole;
    }

    public void setPlayerRole(Role playerRole){
        this.playerRole = playerRole;
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(Team playerTeam) {
        this.playerTeam = playerTeam;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public List<ClientCard> getCards() {
        return cards;
    }

    public void setCards(List<ClientCard> cards) {
        this.cards = cards;
    }
}