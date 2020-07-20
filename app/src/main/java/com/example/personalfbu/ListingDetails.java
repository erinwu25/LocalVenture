package com.example.personalfbu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ListingDetails extends AppCompatActivity {

    TextView tvDetailsName, tvDetailsRating, tvDetailsBlurb, tvDetailsBio, tvDetailsAvailability, tvDetailsLocation, tvDetailsContact, tvDetailsDate;
    Button btnDetailsViewReviews, btnDetailsEdit, btnAddReview;
    Listing listing;
    Number rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        // find elements in view
        tvDetailsName = findViewById(R.id.tvDetailsName);
        tvDetailsRating = findViewById(R.id.tvDetailsRating);
        tvDetailsBlurb = findViewById(R.id.tvDetailsBlurb);
        tvDetailsBio = findViewById(R.id.tvDetailsBio);
        tvDetailsAvailability = findViewById(R.id.tvDetailsAvailability);
        tvDetailsLocation = findViewById(R.id.tvDetailsLocation);
        tvDetailsContact = findViewById(R.id.tvDetailsContact);
        tvDetailsDate = findViewById(R.id.tvDetailsDate);
        btnDetailsEdit = findViewById(R.id.btnDetailsEdit);
        btnDetailsViewReviews = findViewById(R.id.btnDetailsViewReviews);
        btnAddReview = findViewById(R.id.btnAddReview);

        // get the listing
        listing = Parcels.unwrap(getIntent().getParcelableExtra(Listing.class.getSimpleName()));

        // get listing creator and current user
        final ParseUser listingCreator = listing.getUser();
        ParseUser currentUser = ParseUser.getCurrentUser();

        // determine whether to show the edit listing button
        // if the current user is the listing's creator, they can edit the listing, else we remove the button
        if (!currentUser.getObjectId().equals(listingCreator.getObjectId())) {
            btnDetailsEdit.setVisibility(View.GONE);
        }

        // determine whether to show add review button
        if (currentUser.getObjectId().equals(listingCreator.getObjectId())){
            btnAddReview.setVisibility(View.GONE);
        }

        // fill in details
        tvDetailsName.setText(listingCreator.getString("Name"));
        rating = listingCreator.getNumber("Rating");
        if (rating.doubleValue() < 1) {
            tvDetailsRating.setText("No ratings yet");
        }
        else {
            tvDetailsRating.setText("Rating: "+ String.valueOf(rating) + " out of 5");
        }
        tvDetailsBlurb.setText(listing.getBlurb());
        tvDetailsBio.setText(listingCreator.getString("Bio"));
        tvDetailsAvailability.setText("Available "+ listing.getAvailability());
        tvDetailsLocation.setText("Location: "+ listingCreator.getString("location"));
        tvDetailsContact.setText("Contact: "+ listingCreator.getString("emailAddress"));
        tvDetailsDate.setText(getRelativeTimeAgo(listing.getKeyCreatedKey().toString()));
        Log.d("ListingDetails", listingCreator.getString("emailAddress"));

        // click listener for View Reviews
        btnDetailsViewReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toViewReviews = new Intent(ListingDetails.this, ViewReviewsActivity.class);
                // put extra for listing creator
                Bundle reviewBundle = new Bundle();
                reviewBundle.putParcelable("toUser", listingCreator);
                toViewReviews.putExtras(reviewBundle);
                startActivity(toViewReviews);
            }
        });

        btnDetailsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEditListing = new Intent(ListingDetails.this, EditListingActivity.class);
                toEditListing.putExtra(Listing.class.getSimpleName(), Parcels.wrap(listing));
                startActivityForResult(toEditListing, 10);
            }
        });

        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddReview = new Intent(ListingDetails.this, AddReview.class);
                Bundle userBundle = new Bundle();
                userBundle.putParcelable("toUser", listingCreator);
                toAddReview.putExtras(userBundle);
                //start activity for result
                startActivity(toAddReview);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if request code is the same as result code
        if(requestCode == 10) {
            tvDetailsBlurb.setText(data.getStringExtra("blurb"));
            tvDetailsAvailability.setText(data.getStringExtra("availability"));
        }
    }

    // manipulate created at
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String igFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(igFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            int flag = DateUtils.FORMAT_ABBREV_RELATIVE;
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, flag).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}