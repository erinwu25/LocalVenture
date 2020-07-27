package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditListingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView tvEditListingName, tvEditStart, tvEditEnd;
    EditText etEditListingBlurb;
    Button btnEditListingSave, btnEditStart, btnEditEnd;
    Listing listing;
    ParseUser currentUser;
    boolean startCurrent = false;
    boolean endCurrent = false;
    Date startDateResult, endDateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        // locate elements in view
        tvEditListingName = findViewById(R.id.tvEditListingName);
        etEditListingBlurb = findViewById(R.id.etEditListingBlurb);
        btnEditListingSave = findViewById(R.id.btnEditListingSave);
        tvEditStart = findViewById(R.id.tvEditStart);
        tvEditEnd = findViewById(R.id.tvEditEnd);
        btnEditStart = findViewById(R.id.btnEditStart);
        btnEditEnd = findViewById(R.id.btnEditEnd);



        // get current user
        currentUser = ParseUser.getCurrentUser();

        // unwrap listing
        listing = Parcels.unwrap(getIntent().getParcelableExtra(Listing.class.getSimpleName()));

        // populate elements
        tvEditListingName.setText(currentUser.getString("Name"));
        etEditListingBlurb.setText(listing.getBlurb());
        tvEditStart.setText(readableDate(listing.getStartDate()));
        tvEditEnd.setText(readableDate(listing.getEndDate()));

        // btn choose dates
        btnEditStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment startPicker = new DatePickerFragment();
                startPicker.show(getSupportFragmentManager(), "startPicker");
                startCurrent = true;
            }
        });

        btnEditEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment endPicker = new DatePickerFragment();
                endPicker.show(getSupportFragmentManager(), "endPicker");
                endCurrent = true;
            }
        });


        btnEditListingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String blurb = etEditListingBlurb.getText().toString();
                if (blurb.isEmpty()) {
                    Toast.makeText(EditListingActivity.this, "Blurb cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((startDateResult == null) || (endDateResult == null)) {
                    Toast.makeText(EditListingActivity.this, "Availability cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // save changes and return
                saveListingChanges(currentUser, listing, blurb);
                Intent backToListing = new Intent(EditListingActivity.this, ListingDetails.class);
                backToListing.putExtra("blurb", blurb);
                backToListing.putExtra("start", readableDate(startDateResult));
                backToListing.putExtra("end", readableDate(endDateResult));
                setResult(10, backToListing);
                finish();

            }
        });
    }

    private void saveListingChanges(ParseUser currentUser, Listing listing, String blurb) {
        listing.setBlurb(blurb);
        listing.setStartDate(startDateResult);
        listing.setEndDate(endDateResult);
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

    public String readableDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return DateFormat.getDateInstance().format(c.getTime());
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        String date = DateFormat.getDateInstance().format(cal.getTime());
        if (startCurrent) {
            tvEditStart.setText(date);
            startDateResult = cal.getTime();
            startCurrent = false;
        }
        if (endCurrent) {
            tvEditEnd.setText(date);
            endDateResult = cal.getTime();
            endCurrent = false;
        }
    }
}