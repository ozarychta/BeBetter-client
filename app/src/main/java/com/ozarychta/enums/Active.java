package com.ozarychta.enums;

public enum Active {
    ALL("Wszystkie"),
    ACTIVE("W trakcie"),
    INACTIVE("Nie rozpoczęte");

    private String label;

    Active(String aState) {
        label = aState;
    }

//    @Override public String toString() {
//        return label;
//    }
}
