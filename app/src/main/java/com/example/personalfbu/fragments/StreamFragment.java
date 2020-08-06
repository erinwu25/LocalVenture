package com.example.personalfbu.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.personalfbu.EditProfileActivity;
import com.example.personalfbu.Listing;
import com.example.personalfbu.ListingAdapter;
import com.example.personalfbu.MainActivity;
import com.example.personalfbu.R;
import com.example.personalfbu.SearchActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class StreamFragment extends Fragment {

    public RecyclerView rvStream;
    public ListingAdapter adapter;
    public List<Listing> listingList;
    public List<Listing> masterList;

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

        // find elements
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
        Drawable verticalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.vertical_divider);
        dividerItemDecoration.setDrawable(verticalDivider);
        rvStream.addItemDecoration(dividerItemDecoration);

        // query listings
        queryListings();

        // Item touch helper for swiping (gesture recognizer)
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvStream);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        // this method only used to rearrange recycler view rows (so we can ignore this)
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            ArrayList<Listing> savedListings;
            int position = viewHolder.getAdapterPosition();
            Object saved = currentUser.get("savedListings");
            // if left swiped, add to saved
            if (direction == ItemTouchHelper.LEFT) {
                if (saved == null) {
                    savedListings = new ArrayList<>();
                }
                else {
                    savedListings = (ArrayList<Listing>) saved;
                }
                boolean added = false;
                Listing toAdd = listingList.get(position);
                for (int i=0; i<savedListings.size(); i++) {
                    if (savedListings.get(i).getObjectId().equals(toAdd.getObjectId())) {
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    savedListings.add(toAdd);
                    currentUser.put("savedListings", savedListings);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null) {
                                Toast.makeText(getContext(), "Listing Saved!", Toast.LENGTH_SHORT).show();
                            }
                            else { Toast.makeText(getContext(), "Error saving listing", Toast.LENGTH_SHORT).show();}
                        }
                    });
                }
                else { Toast.makeText(getContext(), "Already Saved", Toast.LENGTH_SHORT).show(); }
            }
            // if right swiped, remove from saved
            else if (direction == ItemTouchHelper.RIGHT) {
                if (saved == null) {
                    // if nothing saved
                    Toast.makeText(getContext(), "Nothing to unsave", Toast.LENGTH_SHORT).show();
                    return;
                }
                savedListings = (ArrayList<Listing>) saved;
                int inList = -1;
                Listing toUnsave = listingList.get(position);
                for (int j = 0; j < savedListings.size(); j++) {
                    // swiped listing is saved, note index to remove
                    if (savedListings.get(j).getObjectId().equals(toUnsave.getObjectId())) {
                        inList = j;
                        break;
                    }
                }
                // if listing to unsave was found in list, remove from list
                if (inList != -1) {
                    savedListings.remove(inList);
                    currentUser.put("savedListings", savedListings);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) { Toast.makeText(getContext(), "Error saving listing", Toast.LENGTH_SHORT).show(); }
                            else { Toast.makeText(getContext(), "Listing Unsaved!", Toast.LENGTH_SHORT).show(); }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Not in saved list", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        // for handling background and image to display on swipe
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_bookmark_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_bookmark_border_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
        }
    };


    private void queryListings() {
        // specify which class to query
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);

        // include data referred by user key
        query.include(Listing.KEY_USER);

        // Limit query to last 30 listings
        query.setLimit(30);

        // order listings by creation date descending
        query.addDescendingOrder(Listing.KEY_CREATED_KEY);

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
               if (e != null) {
                   // log issue getting listings
                   Log.e("StreamFragment", "", e);
                   Toast.makeText(getContext(), "Error getting listings", Toast.LENGTH_SHORT).show();
                   return;
               }
                // add data to list and notify adapter
               listingList.addAll(listings);
               masterList.addAll(listings);
               adapter.notifyDataSetChanged();
            }
        });
    }

}