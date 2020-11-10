package com.parabbits.tajniakiserver.game;

public enum UseCardResult {
    CORRECT,
    INCORRECT,
    LAST_CORRECT,
    LAST_INCORRECT,
    KILLER;

    public static UseCardResult getResult(ClickCorrectness correctness, boolean endGame){
        switch (correctness){
            case CORRECT:
                return endGame ? LAST_CORRECT: CORRECT;
            case INCORRECT:
                return endGame ? LAST_INCORRECT: INCORRECT;
            case KILLER:
                return KILLER;
            default:
                throw new IllegalArgumentException();
        }
    }
}
