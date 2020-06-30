package com.ozarychta.enums;

import android.content.Context;

import com.ozarychta.R;
import com.ozarychta.EnumWithLabel;

public enum RepeatPeriod implements EnumWithLabel {
    ALL(0, R.string.all),
    ONCE_PER_WEEK(1, R.string.repeat_1_per_week),
    TWICE_PER_WEEK(2, R.string.repeat_2_per_week),
    THREE_PER_WEEK(3, R.string.repeat_3_per_week),
    FOUR_PER_WEEK(4, R.string.repeat_4_per_week),
    FIVE_PER_WEEK(5, R.string.repeat_5_per_week),
    SIX_PER_WEEK(6, R.string.repeat_6_per_week),
    EVERYDAY(7, R.string.repeat_7_per_week);

    private Integer timesPerWeek;

    private Integer resourceId;

    RepeatPeriod(Integer timesPerWeek, Integer resourceId){
        this.timesPerWeek = timesPerWeek;
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }

    public Integer getTimesPerWeek() {
        return timesPerWeek;
    }
}
