package com.ozarychta.enums;

public enum RepeatPeriod {
    ALL(0, "All"),
    ONCE_PER_WEEK(1, "1 per week"),
    TWICE_PER_WEEK(2, "2 per week"),
    THREE_PER_WEEK(3, "3 per week"),
    FOUR_PER_WEEK(4, "4 per week"),
    FIVE_PER_WEEK(5, "5 per week"),
    SIX_PER_WEEK(6, "6 per week"),
    EVERYDAY(7, "7 per week");

    private Integer timesPerWeek;
    private String label;

    RepeatPeriod(Integer timesPerWeek, String label) {
        this.timesPerWeek = timesPerWeek;
        this.label = label;
    }

    @Override public String toString() {
        return label;
    }

    public Integer getTimesPerWeek() {
        return timesPerWeek;
    }
}
