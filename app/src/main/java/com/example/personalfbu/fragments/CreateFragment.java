package com.example.personalfbu.fragments;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalfbu.BitmapScaler;
import com.example.personalfbu.DatePickerFragment;
import com.example.personalfbu.Listing;
import com.example.personalfbu.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class CreateFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private final String TAG = "CreateFragment";
    public final static int PICK_PHOTO_CODE = 2001;
    TextView tvComposeName, tvComposeEmail, tvComposeLocation, tvStartDate, tvEndDate;
    ImageView ivComposeProfileImg, iv1, iv2, iv3, iv4;
    EditText etComposeBlurb;
    Button btnComposeSubmit, btnStartDate, btnEndDate;
    ImageButton btnComposeImgs;
    ParseFile imgFile;
    List mBitmapsSelected, filesSelected;
    boolean choseImgs = false;
    boolean startCurrent = false;
    boolean endCurrent = false;
    Date startDateResult, endDateResult;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get current user to populate some fields
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // find elements in view
        tvComposeName = view.findViewById(R.id.tvComposeName);
        ivComposeProfileImg = view.findViewById(R.id.ivComposeProfileImg);
        etComposeBlurb = view.findViewById(R.id.etComposeBlurb);
        tvComposeEmail = view.findViewById(R.id.tvComposeEmail);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvComposeLocation = view.findViewById(R.id.tvComposeLocation);
        btnComposeSubmit = view.findViewById(R.id.btnComposeSubmit);
        btnComposeImgs = view.findViewById(R.id.btnComposeImgs);
        btnEndDate = view.findViewById(R.id.btnEndDate);
        iv1 = view.findViewById(R.id.iv1);
        iv2 = view.findViewById(R.id.iv2);
        iv3 = view.findViewById(R.id.iv3);
        iv4 = view.findViewById(R.id.iv4);
        btnStartDate = view.findViewById(R.id.btnStartDate);

        // populate elements
        tvComposeName.setText(currentUser.getString("Name"));
        tvComposeEmail.setText(currentUser.getEmail());
        tvComposeLocation.setText("Area: " + currentUser.getString("location"));
        imgFile = currentUser.getParseFile("profileImg");
        if (imgFile != null) {
            Glide.with(getContext())
                    .load(imgFile.getUrl())
                    .transform(new RoundedCornersTransformation(15, 3))
                    .into(ivComposeProfileImg);
        }

        // click listener for choosing dates
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.setTargetFragment(CreateFragment.this, 0);
                datePicker.show(getFragmentManager(), "datePicker");
                startCurrent = true;

            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment endDatePicker = new DatePickerFragment();
                endDatePicker.setTargetFragment(CreateFragment.this, 0);
                endDatePicker.show(getFragmentManager(), "endDatePicker");
                endCurrent = true;
            }
        });

        // click listener for choosing imgs
        btnComposeImgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickImgs();
            }
        });

        // Click listener for submit
        btnComposeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String blurb = etComposeBlurb.getText().toString();
                if (blurb.isEmpty()) {
                    Toast.makeText(getContext(), "Blurb cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((startDateResult == null) || (endDateResult == null)) {
                    Toast.makeText(getContext(), "Please add your availability", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(startDateResult.after(endDateResult) && (!(startDateResult.compareTo(endDateResult)==0))) {
                    Toast.makeText(getContext(), "Start date must be before end date", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveListing(currentUser, blurb);
            }
        });
    }


    // for selecting images
    private void onPickImgs() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if returning from selecting photos
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            if ((data.getClipData() != null) && (data.getClipData().getItemCount() <= 4)) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                mBitmapsSelected = new ArrayList<Bitmap>();
                filesSelected = new ArrayList<ParseFile>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);

                    // Use the loadFromUri method from above
                    Bitmap bitmap = loadFromUri(uri);

                    // scale image to downsize
                    bitmap = BitmapScaler.scaleToFitWidth(bitmap, 200);

                    mBitmapsSelected.add(bitmap);
                    filesSelected.add(BitmapScaler.conversionBitmapParseFile(bitmap));
                }
                choseImgs = true;
                switch (mBitmapsSelected.size()) {
                    case 4:
                        iv4.setImageBitmap((Bitmap) mBitmapsSelected.get(3));
                    case 3:
                        iv3.setImageBitmap((Bitmap) mBitmapsSelected.get(2));
                    case 2:
                        iv2.setImageBitmap((Bitmap) mBitmapsSelected.get(1));
                    case 1:
                        iv1.setImageBitmap((Bitmap) mBitmapsSelected.get(0));
                    case 0:
                        break;
                }

            } else if (data.getClipData().getItemCount() > 4) {
                Toast.makeText(getContext(), "Please choose 4 images or less", Toast.LENGTH_LONG).show();
            }
        }
    }

    // load selected photos from uri
    private Bitmap loadFromUri(Uri uri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), uri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void saveListing(ParseUser currentUser, String blurb) {
        // create new listing and set attributes
        Listing newListing = new Listing();
        newListing.setUser(currentUser);
        newListing.setBlurb(blurb);
        newListing.setStartDate(startDateResult);
        newListing.setEndDate(endDateResult);
        if (choseImgs) {
            newListing.setImages(filesSelected);
        }

        // save listing to database
        newListing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while saving", e);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                } else {
                    // clear fields after posting
                    Toast.makeText(getContext(), "Successfully posted!", Toast.LENGTH_SHORT).show();
                    tvStartDate.setText("");
                    etComposeBlurb.setText("");
                    iv1.setImageResource(R.drawable.ic_menu_gallery);
                    iv2.setImageResource(R.drawable.ic_menu_gallery);
                    iv3.setImageResource(R.drawable.ic_menu_gallery);
                    iv4.setImageResource(R.drawable.ic_menu_gallery);
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        String date = DateFormat.getDateInstance().format(c.getTime());
        if (startCurrent) {
            tvStartDate.setText(date);
            startDateResult = c.getTime();
            startCurrent = false;
        }
        if (endCurrent) {
            tvEndDate.setText(date);
            endDateResult = c.getTime();
            endCurrent = false;
        }

    }
}