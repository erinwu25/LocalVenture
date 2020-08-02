package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SavedListings extends AppCompatActivity {

    private RecyclerView rvStream;
    private ListingAdapter adapter;
    private List<Listing> listingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stream);

        // find recycler view
        rvStream = findViewById(R.id.rvStream);

        // set visibility of views
        rvStream.setVisibility(View.VISIBLE);
        findViewById(R.id.avNoSaved).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvNoReviews).setVisibility(View.INVISIBLE);

        // create list and set adapter on list
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(SavedListings.this, listingList);
        rvStream.setAdapter(adapter);
        rvStream.setLayoutManager(new LinearLayoutManager(SavedListings.this));

        // dividers between recycler view items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvStream.getContext(), LinearLayoutManager.VERTICAL);
        Drawable verticalDivider = ContextCompat.getDrawable(SavedListings.this, R.drawable.vertical_divider);
        dividerItemDecoration.setDrawable(verticalDivider);
        rvStream.addItemDecoration(dividerItemDecoration);

        // fill with saved listings
        try {
            populateSaved();
        } catch (ParseException e) {
            Log.e("SavedListings", "Error populating list", e);
            Toast.makeText(SavedListings.this, "Error showing saved listings", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateSaved() throws ParseException {
        // get saved listings from database
        Object saved = ParseUser.getCurrentUser().get("savedListings");

        // check if null and if not proceed
        if (saved == null) {
            // set animation default
            rvStream.setVisibility(View.INVISIBLE);
            findViewById(R.id.avNoSaved).setVisibility(View.VISIBLE);
            findViewById(R.id.tvNoReviews).setVisibility(View.VISIBLE);
            return; }

        // cast to arraylist
        ArrayList<Listing> savedListings = (ArrayList<Listing>) saved;

        // check if list is empty
        if (savedListings.size() == 0) {
            // set animation default
            rvStream.setVisibility(View.INVISIBLE);
            findViewById(R.id.avNoSaved).setVisibility(View.VISIBLE);
            findViewById(R.id.tvNoReviews).setVisibility(View.VISIBLE);
            return;
        }

        // populate recycler view with saved listings
        listingList.addAll(savedListings);
        adapter.notifyDataSetChanged();

    }
}