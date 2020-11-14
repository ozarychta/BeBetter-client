package com.ozarychta.bebetter.data;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter {

    private final static DateTimeFormatter dmyFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value, dmyFormatter);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.format(dmyFormatter);
    }

}
