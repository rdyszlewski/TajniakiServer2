package com.parabbits.tajniakiserver.shared.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class IdParam {

    @JsonProperty("gameId")
    private UUID id;

    public UUID getId(){
        return id;
    }
}
