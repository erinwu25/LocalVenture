package com.example.personalfbu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class AddReview extends AppCompatActivity {
    TextView tvAddReviewTitle;
    RadioGroup rgRatingGroup;
    RadioButton rb1, rb2, rb3, rb4, rb5;
    EditText etAddReviewContent;
    Button btnAddReviewSubmit;
    ParseUser toUser, fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // find elements in view
        tvAddReviewTitle = findViewById(R.id.tvAddReviewTitle);
        rgRatingGroup = findViewById(R.id.rgRatingGroup);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        rb5 = findViewById(R.id.rb5);
        etAddReviewContent = findViewById(R.id.etAddReviewContent);
        btnAddReviewSubmit = findViewById(R.id.btnAddReviewSubmit);

        // get toUser and fromUser
        toUser = getIntent().getExtras().getParcelable("toUser");
        fromUser = ParseUser.getCurrentUser();

        // populate textview
        tvAddReviewTitle.setText("Add a review for " + toUser.getString("Name"));

        // on submit
        btnAddReviewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ratingNum = rgRatingGroup.getCheckedRadioButtonId();
                if (ratingNum == 0) {
                    Toast.makeText(AddReview.this, "Please select a numerical rating", Toast.LENGTH_SHORT).show();
                    return;
                }
                String reviewContent = etAddReviewContent.getText().toString();
                if (reviewContent.isEmpty()) {
                    Toast.makeText(AddReview.this, "Description of experience required", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitReview(fromUser, toUser, ratingNum, reviewContent);
            }
        });
    }

    private void submitReview(ParseUser fromUser, ParseUser toUser, int ratingNum, String reviewContent) {
        final Review newReview = new Review();
        newReview.setFromUser(fromUser);
        newReview.setToUser(toUser);
        newReview.setReviewContent(reviewContent);
        int rating;
        if(ratingNum == rb1.getId()) { rating = 1; }
        else if (ratingNum == rb2.getId()) { rating = 2; }
        else if (ratingNum == rb3.getId()) { rating = 3; }
        else if (ratingNum == rb4.getId()) { rating = 4; }
        else {rating = 5; }
        newReview.setRating(rating, toUser);
        newReview.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(AddReview.this, "Issue saving review", Toast.LENGTH_SHORT).show();
                    Log.e("AddReview", "issue saving review", e);
                }
                else {
                    finish();
                }
            }
        });
    }
}