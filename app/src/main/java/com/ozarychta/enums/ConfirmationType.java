package com.ozarychta.enums;

import android.content.Context;

import com.ozarychta.R;
import com.ozarychta.EnumWithLabel;

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
