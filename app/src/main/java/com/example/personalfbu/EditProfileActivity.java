package com.example.personalfbu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class EditProfileActivity extends AppCompatActivity {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1025;
    public final static int PICK_PHOTO_CODE = 1046;
    EditText etEditName, etEditBio, etEditEmail;
    Button btnEditSave, btnTakeProfImg, btnChooseProfImg;
    String name, location, email, bio, prevImgUrl;
    ImageView ivEditImg;
    protected final static String TAG = "EditProfileActivity";
    private File photoFile;
    public String photoFileName = "photo.jpg";
    Bitmap takenImage, selectedImage;
    Boolean chosePhoto = false;
    Uri photoUri;
    ParseGeoPoint geopoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize the SDK
        Places.initialize(EditProfileActivity.this, getResources().getString(R.string.places_sdk_key));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_edit);

        // set hint
        autocompleteFragment.setHint("Enter your city");

        // set type filter for cities
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS_COMPONENTS, Place.Field.NAME, Place.Field.LAT_LNG));

        // find elements on view
        etEditName = findViewById(R.id.etEditName);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditBio = findViewById(R.id.etEditBio);
        btnEditSave = findViewById(R.id.btnEditSave);
        btnTakeProfImg = findViewById(R.id.btnTakeProfImg);
        ivEditImg = findViewById(R.id.ivEditImg);
        btnChooseProfImg = findViewById(R.id.btnChooseProfImg);

        // fill with previous answers, if any
        final ParseUser currentUser = ParseUser.getCurrentUser();
        name = currentUser.getString("Name");
        if(name != null) {
            etEditName.setText(name);
        }
        location = currentUser.getString("location");
        geopoint = currentUser.getParseGeoPoint("coordinates");
        if(location != null) {
            autocompleteFragment.setText(location);
        }
        email = currentUser.getEmail();
        if(email != null) {
            etEditEmail.setText(email);
        }
        bio = currentUser.getString("Bio");
        if(bio != null) {
            etEditBio.setText(bio);
        }
        ParseFile prevImg = currentUser.getParseFile("profileImg");
        if (prevImg != null) {
            prevImgUrl = prevImg.getUrl();
            Glide.with(EditProfileActivity.this)
                    .load(prevImgUrl)
                    .into(ivEditImg);
        }

        // chose place listener
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                List<AddressComponent> addressComponentList = place.getAddressComponents().asList();
                AddressComponent c;
                location = place.getName();
                for (int i = 0; i < addressComponentList.size(); i++) {
                    c = addressComponentList.get(i);
                    if(c.getTypes().contains("administrative_area_level_1")) {
                        location += ", " + c.getShortName();
                    }
                    if(c.getTypes().contains("country")) {
                        location += ", " + c.getName();
                    }
                }
                geopoint = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("EditProfileActivity", "an error occurred while getting place data: " + status);
            }
        });


        // click listener for take photo
        btnTakeProfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        // click listener for choose photo
        btnChooseProfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery();
            }
        });

        // onClick listener for Save
        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etEditName.getText().toString();
                if(name.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(location.isEmpty() || geopoint == null) {
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
                if (!chosePhoto) {
                    if (photoFile == null) {
                        photoFile = new File("fakepath");
                    }
                }
                else if (chosePhoto) {
                    photoFile = new File("chosefile");
                }

                // save profile details
                saveProfile(currentUser, name, location, bio, email, photoFile, geopoint);
                Intent backToProfile = new Intent(EditProfileActivity.this, MainActivity.class);
                backToProfile.putExtra("name", name);
                backToProfile.putExtra("location", location);
                backToProfile.putExtra("bio", bio);
                backToProfile.putExtra("email", email);
                if (takenImage != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    takenImage.compress(Bitmap.CompressFormat.PNG, 50, baos);
                    backToProfile.putExtra("img", baos.toByteArray());
                }
                else if (selectedImage != null) {
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 50, baos2);
                    backToProfile.putExtra("img", baos2.toByteArray());
                }
                setResult(25, backToProfile);
                finish();
            }
        });
    }

    private void launchGallery() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(EditProfileActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(EditProfileActivity.this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // taken photo
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // RESIZE BITMAP, see section below
                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName));

                // by this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());

                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 250);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(photoFileName + "_resized");

                try {
                    resizedFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    // Write the bytes of the bitmap to file
                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // store the path to that resized image
                takenImage = BitmapFactory.decodeFile(resizedFile.getAbsolutePath());

                // Load the taken image into a preview
                ivEditImg.setVisibility(View.VISIBLE);
                ivEditImg.setImageBitmap(takenImage);
                chosePhoto = false;
            }
        }
        else if ((data != null) && requestCode == PICK_PHOTO_CODE) {  // choose photo
            photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            selectedImage = loadFromUri(photoUri);

            // scale image to downsize
            selectedImage = BitmapScaler.scaleToFitWidth(selectedImage, 100);

            ivEditImg.setImageBitmap(selectedImage);
            chosePhoto = true;
        }
        else {
            // Result was a failure
        }
    }

    private Bitmap loadFromUri(Uri photoUri) {
        Bitmap bImage = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                bImage = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                bImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bImage;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(EditProfileActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


    private void saveProfile(final ParseUser currentUser, String name, String location, String bio,
                             String email, final File photoFile, ParseGeoPoint geopoint) {
        currentUser.put("emailAddress", email);
        currentUser.setEmail(email);
        currentUser.put("Name", name);
        currentUser.put("location", location);
        currentUser.put("Bio", bio);
        currentUser.put("coordinates", geopoint);

        if (photoFile.getPath() == "chosefile") {
            final ParseFile p = BitmapScaler.conversionBitmapParseFile(selectedImage);
            currentUser.put("profileImg", p);
        }
        else if (photoFile.getPath() != "fakepath") {
            ParseFile p = new ParseFile(photoFile);
            currentUser.put("profileImg", p);
        }

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error saving profile details", e);
                    Toast.makeText(EditProfileActivity.this, "Error while saving profile details", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Successfully saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}