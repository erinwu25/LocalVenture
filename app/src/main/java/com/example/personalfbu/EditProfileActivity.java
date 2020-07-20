package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileActivity extends AppCompatActivity {
    EditText etEditName, etEditLocation, etEditBio, etEditEmail;
    Button btnEditSave;
    String name, location, email, bio;
    protected final static String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // find elements om view
        etEditName = findViewById(R.id.etEditName);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditLocation = findViewById(R.id.etEditLocation);
        etEditBio = findViewById(R.id.etEditBio);
        btnEditSave = findViewById(R.id.btnEditSave);

        // fill with previous answers, if any
        final ParseUser currentUser = ParseUser.getCurrentUser();
        name = currentUser.getString("Name");
        if(name != null) {
            etEditName.setText(name);
        }
        location = currentUser.getString("location");
        if(location != null) {
            etEditLocation.setText(location);
        }
        email = currentUser.getEmail();
        if(email != null) {
            etEditEmail.setText(email);
        }
        bio = currentUser.getString("Bio");
        if(bio != null) {
            etEditBio.setText(bio);
        }

        // onClick listener for Save
        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etEditName.getText().toString();
                if(name.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                location = etEditLocation.getText().toString();
                if(location.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Location cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                bio = etEditBio.getText().toString();
                if(bio.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Bio cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                email = etEditEmail.getText().toString();
                if(email.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // save profile details
                saveProfile(currentUser, name, location, bio, email);
                Intent backToProfile = new Intent(EditProfileActivity.this, MainActivity.class);
                backToProfile.putExtra("name", name);
                backToProfile.putExtra("location", location);
                backToProfile.putExtra("bio", bio);
                backToProfile.putExtra("email", email);
                setResult(25, backToProfile);
                finish();
            }
        });
    }

    private void saveProfile(ParseUser currentUser, String name, String location, String bio, String email) {
        currentUser.put("emailAddress", email);
        currentUser.setEmail(email);
        currentUser.put("Name", name);
        currentUser.put("location", location);
        currentUser.put("Bio", bio);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error saving profile details", e);
                    Toast.makeText(EditProfileActivity.this, "Error while saving profile details", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EditProfileActivity.this, "Successfully saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}