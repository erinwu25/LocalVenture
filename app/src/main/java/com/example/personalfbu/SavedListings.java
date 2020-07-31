package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

        // create list and set adapter on list
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(SavedListings.this, listingList);
        rvStream.setAdapter(adapter);
        rvStream.setLayoutManager(new LinearLayoutManager(SavedListings.this));

        // dividers between recycler view items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvStream.getContext(), LinearLayoutManager.VERTICAL);
        rvStream.addItemDecoration(dividerItemDecoration);

        // fill with saved listings
        try {
            populateSaved();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateSaved() throws ParseException {
        // get saved listings from database
        Object saved = ParseUser.getCurrentUser().get("savedListings");

        // check if null and if not proceed
        if (saved == null) { return; }

        // cast to arraylist
        ArrayList<Listing> savedListings = (ArrayList<Listing>) saved;

        // populate recycler view with saved listings
        listingList.addAll(savedListings);
        adapter.notifyDataSetChanged();

    }
}