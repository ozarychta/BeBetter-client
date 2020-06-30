package com.ozarychta.enums;

import android.content.Context;

import com.ozarychta.EnumWithLabel;

public enum MoreOrLess implements EnumWithLabel {
    MORE(true),
    LESS( false);

    private Boolean booleanValue;

    MoreOrLess(Boolean booleanValue) {
        booleanValue = booleanValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    @Override
    public String getLabel(Context context) {
        return booleanValue.toString();
    }
}
