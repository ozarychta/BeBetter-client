package com.ozarychta.enums;

public enum Category {
    ALL("All"),
    SPORT("Sport"),
    FAMILY("Family"),
    EDUCATION("Education"),
    WORK("Work"),
    LIFESTYLE("Lifestyle"),
    HEALTH("Health");

    private String label;

    Category(String label) {
        this.label = label;
    }

    @Override public String toString() {
        return label;
    }
}
