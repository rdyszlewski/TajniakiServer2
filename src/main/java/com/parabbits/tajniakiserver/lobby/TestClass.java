package com.parabbits.tajniakiserver.lobby;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class TestClass{
    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @JsonProperty("first")
    private String first;
    @JsonProperty("second")
    private String second;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("uuid")
    private UUID uuid;
}