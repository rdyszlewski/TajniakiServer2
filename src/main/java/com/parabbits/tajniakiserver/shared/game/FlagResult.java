package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Player;

public class FlagResult {

    private Card card;
    private Player player;

    public FlagResult(Card card, Player player){
        this.card = card;
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
