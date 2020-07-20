package com.example.personalfbu;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Review")
public class Review extends ParseObject {

    public static final String KEY_fromUser = "fromUser";
    public static final String KEY_toUser = "toUser";
    public static final String KEY_Rating = "Rating";
    public static final String KEY_Content = "Content";
    public static final String KEY_createdAt = "createdAt";

    // getters and setters
    public ParseUser getFromUser() { return getParseUser(KEY_fromUser); }
    public ParseUser getToUser() { return getParseUser(KEY_toUser); }
    public Number getRating() { return getNumber(KEY_Rating); }
    public String getReviewContent() { return getString(KEY_Content); }
    public Date getDate() { return getDate(KEY_createdAt); }

    public void setFromUser(ParseUser fromUser) { put(KEY_fromUser, fromUser); }
    public void setToUser(ParseUser toUser) { put(KEY_toUser, toUser); }
    public void setReviewContent(String reviewContent) { put(KEY_Content, reviewContent); }
    public void setRating(Number newRating, ParseUser toUser) {
        Number currentRating = toUser.getNumber("Rating");
        if(currentRating.doubleValue() >= 1){
            Number updatedRating = (Number)((currentRating.doubleValue() + newRating.doubleValue()) / 2);
            put(KEY_Rating, updatedRating);
//            toUser.put(KEY_Rating, updatedRating);
            return;
        }
        else {
            put(KEY_Rating, newRating);
//            toUser.put(KEY_Rating, newRating);
        }
    }
}
