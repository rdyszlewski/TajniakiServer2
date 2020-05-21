package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.ClientCard;

import java.util.List;

public class ClickMessage {

    private List<ClientCard> editedCards;
    private int pass;

    public ClickMessage(List<ClientCard> editedCards) {
        this.editedCards = editedCards;
    }

    public List<ClientCard> getEditedCards() {
        return editedCards;
    }

    public void setEditedCards(List<ClientCard> editedCards) {
        this.editedCards = editedCards;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }
}
