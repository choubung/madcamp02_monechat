package com.example.monechattest.database;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// 날짜 형식을 위한 converter
public class DateTypeConverter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); //gpt코드 (240707)

    // 기존 코드
    @TypeConverter
    public Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public Date toDate(Long millisSinceEpoch) {
        return millisSinceEpoch == null ? null : new Date(millisSinceEpoch);
    }

    // String 변환 추가
    @TypeConverter
    public static String dateToString(Date date) {
        return date == null ? null : dateFormat.format(date);
    }

    @TypeConverter
    public static Date stringToDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
