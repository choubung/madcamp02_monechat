package com.example.monechat.localdatabase;

import androidx.room.TypeConverter;
import java.util.Date;

// 날짜 형식을 위한 converter
public class DateTypeConverter {
    @TypeConverter
    public Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public Date toDate(Long millisSinceEpoch) {
        return millisSinceEpoch == null ? null : new Date(millisSinceEpoch);
    }
}