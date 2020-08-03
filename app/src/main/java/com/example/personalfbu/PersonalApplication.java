package com.example.personalfbu;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class PersonalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Listing.class);
        ParseObject.registerSubclass(Review.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Conversation.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("erin-fbu-personal") // should correspond to APP_ID env variable
                .clientKey("erinFBU")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://erin-fbu-personal.herokuapp.com/parse/").build());


    }

}
