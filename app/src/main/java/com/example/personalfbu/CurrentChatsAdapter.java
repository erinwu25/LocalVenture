package com.example.personalfbu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class CurrentChatsAdapter extends RecyclerView.Adapter<CurrentChatsAdapter.ViewHolder> {
    Context context;
    List<Conversation> currentChatList;

    // elements
    ImageView ivCurrentChatImg;
    TextView tvChatName;

    public CurrentChatsAdapter(Context context, List<Conversation> convos) {
        this.context = context;
        this.currentChatList = convos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_currentchat, parent, false);
        return new CurrentChatsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentChatsAdapter.ViewHolder holder, int position) {
        // get data
        Conversation conversation = currentChatList.get(position);
        // bind
        try {
            holder.bind(conversation);
        } catch (ParseException e) {
            Log.e("CurrentChatsAdapter", "error binding chats", e);
        }
    }

    @Override
    public int getItemCount() {
        return currentChatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // locate elements in view
            ivCurrentChatImg = itemView.findViewById(R.id.ivCurrentChatImg);
            tvChatName = itemView.findViewById(R.id.tvChatName);

            // itemView's onClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("CurrentChatsAdapter", "clicked");

            // get adapter position
            int position = getAdapterPosition();

            // ensure valid position
            if (position != RecyclerView.NO_POSITION) {
                Conversation conversation = currentChatList.get(position);
                try {
                    ParseUser receivingUser = ((Conversation)conversation.fetchIfNeeded()).getPersonTwo();
                    Intent toChat = new Intent(context, ChatActivity.class);
                    Bundle userBundle = new Bundle();
                    userBundle.putParcelable("receiver", receivingUser);
                    toChat.putExtras(userBundle);
                    // start activity
                    context.startActivity(toChat);
                } catch (ParseException e) {
                    Log.e("CurrentChatsAdapter", "error getting chats", e);
                    return;
                }
            }

        }

        public void bind(Conversation chat) throws ParseException {
            // populate fields
            ParseUser chatUser = ((Conversation)chat.fetchIfNeeded()).getPersonTwo();
            tvChatName.setText(((ParseUser)chatUser.fetchIfNeeded()).getString("Name"));
            ParseFile img = ((ParseUser)chatUser.fetchIfNeeded()).getParseFile("profileImg");
            if (img != null) {
                Glide.with(context)
                        .load(img.getUrl())
                        .circleCrop()
                        .into(ivCurrentChatImg);
            }
        }
    }
}
