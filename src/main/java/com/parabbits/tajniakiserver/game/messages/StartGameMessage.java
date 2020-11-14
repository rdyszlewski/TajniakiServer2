package com.parabbits.tajniakiserver.game.messages;
import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;

import java.util.List;

public class StartGameMessage {

    // TODO: nazwa gracza nie jest tutaj potrzebna. Wykorzystywana jest wyłącznie do testowania. Później to usunąć.
    private String nickname;
    private Role playerRole;
    private Team playerTeam;
    private GameStateMessage gameState;
    private List<ClientCard> cards;
    private List<Player> players;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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

    public GameStateMessage getGameState() {
        return gameState;
    }

    public void setGameState(GameStateMessage gameState) {
        this.gameState = gameState;
    }

    public List<ClientCard> getCards() {
        return cards;
    }

    public void setCards(List<ClientCard> cards) {
        this.cards = cards;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}