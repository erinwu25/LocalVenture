package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewReviewsActivity extends AppCompatActivity {
    RecyclerView rvReviews;
    List<Review> reviewList;
    ReviewAdapter adapter;
    ParseUser toUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        // find recycler view
        rvReviews = findViewById(R.id.rvReviews);

        // set initial views
        rvReviews.setVisibility(View.VISIBLE);
        findViewById(R.id.avNoReviews).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvNoReviews).setVisibility(View.INVISIBLE);

        // initialize a list of listings and adapter
        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(ViewReviewsActivity.this, reviewList);

        // set recyclerview on adapter
        rvReviews.setAdapter(adapter);

        // set layout manager on recycler view
        rvReviews.setLayoutManager(new LinearLayoutManager(ViewReviewsActivity.this));

        // get user
        toUser = getIntent().getExtras().getParcelable("toUser");

        // dividers between recycler view items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvReviews.getContext(), LinearLayoutManager.VERTICAL);
        rvReviews.addItemDecoration(dividerItemDecoration);

        // get reviews
        queryReviews(toUser);
    }

    private void queryReviews(ParseUser toUser) {
        // specify which class to query
        ParseQuery<Review> query = ParseQuery.getQuery(Review.class);

        // include data referred by user key
        query.include(Review.KEY_fromUser);

        // filter by reviews about certain user
        query.whereEqualTo(Review.KEY_toUser, toUser);

        // Limit query to last 20 listings
        query.setLimit(20);

        // order listings by creation date descending
        query.addDescendingOrder(Review.KEY_createdAt);

        // start an asynchronous call for Reviews
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> reviews, ParseException e) {
                if (e != null) {
                    // log issue getting listings
                    Log.e("ViewReviewsActivity", "Error querying listings", e);
                    return;
                }

                if ((reviews == null) || reviews.size() == 0) {
                    // set views if no results
                    rvReviews.setVisibility(View.INVISIBLE);
                    findViewById(R.id.avNoReviews).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvNoReviews).setVisibility(View.VISIBLE);
                }
                else {
                    // add reviews to list and populate recyclerview
                    reviewList.addAll(reviews);
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }
}