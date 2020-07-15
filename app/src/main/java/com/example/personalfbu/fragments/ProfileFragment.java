package com.example.personalfbu.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.personalfbu.EditProfileActivity;
import com.example.personalfbu.R;
import com.example.personalfbu.ViewReviewsActivity;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {


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

        TextView tvProfileName, tvProfileUsername, tvProfileRating, tvProfileLocation, tvProfileBio, tvProfileEmail;
        Button btnToReviews;
        ImageButton btnEditProfile;

        // find element in view
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        tvProfileRating = view.findViewById(R.id.tvProfileRating);
        tvProfileLocation = view.findViewById(R.id.tvProfileLocation);
        tvProfileBio = view.findViewById(R.id.tvProfileBio);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnToReviews = view.findViewById(R.id.btnToReviews);

        // get current user
        ParseUser currentUser = ParseUser.getCurrentUser();

        // populate fields
        String name = currentUser.getString("Name");
        if (name != null) {
            tvProfileName.setText(name);
        }
        else {
            tvProfileName.setText("@"+currentUser.getUsername());
        }
        tvProfileUsername.setText("@"+currentUser.getUsername());
        Number rating = currentUser.getNumber("Rating");
        if(rating.doubleValue() < 1) {
            tvProfileRating.setText("No ratings yet");
        }
        else {
            tvProfileRating.setText(String.valueOf(rating));
        }
        tvProfileLocation.setText("Location: " +currentUser.getString("location"));
        tvProfileBio.setText(currentUser.getString("Bio"));
        tvProfileEmail.setText("Contact: "+currentUser.getEmail());

        // buttons
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to edit profile activity
                Intent toEditProfile = new Intent(getContext(), EditProfileActivity.class);
                startActivity(toEditProfile);
            }
        });

        btnToReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // navigate to reviews activity
                Intent toViewReviews = new Intent(getContext(), ViewReviewsActivity.class);
                startActivity(toViewReviews);
            }
        });


    }
}