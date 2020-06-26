package com.ozarychta.enums;

public enum ChallengeState {
    STARTED("Started"),
    NOT_STARTED_YET("Not started yet"),
    FINISHED("Finished"),
    NOT_FINISHED_YET("Not finished yet"),
    ALL("All");

    private String label;

    ChallengeState(String label) {
        this.label = label;
    }

    @Override public String toString() {
        return label;
    }
}
