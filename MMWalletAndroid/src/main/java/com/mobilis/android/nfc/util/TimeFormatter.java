package com.mobilis.android.nfc.util;

import java.text.SimpleDateFormat;

/**
 * Created by lewischao on 18/05/15.
 */
public class TimeFormatter {
    private final static String DATE_FORMAT = "dd/MM/yyyy";
    private final static String TIME_FORMAT = "HH:mm:ss";
    private final static String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static SimpleDateFormat getDateFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf;
    }

    public static SimpleDateFormat getTimeFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf;
    }

    public static SimpleDateFormat getDateTimeFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        return sdf;
    }

    public static SimpleDateFormat getCustomDateTimeFormatter(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf;
    }
}
