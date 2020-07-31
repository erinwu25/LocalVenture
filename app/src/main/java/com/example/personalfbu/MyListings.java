package com.example.personalfbu;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyListings extends AppCompatActivity {

    private RecyclerView rvStream;
    private ListingAdapter adapter;
    private List<Listing> listingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stream);

        // find elements
        rvStream = findViewById(R.id.rvStream);

        // create list and set adapter on list
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(MyListings.this, listingList);
        rvStream.setAdapter(adapter);
        rvStream.setLayoutManager(new LinearLayoutManager(MyListings.this));

        // dividers between recycler view items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvStream.getContext(), LinearLayoutManager.VERTICAL);
        rvStream.addItemDecoration(dividerItemDecoration);

        // get all listings that are by the currently logged in user
        queryMyListings();

    }

    // get all listings that are by the currently logged in user
    private void queryMyListings() {
        // specify which class to query
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);

        // include data referred by user key
        query.include(Listing.KEY_USER);

        // filter by posts from the current user
        query.whereEqualTo(Listing.KEY_USER, ParseUser.getCurrentUser());

        // Limit query to last 20 listings
        query.setLimit(20);

        // order listings by creation date descending
        query.addDescendingOrder(Listing.KEY_CREATED_KEY);

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
                if (e != null) {
                    // log issue getting listings
                    return;
                }
                // log that i'm getting data

                listingList.addAll(listings);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
