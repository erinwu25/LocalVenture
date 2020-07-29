package com.example.personalfbu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
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
        MenuItem search = menu.findItem(R.id.search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.LogOut:
                logout();
                break;
            case R.id.search:
                Intent toSearch = new Intent(this, SearchActivity.class);
                startActivityForResult(toSearch, REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // log out
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();  // will be null

        // go back to login page
        Intent toLogin = new Intent(this, LoginActivity.class);
        startActivity(toLogin);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.actionStream);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if request code is the same
        if (requestCode == REQUEST_CODE) {
            // filter based on date and location
            if (data.hasExtra("location")) {
                // time to filter rv (alter rvlist)
                StreamFragment fragInst = (StreamFragment) fragmentManager.findFragmentById(R.id.flContainer);
                if (fragInst != null) {
                    filterLocation(fragInst.adapter, fragInst.listingList, fragInst.masterList,
                            Double.parseDouble(data.getStringExtra("lat")), Double.parseDouble(data.getStringExtra("lng")));
                }
            }
        }
    }

    private void filterLocation(ListingAdapter adapter, List<Listing> listingList, List<Listing> masterList, double lat, double lng) {
        Listing item;
        Double dist;
        List<Integer> newList = new ArrayList<>();
        ParseGeoPoint geo;
        for (int i = 0; i<masterList.size(); i++) {
            item = masterList.get(i);
            geo = item.getUser().getParseGeoPoint("coordinates");
            dist = getDist(lat, lng, geo.getLatitude(), geo.getLongitude());
            Log.d(Tag, dist.toString());
            if (dist <= 50) {
                newList.add(i);
            }
        }
        // clear listing list
        listingList.clear();
        adapter.notifyDataSetChanged();
        for(Integer ind : newList) {
            listingList.add(masterList.get(ind));
//            adapter.notifyItemInserted(listingList.size()-1);
//            adapter.notifyItemChanged(listingList.size());
        }
//        adapter.notifyDataSetChanged();
    }

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