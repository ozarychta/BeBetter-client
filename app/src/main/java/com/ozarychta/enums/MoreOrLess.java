package com.ozarychta.enums;

public enum MoreOrLess {
    MORE(true),
    LESS( false);

    private Boolean booleanValue;

    MoreOrLess(Boolean aBooleanValue) {
        booleanValue = aBooleanValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
}
