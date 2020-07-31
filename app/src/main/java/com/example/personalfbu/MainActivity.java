package com.example.personalfbu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.icu.util.DateInterval;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.personalfbu.fragments.CreateFragment;
import com.example.personalfbu.fragments.ProfileFragment;
import com.example.personalfbu.fragments.StreamFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    // request code
    private final int REQUEST_CODE = 99;
    String Tag = "MainActivity";

    // to manage fragments
    final FragmentManager fragmentManager = getSupportFragmentManager();

    // only bottom navigation left in actual activity; everything else relegated to fragments
    BottomNavigationView bottomNavigationView;

    // fragment
    Fragment fragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // determine what to do based on selected item
        switch (item.getItemId()) {
            case R.id.LogOut:
                logout();
                break;
            case R.id.search:
                Intent toSearch = new Intent(this, SearchActivity.class);
                startActivityForResult(toSearch, REQUEST_CODE);
                break;
            case R.id.clearSearch:
                clearSearchFilters();
                break;
            case R.id.savedListings:
                Intent toSaved = new Intent(this, SavedListings.class);
                startActivity(toSaved);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // log out
        ParseUser.logOut();

        // go back to login page
        Intent toLogin = new Intent(this, LoginActivity.class);
        startActivity(toLogin);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create tab navigation and determine what to display based on tab
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.actionCreate:
                        // go to compose activity (main activity)
                        fragment = new CreateFragment();
                        break;
                    case R.id.actionProfile:
                        // go to profile
                        fragment = new ProfileFragment();
                        break;
                    case R.id.actionStream:
                    default:
                        // go to stream
                        fragment = new StreamFragment();
                        break;
                }
                // set chosen fragment to display
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.actionStream);

    }

    // clear the search filters and display original list
    private void clearSearchFilters() {
        StreamFragment fragInst = (StreamFragment) fragmentManager.findFragmentById(R.id.flContainer);
        if (fragInst != null) {
            fragInst.listingList.clear();
            fragInst.adapter.notifyDataSetChanged();
            fragInst.listingList.addAll(fragInst.masterList);
            fragInst.adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if request code is the same
        if ((data != null) &&requestCode == REQUEST_CODE) {
            // get fragment instance
            StreamFragment fragInst = (StreamFragment) fragmentManager.findFragmentById(R.id.flContainer);
            // filter based on location
            if (data.hasExtra("location")) {
                if (fragInst != null) {
                    filterLocation(fragInst.adapter, fragInst.listingList, fragInst.masterList,
                            Double.parseDouble(data.getStringExtra("lat")), Double.parseDouble(data.getStringExtra("lng")),
                            Integer.valueOf(data.getStringExtra("dist")));
                }
            }
            // filter based on date
            if (data.hasExtra("startDate")) {
                if (fragInst != null) {
                    Date startDate = new Date();
                    Date endDate = new Date();
                    startDate.setTime(data.getLongExtra("startDate", -1));
                    endDate.setTime(data.getLongExtra("endDate", -1));
                    filterDate(fragInst.adapter, fragInst.listingList, startDate, endDate);
                }
            }
        }
    }

    // filter listings by dates
    private void filterDate(ListingAdapter adapter, List<Listing> listingList, Date startDate, Date endDate) {
        List<Listing> inDate = new ArrayList<>();
        Listing item;
        Date listingStart, listingEnd;
        for (int i = 0; i<listingList.size(); i++) {
            item = listingList.get(i);
            listingStart = item.getStartDate();
            listingEnd = item.getEndDate();
            if (overlapDates(startDate, endDate, listingStart, listingEnd)) {
                inDate.add(item);
            }
        }
        listingList.clear();
        adapter.notifyDataSetChanged();
        listingList.addAll(inDate);
        adapter.notifyDataSetChanged();
    }

    private boolean overlapDates(Date startDate, Date endDate, Date listingStart, Date listingEnd) {
        // logic from https://stackoverflow.com/questions/17106670/how-to-check-a-timeperiod-is-overlapping-another-time-period-in-java
        return (!startDate.after(listingEnd) && !listingStart.after(endDate));
    }


    // filter listings by location
    private void filterLocation(ListingAdapter adapter, List<Listing> listingList, List<Listing> masterList,
                                double lat, double lng, int maxDistance) {
        Listing item;
        Double dist;
        List<Integer> newList = new ArrayList<>();
        ParseGeoPoint geo;

        // for each listing, get coordinates and check if within range of the searched destination
        for (int i = 0; i<masterList.size(); i++) {
            item = masterList.get(i);
            geo = item.getUser().getParseGeoPoint("coordinates");
            dist = getDist(lat, lng, geo.getLatitude(), geo.getLongitude());
            if (dist <= maxDistance) {
                newList.add(i);
            }
        }

        // clear listing list and add only those that are within the chosen range of the location
        listingList.clear();
        adapter.notifyDataSetChanged();
        for(Integer ind : newList) {
            listingList.add(masterList.get(ind));
        }
        adapter.notifyDataSetChanged();
    }

    // uses the great circle distance approach
    private Double getDist(double lat1, double lng1, double lat2, double lng2) {
        double rlat1, rlat2, rlng1, rlng2;
        rlat1 = lat1/(180/Math.PI);
        rlat2 = lat2/(180/Math.PI);
        rlng1 = lng1/(180/Math.PI);
        rlng2 = lng2/(180/Math.PI);
        // distance in miles
        return 3963.0*Math.acos(Math.sin(rlat1)*Math.sin(rlat2) + Math.cos(rlat1)*Math.cos(rlat2)*Math.cos(rlng2-rlng1));
    }
}