package com.parabbits.tajniakiserver.shared.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class StringParam extends IdParam{

    @JsonProperty("value")
    private String value;

    public String getValue(){
        return value;
    }
}
