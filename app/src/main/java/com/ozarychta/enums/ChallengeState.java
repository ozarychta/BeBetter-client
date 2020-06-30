package com.ozarychta.enums;

import android.content.Context;

import com.ozarychta.R;
import com.ozarychta.EnumWithLabel;

public enum ChallengeState implements EnumWithLabel {
    STARTED(R.string.state_started),
    NOT_STARTED_YET(R.string.state_not_started),
    FINISHED(R.string.state_finished),
    NOT_FINISHED_YET(R.string.state_not_finished),
    ALL(R.string.all);

    private Integer resourceId;

    ChallengeState(Integer resourceId){
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }
}
