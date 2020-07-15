package com.example.personalfbu.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.personalfbu.R;
import com.parse.ParseUser;

public class CreateFragment extends Fragment {


    public CreateFragment() {
        // Required empty public constructor
    }


    @Override
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvComposeName;
        TextView tvComposeRating;
        ImageView ivComposeProfileImg;
        EditText etComposeBlurb;
        TextView tvComposeEmail;
        EditText etComposeAvailability;
        TextView tvComposeLocation;

        // get current user to populate some fields
        ParseUser currentUser = ParseUser.getCurrentUser();

        // find elements in view
        tvComposeName = view.findViewById(R.id.tvComposeName);
        tvComposeRating = view.findViewById(R.id.tvComposeRating);
        ivComposeProfileImg = view.findViewById(R.id.ivComposeProfileImg);
        etComposeBlurb = view.findViewById(R.id.etComposeBlurb);
        tvComposeEmail = view.findViewById(R.id.tvComposeEmail);
        etComposeAvailability = view.findViewById(R.id.etCreateEmail);
        tvComposeLocation = view.findViewById(R.id.tvComposeLocation);

        // populate elements
        tvComposeName.setText(currentUser.getString("Name"));
        Number rating = currentUser.getNumber("Rating");
        if (rating.doubleValue() < 1) {
            tvComposeRating.setText("No ratings yet");
        }
        else {
            tvComposeRating.setText(String.valueOf(rating));
        }
        tvComposeEmail.setText(currentUser.getEmail());
        tvComposeLocation.setText("Area: "+currentUser.getString("location"));

    }
}