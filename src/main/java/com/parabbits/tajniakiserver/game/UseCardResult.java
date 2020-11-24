package com.parabbits.tajniakiserver.game;

public enum UseCardResult {
    CORRECT,
    INCORRECT,
    LAST_CORRECT,
    LAST_INCORRECT,
    KILLER;

    public static UseCardResult getResult(ClickCorrectness correctness, boolean endGame) {
        switch (correctness) {
            case CORRECT:
                return endGame ? LAST_CORRECT : CORRECT;
            case INCORRECT:
                return endGame ? LAST_INCORRECT : INCORRECT;
            case KILLER:
                return KILLER;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static ClickCorrectness getCorrectness(UseCardResult result) {
        switch (result) {
            case CORRECT:
            case LAST_CORRECT:
                return ClickCorrectness.CORRECT;
            case INCORRECT:
            case LAST_INCORRECT:
                return ClickCorrectness.INCORRECT;
            case KILLER:
                return ClickCorrectness.KILLER;
            default:
                throw new IllegalArgumentException();
        }
    }
}
