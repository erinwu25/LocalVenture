package com.example.personalfbu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    Button btnReturnSearch;
    String placeName, lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // locate elements
        btnReturnSearch = findViewById(R.id.btnReturnSearch);

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

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // handle location data
                Log.d("SearchActivity", place.getName() + ", " + place.getLatLng().latitude);
                placeName = place.getName();
                lat = String.valueOf(place.getLatLng().latitude);
                lng = String.valueOf(place.getLatLng().longitude);
//                Log.d("SearchActivity", place.getLatLng().latitude);

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("SearchActivity", "an error occurred while getting place data: " + status);
            }
        });

        btnReturnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMain = new Intent(SearchActivity.this, MainActivity.class);
                backToMain.putExtra("location", placeName);
                backToMain.putExtra("lat", lat);
                backToMain.putExtra("lng", lng);
                setResult(99, backToMain);
                finish();
            }
        });

    }

//    public Number getLat(String latLng) {
//
//    }
}