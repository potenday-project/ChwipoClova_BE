package com.chwipoClova.common.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getStringDateFormat(String asisFormat, String tobeFormat, String value) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(asisFormat)).format(DateTimeFormatter.ofPattern(tobeFormat));
    }

    public static String getStringTimeFormat(String asisFormat, String tobeFormat, String value) {
        return LocalTime.parse(value, DateTimeFormatter.ofPattern(asisFormat)).format(DateTimeFormatter.ofPattern(tobeFormat));
    }

}
