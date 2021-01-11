package com.example.haliyikamaapp.ToolLayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils
{
    private SimpleDateFormat dateFormatDot;
    private SimpleDateFormat dateFormatDotLong;

    SimpleDateFormat mdateFormat = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss,SSSSSS'Z'");
    public Long dateLong =null;
    public Date dateDate=null;
    public static Calendar calendar =null;
    public SimpleDateFormat dateFormat = null;

    public SimpleDateFormat dateTimeFormat = null;

    public String dateNow = null;
    public String dateNowLong = null;
    public String dateNowDateString = null;

    public DateUtils()
    {
        calendar = Calendar.getInstance();
        dateFormat =new SimpleDateFormat("dd/MM/yyyy");
        dateFormatDot = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatDotLong = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        dateLong = calendar.getTime().getTime();
        dateDate =calendar.getTime();
        dateNow = asShortDateString(dateDate);
        dateNowLong = asLongDateString(dateDate);
    }

     public static Date getformattedDate(int year, int month, int day)
     {

         Date daten = null;
             SimpleDateFormat nemdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSSSSS");
             String formatedDate = nemdateFormat.format(new Date(year, month, day));
             Calendar calendarm = Calendar.getInstance();
             calendarm.set(year, month, day);
             daten= calendar.getTime();
         return daten;
     }

    public Date ConvertStringToDate(String dateStr) throws ParseException
    {
        return dateFormatDot.parse(dateStr);
    }


    public String ConvertDateToString(Date _date)
    {
        return dateFormat.format(_date);
    }
    public String ConvertDateTimeToString(Date _date)
    {
        return dateTimeFormat.format(_date);
    }

    public static String asShortDateString(final Date dt)
    {
        SimpleDateFormat dF = new SimpleDateFormat("dd.MM.yyyy");
        return dF.format(dt);
    }

    public static String asLongDateString(final Date dt)
    {
        SimpleDateFormat dF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return dF.format(dt);
    }

    public String ConvertLongStringToShortString(String dateStr) throws ParseException//2016-01-30 23:34:56.7 şeklindeki tarihi 30.01.2016 yapar(adapterlar için kullanılır)
    {
        Date date = dateTimeFormat.parse(dateStr);
        return asShortDateString(date);
    }

    public String asDateTimeString(String dateStr) throws ParseException//30.01.2016 diye girilen texti 2016-01-30 23:34:56.7 formatına çevirir(veri kayıt işlemi için kullanılır)
    {
        Date convertedDate = new Date();
        convertedDate = dateFormatDot.parse(dateStr);
        String result = dateTimeFormat.format(convertedDate);
        return result;
    }

}
