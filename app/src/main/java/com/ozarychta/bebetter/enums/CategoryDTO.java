package com.ozarychta.bebetter.enums;

import android.content.Context;

import com.ozarychta.bebetter.R;

public enum CategoryDTO implements EnumWithLabel {
    SPORT(R.string.category_sport),
    FAMILY(R.string.category_family),
    EDUCATION(R.string.category_education),
    WORK(R.string.category_work),
    LIFESTYLE(R.string.category_lifestyle),
    HEALTH(R.string.category_health);

    private Integer resourceId;

    CategoryDTO(Integer resourceId){
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }
}
