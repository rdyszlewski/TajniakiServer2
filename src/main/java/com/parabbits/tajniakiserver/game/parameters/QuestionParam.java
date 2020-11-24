package com.parabbits.tajniakiserver.game.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parabbits.tajniakiserver.shared.parameters.IdParam;

public class QuestionParam extends IdParam {

    @JsonProperty("question")
    private String question;

    @JsonProperty("number")
    private int number;

    public String getQuestion() {
        return question;
    }

    public int getNumber() {
        return number;
    }
}
