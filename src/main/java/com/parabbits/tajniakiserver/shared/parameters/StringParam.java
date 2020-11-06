package com.parabbits.tajniakiserver.shared.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class StringParam extends IdParam{

//    @JsonProperty("gameId")
//    private UUID gameId;

    @JsonProperty("value")
    private String value;

//    public UUID getGameId(){
//        return gameId;
//    }

    public String getValue(){
        return value;
    }
}
