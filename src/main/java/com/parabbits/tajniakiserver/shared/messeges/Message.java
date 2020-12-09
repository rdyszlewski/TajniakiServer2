package com.parabbits.tajniakiserver.shared.messeges;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    @JsonProperty("message")
    private final String message;

    public Message(String message) {
        this.message = message;
    }
}
