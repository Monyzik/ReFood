package com.example.refood;

import static android.content.Context.RECEIVER_EXPORTED;
import static android.content.Context.RECEIVER_NOT_EXPORTED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yalantis.pulltomakesoup.PullToRefreshView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class TapeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ArrayList <Post> posts = new ArrayList<>();

    public RecyclerView posts_recyclerView;
    public TextView no_connection;

    PullToRefreshView tape_refresh_view;

    PostsTapeAdapter adapter;

    protected DataReceiver dataReceiver;

    IntentFilter intentFilter;

    FirebaseFirestore db;
    private String mParam1;
    private String mParam2;

    public TapeFragment() {
    }

    public static TapeFragment newInstance(String param1, String param2) {
        TapeFragment fragment = new TapeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", -1);
            String json = intent.getStringExtra("post");
            Post post;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                post = objectMapper.readValue(json, Post.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            posts.set(position, post);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataReceiver = new DataReceiver();
        intentFilter = new IntentFilter("updatedPost");
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        requireActivity().registerReceiver(dataReceiver, intentFilter, RECEIVER_NOT_EXPORTED);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tape, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (NetworkUtils.isNetworkConnected(getContext())) {
            no_connection = view.findViewById(R.id.no_connection2);
            posts_recyclerView = view.findViewById(R.id.tape_recycler_view);
            tape_refresh_view = view.findViewById(R.id.tape_refresh_view);
            db = FirebaseFirestore.getInstance();

            no_connection.setVisibility(View.GONE);
            posts_recyclerView.setVisibility(View.VISIBLE);

            db.collection(Post.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = document.toObject(Post.class);
                        posts.add(post);
                        posts_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new PostsTapeAdapter(posts, getActivity());
                        posts_recyclerView.setAdapter(adapter);
                    }
                }
            });

            tape_refresh_view.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    posts.clear();
                    db.collection(Post.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                posts.add(post);
                                posts_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                adapter = new PostsTapeAdapter(posts, getActivity());
                                posts_recyclerView.setAdapter(adapter);
                            }
                            tape_refresh_view.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }
}