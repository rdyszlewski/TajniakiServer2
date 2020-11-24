package com.parabbits.tajniakiserver.shared.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BoolParam extends IdParam {

//    @JsonProperty("gameId")
//    private UUID gameId;

    @JsonProperty("value")
    private boolean value;

//    public UUID getGameId(){
//        return gameId;
//    }

    public boolean getValue() {
        return value;
    }
}
