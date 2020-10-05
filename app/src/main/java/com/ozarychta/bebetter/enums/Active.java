package com.ozarychta.bebetter.enums;

public enum Active {
    ALL("Wszystkie", true),
    ACTIVE("W trakcie", true),
    INACTIVE("Nie rozpoczęte", false);

    private String label;
    private Boolean booleanValue;

    Active(String aState, Boolean aBooleanValue) {
        label = aState;
        booleanValue = aBooleanValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    //    @Override public String toString() {
//        return label;
//    }
}
