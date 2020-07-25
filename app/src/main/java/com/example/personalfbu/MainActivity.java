package com.example.personalfbu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.personalfbu.fragments.CreateFragment;
import com.example.personalfbu.fragments.ProfileFragment;
import com.example.personalfbu.fragments.StreamFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    // to manage fragments
    final FragmentManager fragmentManager = getSupportFragmentManager();

    // only bottom navigation left in actual activity; everything else relegated to fragments
    BottomNavigationView bottomNavigationView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.LogOut:
                logout();
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
                Fragment fragment;
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


}