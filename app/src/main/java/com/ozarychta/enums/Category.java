package com.ozarychta.enums;

import android.content.Context;

import com.ozarychta.R;
import com.ozarychta.EnumWithLabel;

public enum Category implements EnumWithLabel {
    ALL(R.string.all),
    SPORT(R.string.category_sport),
    FAMILY(R.string.category_family),
    EDUCATION(R.string.category_education),
    WORK(R.string.category_work),
    LIFESTYLE(R.string.category_lifestyle),
    HEALTH(R.string.category_health);

    private Integer resourceId;

    Category(Integer resourceId){
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }
}
