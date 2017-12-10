package com.tim.app.server.entry.db;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @创建者 倪军
 * @创建时间 09/12/2017
 * @描述
 */

public class DateStringConverter implements PropertyConverter<Date,String> {
    private static final String DEFAULT_FORMAT="yyyy-MM-dd HH:mm:ss";

    @Override
    public Date convertToEntityProperty(String databaseValue) {
        return convert2Date(databaseValue,DEFAULT_FORMAT);
    }

    @Override
    public String convertToDatabaseValue(Date entityProperty) {
        return convert2String(entityProperty,DEFAULT_FORMAT);
    }
    public static String convert2String(Date date,String format){
        String currentDate=null;
        try {
            SimpleDateFormat formatter=new SimpleDateFormat(format);
            currentDate=formatter.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return currentDate;
    }
    public static Date convert2Date(String day, String format) {
        if (day == null || format == null)
            return null;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            Date dt = formatter.parse(day);
            return dt;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}