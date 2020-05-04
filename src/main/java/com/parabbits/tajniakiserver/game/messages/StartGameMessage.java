package com.parabbits.tajniakiserver.game.messages;
import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;

import java.util.List;

public class StartGameMessage {

    // TODO: nazwa gracza nie jest tutaj potrzebna. Wykorzystywana jest wyłącznie do testowania. Później to usunąć.
    private String nickname;
    private Role playerRole;
    private Team playerTeam;
    private GameState gameState;
    private List<ClientCard> cards;
    private List<GamePlayer> players;

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

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }
}