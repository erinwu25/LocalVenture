package com.example.personalfbu;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
    Context context;
    List<Listing> listings;
    List<Listing> allListings;

    // pass in the context and listings
    public ListingAdapter(Context context, List<Listing> listings) {
        this.context = context;
        this.listings = listings;
        this.allListings = new ArrayList<>(listings);
    }

    @NonNull
    @Override
    // for each row, inflate the layout
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingAdapter.ViewHolder holder, int position) {
        // get data
        Listing listing = listings.get(position);
        // bind listing with view holder
        try {
            holder.bind(listing);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    // clear listings
    public void clear() {
        listings.clear();
        notifyDataSetChanged();
    }


    // define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvListingName, tvListingRating, tvListingDate;
        ImageView ivListingProfileImg;
        List<Review> ratingResults = new ArrayList<>();
        double rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvListingName = itemView.findViewById(R.id.tvListingName);
            tvListingRating = itemView.findViewById(R.id.tvListingRating);
            ivListingProfileImg = itemView.findViewById(R.id.ivListingProfileImg);
            tvListingDate = itemView.findViewById(R.id.tvListingDate);

            // itemView's onClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // get item position
            int position = getAdapterPosition();
            // ensure valid position
            if (position != RecyclerView.NO_POSITION) {
                Listing listing = listings.get(position);
                Intent toListingDetails = new Intent(context, ListingDetails.class);
                toListingDetails.putExtra(Listing.class.getSimpleName(), Parcels.wrap(listing));
                context.startActivity(toListingDetails);
            }
        }

        public void bind(Listing listing) throws ParseException {
            // fetch data and bind
            ParseUser u = ((Listing)listing.fetchIfNeeded()).getUser().fetchIfNeeded();
            tvListingName.setText((u.getString("Name")));
            queryRatings(listing.getUser());
            tvListingDate.setText(ListingDetails.getRelativeTimeAgo(((Listing)listing.fetchIfNeeded()).getKeyCreatedKey().toString()));
            // bind image
            ivListingProfileImg.setImageResource(R.drawable.ic_menu_compass);
            ParseFile imgFile = (u.getParseFile("profileImg"));
            if (imgFile != null) {
                Glide.with(context)
                        .load(imgFile.getUrl())
                        .circleCrop()
                        .into(ivListingProfileImg);
            }
        }

        public void queryRatings(ParseUser user) {
            Log.d("queryRatings", user.toString());
            ratingResults.clear();
            // specify which class to query
            ParseQuery<Review> query = ParseQuery.getQuery(Review.class);

            // include data referred by ratingNum?
            query.include("*");

            // filter by reviews about certain user
            query.whereEqualTo(Review.KEY_toUser, user);

            // Limit query to last 20 listings
            query.setLimit(20);

            // start an asynchronous call for Reviews
            query.findInBackground(new FindCallback<Review>() {
                @Override
                public void done(List<Review> reviews, ParseException e) {
                    if (e != null) {
                        // log issue getting listings
                        return;
                    }
                    ratingResults.addAll(reviews);
                    averageRatings(ratingResults);
                }
            });
        }

        private void averageRatings(List<Review> ratingResults) {
            // get all the rating numbers from the returned reviews
            double ratingNums = 0.0;
            int numOfReviews = ratingResults.size();
            if (numOfReviews > 0) {
                for (Review review : ratingResults) {
                    ratingNums += review.getNumber(Review.KEY_Rating).doubleValue();
                    rating = ratingNums/numOfReviews;
                }
            }
            else {
                rating = 0.0;
            }
            if(rating != 0.0) {
                DecimalFormat df = new DecimalFormat("#.##");
                tvListingRating.setText("Rating: "+ df.format(rating)+" out of 5");
            }
            else {
                tvListingRating.setText("No ratings yet");
            }
        }
    }
}
