package com.example.personalfbu;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("RatingHolder")
public class RatingHolder extends ParseObject {
    public static final String KEY_RatingOwner = "RatingOwner";
    public static final String KEY_RatingNum = "RatingNum";

    // empty constructor
    public RatingHolder () {
    }

    // getters and setters
    public ParseUser getRatingOwner() { return getParseUser(KEY_RatingOwner); }
    public Number getRatingNum() {
        try {
            return fetchIfNeeded().getNumber(KEY_RatingNum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setRatingOwner(ParseUser ratingOwner) { put(KEY_RatingOwner, ratingOwner); }
    public void setRatingNum(Number newRating) {
        Number currentRating = getRatingNum();
        if (currentRating.doubleValue() >= 1) {
            Number updatedRating = (Number)((currentRating.doubleValue() + newRating.doubleValue()) / 2);
            put(KEY_RatingNum, updatedRating);
        }
        else {
            put(KEY_RatingNum, newRating);
        }
    }
}
