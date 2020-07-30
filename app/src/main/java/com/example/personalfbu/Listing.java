package com.example.personalfbu;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ParseClassName("Listing")
public class Listing extends ParseObject {
    public static final String KEY_Blurb = "Blurb";
    public static final String KEY_IMAGES = "ImgArray";
    public static final String KEY_USER = "Creator";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_startDate = "startDate";
    public static final String KEY_endDate = "endDate";

    // getters and setters
    public String getBlurb() {
        return getString(KEY_Blurb);
    }

    public void setBlurb (String blurb) {
        put(KEY_Blurb, blurb);
    }

    public Object getImages() {
        return get(KEY_IMAGES);
    }

    public void setImages(List imgs) {
        put(KEY_IMAGES, imgs);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Date getKeyCreatedKey() { return getCreatedAt(); }

    public String getAvailability() {
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(getStartDate());
        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(getEndDate());
        String startDate = DateFormat.getDateInstance().format(cStart.getTime());
        String endDate = DateFormat.getDateInstance().format(cEnd.getTime());
        return startDate + " - " + endDate;
    }

    public Date getStartDate() { return getDate(KEY_startDate); }

    public void setStartDate(Date startDate) { put(KEY_startDate, startDate); }

    public Date getEndDate() { return getDate(KEY_endDate); }

    public void setEndDate(Date endDate) { put(KEY_endDate, endDate); }



}
