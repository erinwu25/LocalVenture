package com.example.personalfbu.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalfbu.Listing;
import com.example.personalfbu.ListingAdapter;
import com.example.personalfbu.MainActivity;
import com.example.personalfbu.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class StreamFragment extends Fragment {

    public RecyclerView rvStream;
    public ListingAdapter adapter;
    public List<Listing> listingList;
    public List<Listing> masterList;
    private MainActivity mainActivity = (MainActivity)getActivity();

    // Required empty public constructor
    public StreamFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find recycler view
        rvStream = view.findViewById(R.id.rvStream);

        // initialize a list of listings and adapter
        masterList = new ArrayList<>();
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(getContext(), listingList);

        // set adapter on listingList
        rvStream.setAdapter(adapter);

        // set layout manager on recycler view
        rvStream.setLayoutManager(new LinearLayoutManager(getContext()));

        // dividers between recycler view items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvStream.getContext(), LinearLayoutManager.VERTICAL);
        rvStream.addItemDecoration(dividerItemDecoration);

        // query listings
        queryListings();
    }

    private void queryListings() {
        // specify which class to query
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);

        // include data referred by user key
        query.include(Listing.KEY_USER);

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
               masterList.addAll(listings);
               adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        adapter.notifyDataSetChanged();
//        for (int i = 0; i < listingList.size(); i++) {
//            Log.d("STream", listingList.get(i).getUser().getUsername());
//            Log.d("STream", "if exists: "+listingList.get(i).getUser().getParseFile("profileImg"));
//        }
//        listingList.clear();
//        adapter.notifyDataSetChanged();
    }
}