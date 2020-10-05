package com.ozarychta.bebetter.enums;

import android.content.Context;

public enum MoreOrLess implements EnumWithLabel {
    MORE(true),
    LESS( false);

    private Boolean booleanValue;

    MoreOrLess(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    @Override
    public String getLabel(Context context) {
        return booleanValue.toString();
    }
}
