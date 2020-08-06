package com.example.personalfbu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.util.DateInterval;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalfbu.fragments.ChatFragment;
import com.example.personalfbu.fragments.CreateFragment;
import com.example.personalfbu.fragments.ProfileFragment;
import com.example.personalfbu.fragments.StreamFragment;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    // request code
    private final int REQUEST_CODE = 99;
    String Tag = "MainActivity";
    TextView tvFilters;
    // to manage fragments
    final FragmentManager fragmentManager = getSupportFragmentManager();

    // only bottom navigation left in actual activity; everything else relegated to fragments
    BottomNavigationView bottomNavigationView;

    // fragment
    Fragment fragment;

    // menu
    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set text color of menu items
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
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
        try {
            // log out
            ParseUser.logOut();

            // go back to login page
            Intent toLogin = new Intent(this, LoginActivity.class);
            startActivity(toLogin);
            finish();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error logging out. Please try again", Toast.LENGTH_SHORT).show();
        }
        try {
            LoginManager.getInstance().logOut();
        }
        catch (Exception ex){ Log.e(Tag, "nope", ex); }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find element(s)
        tvFilters = findViewById(R.id.tvFilters);

        // create tab navigation and determine what to display based on tab
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setBackgroundColor(Color.WHITE);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.actionCreate:
                        // go to compose activity (main activity)
                        showSearchOptions(false);
                        fragment = new CreateFragment();
                        break;
                    case R.id.actionChat:
                        showSearchOptions(false);
                        fragment = new ChatFragment();
                        break;
                    case R.id.actionProfile:
                        // go to profile
                        showSearchOptions(false);
                        fragment = new ProfileFragment();
                        break;
                    case R.id.actionStream:
                        showSearchOptions(true);

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
;
    }

    // clear the search filters and display original list
    private void clearSearchFilters() {
        StreamFragment fragInst = (StreamFragment) fragmentManager.findFragmentById(R.id.flContainer);
        if (fragInst != null) {
            fragInst.listingList.clear();
            fragInst.adapter.notifyDataSetChanged();
            fragInst.listingList.addAll(fragInst.masterList);
            fragInst.adapter.notifyDataSetChanged();


            //clear text
            ((TextView)findViewById(R.id.tvFilters)).setText("");

            //set views
            findViewById(R.id.tvFilters).setVisibility(View.INVISIBLE);
            findViewById(R.id.avAirplane).setVisibility(View.VISIBLE);
            findViewById(R.id.rvStream).setVisibility(View.VISIBLE);
            findViewById(R.id.avNoResults).setVisibility(View.INVISIBLE);
            findViewById(R.id.tvNoResult).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if request code is the same
        if ((data != null) &&requestCode == REQUEST_CODE) {
            StreamFragment newStreamFrag = new StreamFragment();
            if (fragmentManager.findFragmentById(R.id.flContainer).getClass() != newStreamFrag.getClass()) {
                Log.d("debug", "in");
                fragmentManager.beginTransaction().replace(R.id.flContainer, newStreamFrag).commit();
            }
            // set visibility
            findViewById(R.id.rvStream).setVisibility(View.VISIBLE);
            findViewById(R.id.avNoResults).setVisibility(View.INVISIBLE);
            findViewById(R.id.tvNoResult).setVisibility(View.INVISIBLE);

            // get fragment instance
            StreamFragment fragInst = (StreamFragment) fragmentManager.findFragmentById(R.id.flContainer);

            // filter based on location
            if (data.hasExtra("location")) {
                if (fragInst != null) {
                    try {
                        filterLocation(fragInst.adapter, fragInst.listingList, fragInst.masterList,
                                Double.parseDouble(data.getStringExtra("lat")), Double.parseDouble(data.getStringExtra("lng")),
                                Integer.valueOf(data.getStringExtra("dist")));

                        // set "showing results for"
                        findViewById(R.id.avAirplane).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tvFilters).setVisibility(View.VISIBLE);
                        String locationString = getResources().getString(R.string.showresults) + " " + data.getStringExtra("location");
                        ((TextView)findViewById(R.id.tvFilters)).setText(locationString);
                    }
                    catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error filtering by location. Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            // filter based on date
            if (data.hasExtra("startDate")) {
                if (fragInst != null) {
                    Date startDate = new Date();
                    Date endDate = new Date();
                    startDate.setTime(data.getLongExtra("startDate", -1));
                    endDate.setTime(data.getLongExtra("endDate", -1));
                    try {
                        filterDate(fragInst.adapter, fragInst.listingList, startDate, endDate);

                        // set filter title
                        String addDates = ((TextView)findViewById(R.id.tvFilters)).getText() + " from " + readableDate(startDate) + " - " + readableDate(endDate);
                        ((TextView)findViewById(R.id.tvFilters)).setText(addDates);
                    }
                    catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error filtering by date. Please try again", Toast.LENGTH_SHORT).show();
                    }
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
        if (inDate.size() == 0) {
            // if no results, show animation
            findViewById(R.id.rvStream).setVisibility(View.INVISIBLE);
            findViewById(R.id.avNoResults).setVisibility(View.VISIBLE);
            findViewById(R.id.tvNoResult).setVisibility(View.VISIBLE);
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

        if (newList.size() == 0) {
            // if no results, show animation
            findViewById(R.id.rvStream).setVisibility(View.INVISIBLE);
            findViewById(R.id.avNoResults).setVisibility(View.VISIBLE);
            findViewById(R.id.tvNoResult).setVisibility(View.VISIBLE);
        }
        else {
            for (Integer ind : newList) {
                listingList.add(masterList.get(ind));
            }
            adapter.notifyDataSetChanged();
        }
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

    public void showSearchOptions(boolean showMenu) {
        if (this.menu == null) { return;}
        this.menu.setGroupVisible(R.id.searchGroup, showMenu);
    }

    // returns date in string form
    public String readableDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return DateFormat.getDateInstance().format(c.getTime());
    }
}