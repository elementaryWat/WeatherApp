package com.gerardoaugusto.myweatherviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by GerardoAugusto on 14/1/2017.
 */

public class Weather {
    public String dayWeek;
    public String minTemp;
    public String maxTemp;
    public String humidity;
    public String description;
    public String iconUrl;
    public Weather(long timestamp, double minTemp, double maxTemp, double humidity, String description,String iconName){
        NumberFormat numberFormat=NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayWeek=convertTimeStampToDay(timestamp);
        this.minTemp=numberFormat.format(minTemp)+"\u00B0F";
        this.maxTemp=numberFormat.format(maxTemp)+"\u00B0F";
        this.humidity=NumberFormat.getPercentInstance().format(humidity/100.0);
        this.description=description;
        this.iconUrl="http://openweathermap.org/img/w/"+iconName+".png";
    }

    private String convertTimeStampToDay(long timestamp) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(timestamp*1000);
        TimeZone timeZone=TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat dateFormatter=new SimpleDateFormat("EEEE");
        return dateFormatter.format(calendar.getTime());
    }
}
