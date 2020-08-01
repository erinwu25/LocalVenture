package com.example.personalfbu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Button btnReturnSearch, btnFilterStart, btnFilterEnd;
    String placeName, lat, lng;
    RadioGroup rgDist;
    RadioButton rb5m, rb10m, rb25m, rb50m, rb100m;
    TextView tvFilterStart, tvFilterEnd;
    boolean startCurrent = false;
    boolean endCurrent = false;
    Date startDateResult, endDateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // locate elements
        btnReturnSearch = findViewById(R.id.btnReturnSearch);
        rgDist = findViewById(R.id.rgDist);
        rb5m = findViewById(R.id.rb5m);
        rb10m = findViewById(R.id.rb10m);
        rb25m = findViewById(R.id.rb25m);
        rb50m = findViewById(R.id.rb50m);
        rb100m = findViewById(R.id.rb100m);
        tvFilterStart = findViewById(R.id.tvFilterStart);
        tvFilterEnd = findViewById(R.id.tvFilterEnd);
        btnFilterStart = findViewById(R.id.btnFilterStart);
        btnFilterEnd = findViewById(R.id.btnFilterEnd);

        // Initialize the SDK
        Places.initialize(SearchActivity.this, getResources().getString(R.string.places_sdk_key));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // set type filter for cities
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // set hint for the search bar
        autocompleteFragment.setHint("Enter a city to search");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // handle location data
                placeName = place.getName();
                lat = String.valueOf(place.getLatLng().latitude);
                lng = String.valueOf(place.getLatLng().longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("SearchActivity", "an error occurred while getting place data: " + status);
            }
        });

        // click listeners for selecting dates
        btnFilterStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment startPicker = new DatePickerFragment();
                startPicker.show(getSupportFragmentManager(), "startPicker");
                startCurrent = true;
            }
        });

        btnFilterEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment startPicker = new DatePickerFragment();
                startPicker.show(getSupportFragmentManager(), "endPicker");
                endCurrent = true;
            }
        });

        // submit search
        btnReturnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placeName == null) {
                    Toast.makeText(SearchActivity.this, "Enter a city to search", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startDateResult != null) {
                    if (!((startDateResult != null) == (endDateResult != null))) {
                        Toast.makeText(SearchActivity.this, "Please enter a start and end date ", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (startDateResult.after(endDateResult) && (!(startDateResult.compareTo(endDateResult) == 0))) {
                        Toast.makeText(SearchActivity.this, "End date must be at least the same or after start date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String dist = String.valueOf(getDistance());
                Intent backToMain = new Intent(SearchActivity.this, MainActivity.class);
                backToMain.putExtra("location", placeName);
                backToMain.putExtra("lat", lat);
                backToMain.putExtra("lng", lng);
                backToMain.putExtra("dist", dist);
                if (startDateResult!=null) {
                    backToMain.putExtra("startDate", startDateResult.getTime());
                    backToMain.putExtra("endDate", endDateResult.getTime());
                }
                setResult(99, backToMain);
                finish();
            }
        });
    }

    // get the chosen distance
    private int getDistance(){
        int distanceNum = rgDist.getCheckedRadioButtonId();
        if (distanceNum == rb5m.getId()) { return 5; }
        else if (distanceNum == rb10m.getId()) { return 10; }
        else if (distanceNum == rb25m.getId()) { return 25; }
        else if (distanceNum == rb100m.getId()) { return 100; }
        else { return 50; }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        String date = DateFormat.getDateInstance().format(cal.getTime());
        if (startCurrent) {
            tvFilterStart.setText(date);
            startDateResult = cal.getTime();
            startCurrent = false;
        }
        if (endCurrent) {
            tvFilterEnd.setText(date);
            endDateResult = cal.getTime();
            endCurrent = false;
        }
    }
}