package com.ozarychta.enums;

public enum RepeatPeriodDTO {
    ONCE_PER_WEEK(1),
    TWICE_PER_WEEK(2),
    THREE_PER_WEEK(3),
    FOUR_PER_WEEK(4),
    FIVE_PER_WEEK(5),
    SIX_PER_WEEK(6),
    EVERYDAY(7);

    private Integer timesPerWeek;

    RepeatPeriodDTO(Integer atimesPerWeek) {
        timesPerWeek = atimesPerWeek;
    }

    public Integer getTimesPerWeek() {
        return timesPerWeek;
    }
}
