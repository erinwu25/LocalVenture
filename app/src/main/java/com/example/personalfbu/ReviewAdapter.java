package com.example.personalfbu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Context context;
    List<Review> reviews;

    // elements in view
    ImageView ivFromUserImg;
    TextView tvFromUserRating, tvFromUserContent, tvFromUserName, tvFromUserDate;

    // pass in context and reviews
    public ReviewAdapter (Context context, List<Review> reviews ) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    // for each row, inflate the layout
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        // get data
        Review review = reviews.get(position);
        // bind listing with view holder
        holder.bind(review);
    }

    @Override
    public int getItemCount() { return reviews.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // locate elements in view
            ivFromUserImg = itemView.findViewById(R.id.ivFromUserImg);
            tvFromUserContent = itemView.findViewById(R.id.tvFromUserContent);
            tvFromUserName = itemView.findViewById(R.id.tvFromUserName);
            tvFromUserRating = itemView.findViewById(R.id.tvFromUserRating);
            tvFromUserDate = itemView.findViewById(R.id.tvFromUserDate);

        }

        public void bind(Review review) {
            tvFromUserName.setText(review.getFromUser().getString("Name"));
            tvFromUserRating.setText(String.valueOf(review.getRating()) + "/5");
            tvFromUserContent.setText(review.getReviewContent());
            tvFromUserDate.setText(ListingDetails.getRelativeTimeAgo(review.getWhenCreated().toString()));
            ParseFile imgFile = review.getFromUser().getParseFile("profileImg");
            if (imgFile != null) {
                Glide.with(context)
                        .load(imgFile.getUrl())
                        .circleCrop()
                        .into(ivFromUserImg);
            }

        }
    }
}
