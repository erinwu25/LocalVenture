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
    public Date getWhenCreated() { return getCreatedAt(); }

    public void setFromUser(ParseUser fromUser) { put(KEY_fromUser, fromUser); }
    public void setToUser(ParseUser toUser) { put(KEY_toUser, toUser); }
    public void setReviewContent(String reviewContent) { put(KEY_Content, reviewContent); }

    // set rating in the rating holder for the user
    public void setRating(Number newRating, ParseUser toUser) {
        toUser.put("RatingAvg", newRating);
//        RatingHolder toUserHolder = (RatingHolder) toUser.getParseObject(KEY_Rating);
//        toUserHolder.setRatingNum(newRating);

    }

    public void setReviewRating(Number newRating) {
        put(KEY_Rating, newRating);
    }
}
