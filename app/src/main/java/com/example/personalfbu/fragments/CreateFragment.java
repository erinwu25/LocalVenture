package com.example.personalfbu.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalfbu.Listing;
import com.example.personalfbu.R;
import com.example.personalfbu.RatingHolder;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class CreateFragment extends Fragment {

    private final String TAG = "CreateFragment";
    TextView tvComposeName, tvComposeRating, tvComposeEmail, tvComposeLocation;
    ImageView ivComposeProfileImg;
    EditText etComposeBlurb, etComposeAvailability;
    Button btnComposeSubmit;
    ParseFile imgFile;

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

        // get current user to populate some fields
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // find elements in view
        tvComposeName = view.findViewById(R.id.tvComposeName);
        tvComposeRating = view.findViewById(R.id.tvComposeRating);
        ivComposeProfileImg = view.findViewById(R.id.ivComposeProfileImg);
        etComposeBlurb = view.findViewById(R.id.etComposeBlurb);
        tvComposeEmail = view.findViewById(R.id.tvComposeEmail);
        etComposeAvailability = view.findViewById(R.id.etComposeAvailability);
        tvComposeLocation = view.findViewById(R.id.tvComposeLocation);
        btnComposeSubmit = view.findViewById(R.id.btnComposeSubmit);

        // populate elements
        tvComposeName.setText(currentUser.getString("Name"));
//        RatingHolder ratingH = (RatingHolder) currentUser.getParseObject("Rating");
//        Number rating = ratingH.getRatingNum();
//        if (rating.doubleValue() < 1) {
//            tvComposeRating.setText("No ratings yet");
//        }
//        else {
//            tvComposeRating.setText(String.valueOf(rating));
//        }
        tvComposeEmail.setText(currentUser.getEmail());
        tvComposeLocation.setText("Area: "+currentUser.getString("location"));
        imgFile = currentUser.getParseFile("profileImg");
        if (imgFile != null){
            Glide.with(getContext())
                    .load(imgFile.getUrl())
                    .transform(new RoundedCornersTransformation(15, 3))
//                    .circleCrop()
                    .into(ivComposeProfileImg);
        }

        // Click listener for submit
        btnComposeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String blurb = etComposeBlurb.getText().toString();
                if (blurb.isEmpty()) {
                    Toast.makeText(getContext(), "Blurb cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String availability = etComposeAvailability.getText().toString();
                if (availability.isEmpty()) {
                    Toast.makeText(getContext(), "Please add your availability", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveListing(currentUser, blurb, availability);
            }
        });
    }

    private void saveListing(ParseUser currentUser, String blurb, String availability) {
        Listing newListing = new Listing();
        newListing.setUser(currentUser);
        newListing.setAvailability(availability);
        newListing.setBlurb(blurb);
        newListing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e != null) {
                    Log.e(TAG, "error while saving", e);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
                else {
                    // clear fields after posting
                    Toast.makeText(getContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                    etComposeAvailability.setText("");
                    etComposeBlurb.setText("");
                }
            }
        });
    }
}