package com.example.personalfbu;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Listing")
public class Listing extends ParseObject {
    public static final String KEY_Blurb = "Blurb";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "Creator";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_Availability = "Availability";


    // getters and setters
    public String getBlurb() {
        return getString(KEY_Blurb);
    }

    public void setBlurb (String blurb) {
        put(KEY_Blurb, blurb);
    }

//    public ParseFile getImage() {
//        return getParseFile(KEY_IMAGE);
//    }
//
//    public void setImage(ParseFile image) {
//        put(KEY_IMAGE, image);
//    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Date getKeyCreatedKey() { return getCreatedAt(); }

    public String getAvailability() { return getString(KEY_Availability); }

    public void setAvailability(String availability) { put(KEY_Availability, availability); }

}
