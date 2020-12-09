package com.parabbits.tajniakiserver.shared.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LongParam extends IdParam {

//    @JsonProperty("gameId")
//    private UUID gameId;

    @JsonProperty("value")
    private Long value;

//    public UUID getGameId(){
//        return gameId;
//    }

    public Long getValue() {
        return value;
    }
}
