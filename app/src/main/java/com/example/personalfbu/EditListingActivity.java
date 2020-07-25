package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class EditListingActivity extends AppCompatActivity {
    TextView tvEditListingName;
    EditText etEditListingBlurb, etEditListingAvailability;
    Button btnEditListingSave;
    Listing listing;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        // locate elements in view
        tvEditListingName = findViewById(R.id.tvEditListingName);
        etEditListingBlurb = findViewById(R.id.etEditListingBlurb);
        etEditListingAvailability = findViewById(R.id.etEditListingAvailability);
        btnEditListingSave = findViewById(R.id.btnEditListingSave);

        // get current user
        currentUser = ParseUser.getCurrentUser();

        // unwrap listing
        listing = Parcels.unwrap(getIntent().getParcelableExtra(Listing.class.getSimpleName()));

        // populate elements
        tvEditListingName.setText(currentUser.getString("Name"));
        etEditListingBlurb.setText(listing.getBlurb());
//        etEditListingAvailability.setText(listing.getAvailability());

        btnEditListingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String blurb = etEditListingBlurb.getText().toString();
                if (blurb.isEmpty()) {
                    Toast.makeText(EditListingActivity.this, "Blurb cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
//                String availability = etEditListingAvailability.getText().toString();
//                if(availability.isEmpty()) {
//                    Toast.makeText(EditListingActivity.this, "Availability cannot be empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                // save changes and return
                saveListingChanges(currentUser, listing, blurb);
                Intent backToListing = new Intent(EditListingActivity.this, ListingDetails.class);
                backToListing.putExtra("blurb", blurb);
//                backToListing.putExtra("availability", availability);
                setResult(10, backToListing);
                finish();

            }
        });
    }

    private void saveListingChanges(ParseUser currentUser, Listing listing, String blurb) {
        listing.setBlurb(blurb);
        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("EditListingActivity", "Error saving listing", e);
                    Toast.makeText(EditListingActivity.this, "Error while saving listing", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EditListingActivity.this, "Successfully saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}