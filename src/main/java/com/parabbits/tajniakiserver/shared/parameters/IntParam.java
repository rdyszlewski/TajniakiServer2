package com.parabbits.tajniakiserver.shared.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IntParam extends IdParam {

    @JsonProperty("value")
    private int value;

    public int getValue(){
        return value;
    }
}
