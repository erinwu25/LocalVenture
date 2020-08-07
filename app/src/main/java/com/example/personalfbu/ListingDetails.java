package com.example.personalfbu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListingDetails extends AppCompatActivity implements ConfirmDeleteListing.ConfirmDeleteListingListener{

    TextView tvDetailsName, tvDetailsRating, tvDetailsBlurb, tvDetailsBio,
            tvDetailsAvailability, tvDetailsLocation, tvDetailsContact, tvDetailsDate, tvDetailsTitle, tvDetailsPrice;
    Button btnDetailsViewReviews, btnDetailsEdit, btnAddReview, btnDetailsDelete;
    Listing listing;
    ImageView ivDetailsImg, pic1, pic2, pic3, pic4, ivExpanded, btnToChat;
    List<Review> ratingResults = new ArrayList<>();
    double rating;
    Object imgArray;
    ArrayList<ParseFile> fileList;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    ParseUser currentUser, listingUser;

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
        tvDetailsTitle = findViewById(R.id.tvDetailsTitle);
        tvDetailsPrice = findViewById(R.id.tvDetailsPrice);
        btnDetailsEdit = findViewById(R.id.btnDetailsEdit);
        btnDetailsViewReviews = findViewById(R.id.btnDetailsViewReviews);
        btnAddReview = findViewById(R.id.btnAddReview);
        btnToChat = findViewById(R.id.btnToChat);
        btnDetailsDelete = findViewById(R.id.btnDetailsDelete);
        ivDetailsImg = findViewById(R.id.ivDetailsImg);
        pic1 = findViewById(R.id.pic1);
        pic2 = findViewById(R.id.pic2);
        pic3 = findViewById(R.id.pic3);
        pic4 = findViewById(R.id.pic4);
        ivExpanded = findViewById(R.id.ivExpanded);

        // get the listing
        listing = Parcels.unwrap(getIntent().getParcelableExtra(Listing.class.getSimpleName()));

        // get listing creator and current user
        final ParseUser listingCreator = listing.getUser();
        listingUser = listing.getUser();
        currentUser = ParseUser.getCurrentUser();

        // determine whether to show the edit listing button and the delete button
        // if the current user is the listing's creator, they can edit the listing, else we remove the button
        if (!currentUser.getObjectId().equals(listingCreator.getObjectId())) {
            btnDetailsEdit.setVisibility(View.GONE);
            btnDetailsDelete.setVisibility(View.GONE);
        }

        // determine whether to show add review button and chat
        if (currentUser.getObjectId().equals(listingCreator.getObjectId())){
            btnAddReview.setVisibility(View.GONE);
            btnToChat.setVisibility(View.GONE);
            findViewById(R.id.chatTitle).setVisibility(View.GONE);
        }

        // fill in details
        if (listing.getTitle() != null) { tvDetailsTitle.setText(listing.getTitle()); }
        tvDetailsName.setText(listingCreator.getString("Name"));
        tvDetailsBlurb.setText(listing.getBlurb());
        tvDetailsBio.setText(listingCreator.getString("Bio"));
        tvDetailsLocation.setText(listingCreator.getString("location"));
        tvDetailsContact.setText("Contact: "+ listingCreator.getString("emailAddress"));
        tvDetailsDate.setText(getRelativeTimeAgo(listing.getKeyCreatedKey().toString()));
        tvDetailsAvailability.setText("Available "+ listing.getAvailability());
        if (listing.getPrice() != null) {
            double price = listing.getPrice();
            DecimalFormat moneyFormat = new DecimalFormat("$0.00");
            tvDetailsPrice.setText("Price: " + moneyFormat.format(price));
        }
        else { tvDetailsPrice.setText("Price: Free"); }


        queryRatings(listingCreator);
        ivDetailsImg.setImageResource(R.drawable.ic_baseline_person_pin_24);
        ParseFile imgFile = listingCreator.getParseFile("profileImg");
        if (imgFile != null) {
            Glide.with(ListingDetails.this)
                    .load(imgFile.getUrl())
                    .circleCrop()
                    .into(ivDetailsImg);
        }
        imgArray = listing.getImages();
        if (imgArray != null) {
            fileList = (ArrayList<ParseFile>)imgArray;
            switch (fileList.size()) {
                case 4:
                    pic4.setVisibility(View.VISIBLE);
                    Glide.with(ListingDetails.this).load(((ParseFile)fileList.get(3)).getUrl()).into(pic4);
                case 3:
                    pic3.setVisibility(View.VISIBLE);
                    Glide.with(ListingDetails.this).load(((ParseFile)fileList.get(2)).getUrl()).into(pic3);
                case 2:
                    pic2.setVisibility(View.VISIBLE);
                    Glide.with(ListingDetails.this).load(((ParseFile)fileList.get(1)).getUrl()).into(pic2);
                case 1:
                    pic1.setVisibility(View.VISIBLE);
                    Glide.with(ListingDetails.this).load(((ParseFile)fileList.get(0)).getUrl()).into(pic1);
                case 0:
                    break;
            }
        }

        // click listeners for zooming in on the images
        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImage(pic1, ((ParseFile)fileList.get(0)).getUrl());
            }
        });

        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImage(pic2, ((ParseFile)fileList.get(1)).getUrl());
            }
        });

        pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImage(pic3, ((ParseFile)fileList.get(2)).getUrl());
            }
        });

        pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImage(pic4, ((ParseFile)fileList.get(3)).getUrl());
            }
        });

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);




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
                //start activity
                startActivity(toAddReview);
            }
        });

        btnToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toChat = new Intent(ListingDetails.this, ChatActivity.class);
                Bundle userBundle = new Bundle();
                userBundle.putParcelable("receiver", listingCreator);
                toChat.putExtras(userBundle);
                // start activity
                startActivity(toChat);
            }
        });

        btnDetailsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDelete();
            }
        });
    }

    //from https://developer.android.com/training/animation/zoom
    private void zoomImage(final ImageView pic, String picUrl) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) { currentAnimator.cancel(); }

        // set image resourse
//        ivExpanded.setImageResource(ic_menu_gallery);
        Glide.with(ListingDetails.this)
                .load(picUrl)
                .into(ivExpanded);

        // Calculate the starting and ending bounds for the zoomed-in image.
        final Rect startBounds = new Rect(30, 410, 311, 251);
        final Rect finalBounds = new Rect(30, 30, 310, 640);
        final Point globalOffset = new Point(30, 30);

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        pic.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        pic.setAlpha(0f);
        ivExpanded.setVisibility(View.VISIBLE);
        btnAddReview.setVisibility(View.INVISIBLE);
        btnDetailsViewReviews.setVisibility(View.INVISIBLE);
        btnDetailsDelete.setVisibility(View.INVISIBLE);
        btnDetailsEdit.setVisibility(View.INVISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        ivExpanded.setPivotX(0f);
        ivExpanded.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(ivExpanded, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(ivExpanded, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(ivExpanded, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(ivExpanded,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        ivExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(ivExpanded, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(ivExpanded,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(ivExpanded,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(ivExpanded,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pic.setAlpha(1f);
                        ivExpanded.setVisibility(View.GONE);
                        if (currentUser.getObjectId().equals(listingUser.getObjectId())) {
                            btnDetailsEdit.setVisibility(View.VISIBLE);
                            btnDetailsDelete.setVisibility(View.VISIBLE);
                        }
                        if(!currentUser.getObjectId().equals(listingUser.getObjectId())) {
                            btnAddReview.setVisibility(View.VISIBLE);
                        }
                        btnDetailsViewReviews.setVisibility(View.VISIBLE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        pic.setAlpha(1f);
                        ivExpanded.setVisibility(View.GONE);
                        if (currentUser.getObjectId().equals(listingUser.getObjectId())) {
                            btnDetailsEdit.setVisibility(View.VISIBLE);
                            btnDetailsDelete.setVisibility(View.VISIBLE);
                        }
                        if(!currentUser.getObjectId().equals(listingUser.getObjectId())) {
                            btnAddReview.setVisibility(View.VISIBLE);
                        }
                        btnDetailsViewReviews.setVisibility(View.VISIBLE);

                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

    private void openConfirmDelete() {
        ConfirmDeleteListing confirmation = new ConfirmDeleteListing();
        confirmation.show(getSupportFragmentManager(), "ConfirmDelete");
    }

    @Override
    public void onDeleteClicked() {
        listing.deleteInBackground();
        Intent backToListingStream = new Intent(ListingDetails.this, MainActivity.class);
        startActivity(backToListingStream);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if request code is the same as result code
        if((data != null) && requestCode == 10) {
            tvDetailsBlurb.setText(data.getStringExtra("blurb"));
            tvDetailsAvailability.setText("Available " + data.getStringExtra("start") + " - " + data.getStringExtra("end"));
            tvDetailsPrice.setText(data.getStringExtra("price"));
            tvDetailsTitle.setText(data.getStringExtra("title"));
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
            Log.d("ListingDetails", "Error formatting date", e);
        }

        return relativeDate;
    }

    private void averageRatings(List<Review> ratingResults) {
        // get all the rating numbers from the returned reviews
        double ratingNums = 0.0;
        int numOfReviews = ratingResults.size();
        if (numOfReviews > 0) {
            for (Review review : ratingResults) {
                ratingNums += review.getNumber(Review.KEY_Rating).doubleValue();
                rating = ratingNums/numOfReviews;
            }
        }
        else {
            rating = 0.0;
        }
        if(rating != 0.0) {
            DecimalFormat df = new DecimalFormat("#.##");
            tvDetailsRating.setText("Rating: "+ df.format(rating)+" out of 5");
        }
        else {
            tvDetailsRating.setText("No ratings yet");
        }
    }

    public void queryRatings(ParseUser user) {
        // specify which class to query
        ParseQuery<Review> query = ParseQuery.getQuery(Review.class);

        // include data referred by ratingNum?
        query.include("*");

        // filter by reviews about certain user
        query.whereEqualTo(Review.KEY_toUser, user);

        // Limit query to last 20 listings
        query.setLimit(20);

        // start an asynchronous call for Reviews
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> reviews, com.parse.ParseException e) {
                if (e != null) {
                    // log issue getting listings
                    Log.e("ListingDetails", "Error querying ratings", e);
                    return;
                }
                ratingResults.addAll(reviews);
                averageRatings(ratingResults);
            }
        });

    }
}