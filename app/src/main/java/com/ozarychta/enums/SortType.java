package com.ozarychta.enums;

import android.content.Context;

import com.ozarychta.EnumWithLabel;
import com.ozarychta.R;

public enum SortType implements EnumWithLabel {
    RANKING_POINTS_DESC(R.string.rp_desc),
    RANKING_POINTS_ASC(R.string.rp_asc),
    HIGHEST_STREAK_DESC(R.string.hs_desc),
    HIGHEST_STREAK_ASC(R.string.hs_asc);

    private Integer resourceId;

    SortType(Integer resourceId){
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }
}
