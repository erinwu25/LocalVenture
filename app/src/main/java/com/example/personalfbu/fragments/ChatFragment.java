package com.example.personalfbu.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalfbu.Conversation;
import com.example.personalfbu.CurrentChatsAdapter;
import com.example.personalfbu.Listing;
import com.example.personalfbu.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    public RecyclerView rvCurrentChats;
    public CurrentChatsAdapter currentChatsAdapter;
    public List<Conversation> chatList;

    // Required empty public constructor
    public ChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find elements
        rvCurrentChats = view.findViewById(R.id.rvChattingWith);

        // initialize a list of conversations and adapter
        chatList = new ArrayList<>();
        currentChatsAdapter = new CurrentChatsAdapter(getContext(), chatList);

        // set adapter on recyclerview
        rvCurrentChats.setAdapter(currentChatsAdapter);

        // set layout manager on recyclerview
        rvCurrentChats.setLayoutManager(new LinearLayoutManager(getContext()));

        // dividers between recycler view items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvCurrentChats.getContext(), LinearLayoutManager.VERTICAL);
        Drawable verticalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.vertical_divider);
        dividerItemDecoration.setDrawable(verticalDivider);
        rvCurrentChats.addItemDecoration(dividerItemDecoration);

        // query convos
        queryConversations();

    }

    private void queryConversations() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        // specify which class to query
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);

        // where the first person is current user
        query.whereEqualTo(Conversation.KEY_personOne, currentUser);

        // limit to last 20 convos
        query.setLimit(20);

        // order by updated last
        query.addDescendingOrder(Conversation.KEY_updatedAt);

        // start an asynchronous call for conversations
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e != null) {
                    // log issue getting listings
                    Toast.makeText(getContext(), "Error getting chats", Toast.LENGTH_SHORT).show();
                    return;
                }

                // add to list
                chatList.addAll(objects);
                currentChatsAdapter.notifyDataSetChanged();
            }
        });
    }
}