package com.ozarychta.enums;

import android.content.Context;

import com.ozarychta.R;
import com.ozarychta.EnumWithLabel;

public enum AccessType implements EnumWithLabel {
    PRIVATE(R.string.private_access),
    PUBLIC(R.string.public_access),
    ALL(R.string.uppercase_all);

    private Integer resourceId;

    AccessType(Integer resourceId){
        this.resourceId = resourceId;
    }

    @Override
    public String getLabel(Context ctx){
        return ctx.getString(resourceId);
    }
}
