package com.ozarychta.bebetter.enums;

import android.content.Context;

import com.ozarychta.bebetter.R;

public enum ConfirmationType implements EnumWithLabel {
    CHECK_TASK(R.string.check_task),
    COUNTER_TASK(R.string.counter_task);

    private Integer resourceId;

    ConfirmationType(Integer resourceId){
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }
}
