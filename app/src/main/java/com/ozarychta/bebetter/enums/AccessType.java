package com.ozarychta.bebetter.enums;

import android.content.Context;

import com.ozarychta.bebetter.R;

public enum AccessType implements EnumWithLabel {
    PRIVATE(R.string.private_access),
    PUBLIC(R.string.public_access),
    ALL(R.string.all);

    private Integer resourceId;

    AccessType(Integer resourceId){
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }
}
