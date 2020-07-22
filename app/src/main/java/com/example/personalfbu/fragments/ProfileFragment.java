package com.example.personalfbu.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.personalfbu.EditProfileActivity;
import com.example.personalfbu.MyListings;
import com.example.personalfbu.R;
import com.example.personalfbu.RatingHolder;
import com.example.personalfbu.ViewReviewsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileFragment extends Fragment {

    TextView tvProfileName, tvProfileUsername, tvProfileRating, tvProfileLocation, tvProfileBio, tvProfileEmail;
    Button btnToReviews, btnMyListings;
    ImageButton btnEditProfile;
    ImageView ivProfileImg;
    ParseFile image;
    // get current user
    final ParseUser currentUser = ParseUser.getCurrentUser();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find element in view
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        tvProfileRating = view.findViewById(R.id.tvProfileRating);
        tvProfileLocation = view.findViewById(R.id.tvProfileLocation);
        tvProfileBio = view.findViewById(R.id.tvProfileBio);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnToReviews = view.findViewById(R.id.btnToReviews);
        btnMyListings = view.findViewById(R.id.btnMyListings);
        ivProfileImg = view.findViewById(R.id.ivProfileImg);

        // populate fields
        String name = currentUser.getString("Name");
        if (name != null) {
            tvProfileName.setText(name);
        }
        else {
            tvProfileName.setText("@"+currentUser.getUsername());
        }
        tvProfileUsername.setText("@"+currentUser.getUsername());
        image = currentUser.getParseFile("profileImg");
        if(image != null) {
            Glide.with(getContext())
                    .load(image.getUrl())
//                    .transform(new RoundedCornersTransformation(15, 3))
                    .into(ivProfileImg);
        }

//        Log.d("ProfileFragment", currentUser.getObjectId());
        String currUserId = currentUser.getObjectId();

        // testing
//        final RatingHolder[] result = new RatingHolder[1];
//        ParseQuery<RatingHolder> ratingQuery = ParseQuery.getQuery(RatingHolder.class);
//        ratingQuery.include("RatingOwner");
//        ratingQuery.whereEqualTo("RatingOwner", currUserId);
//        ratingQuery.setLimit(1);
//        ratingQuery.findInBackground(new FindCallback<RatingHolder>() {
//            @Override
//            public void done(List<RatingHolder> objects, ParseException e) {
//                if (e != null) {
//                    // issue
//                    Log.e("ProfileFragment", "I hate everything", e);
//                }
//                else {
//                    result[0] = objects.get(0);
//                    Log.d("ProfileFragment", result[0].toString());
//
//
//                    Number rating = result[0].getRatingNum();
//                    if(rating.doubleValue() < 1) {
//                        tvProfileRating.setText("No ratings yet");
//                    }
//                    else {
//                        tvProfileRating.setText(String.valueOf(rating));
//                    }
//                }
//            }
//        });



        //////
        tvProfileLocation.setText("Location: " +currentUser.getString("location"));
        tvProfileBio.setText(currentUser.getString("Bio"));
        tvProfileEmail.setText("Contact: "+currentUser.getEmail());

        // buttons
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to edit profile activity
                Intent toEditProfile = new Intent(getContext(), EditProfileActivity.class);
                startActivityForResult(toEditProfile, 25);
            }
        });


        btnToReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to reviews activity
                Intent toViewReviews = new Intent(getContext(), ViewReviewsActivity.class);
                Bundle toReviewUser = new Bundle();
                toReviewUser.putParcelable("toUser", currentUser);
                toViewReviews.putExtras(toReviewUser);
                startActivity(toViewReviews);
            }
        });

        btnMyListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to my listings
                Intent toMyListings = new Intent(getContext(), MyListings.class);
                startActivity(toMyListings);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if request code is the same as result code
        if(requestCode == 25) {
            // get string extras and set textviews
                tvProfileName.setText(data.getStringExtra("name"));
                tvProfileLocation.setText(data.getStringExtra("location"));
                tvProfileBio.setText(data.getStringExtra("bio"));
                tvProfileEmail.setText(data.getStringExtra("email"));

               if( data.hasExtra("img")) {
                   Bitmap profImg = BitmapFactory.decodeByteArray(data.getByteArrayExtra("img"), 0, data.getByteArrayExtra("img").length);
                   ivProfileImg.setImageBitmap(profImg);
               }

        }
    }
}