package com.parabbits.tajniakiserver.game.messages;
import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.Board;
import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Role;

import java.util.List;

public class StartGameMessage {

    private Role playerRole;
    private GameState gameState;
    private List<Card> cards;

    public Role getPlayerRole(){
        return playerRole;
    }

    public void setPlayerRole(Role playerRole){
        this.playerRole = playerRole;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}