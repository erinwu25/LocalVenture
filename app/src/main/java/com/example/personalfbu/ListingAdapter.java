package com.example.personalfbu;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
    Context context;
    List<Listing> listings;
    Number rating;

    // pass in the context and listings
    public ListingAdapter(Context context, List<Listing> listings) {
        this.context = context;
        this.listings = listings;
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
        holder.bind(listing);
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

        public void bind(Listing listing) {
            tvListingName.setText(listing.getUser().getString("Name"));
            rating = listing.getUser().getNumber("Rating");
            if (rating.doubleValue() < 1 ) {
                tvListingRating.setText("No ratings yet");
            }
            else {
                tvListingRating.setText(String.valueOf(rating) + "/5");
            }
            tvListingDate.setText(ListingDetails.getRelativeTimeAgo(listing.getKeyCreatedKey().toString()));
            // bind image



        }
    }
}
