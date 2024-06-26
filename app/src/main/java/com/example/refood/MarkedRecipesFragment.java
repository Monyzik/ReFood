package com.example.refood;

import static android.content.Context.RECEIVER_EXPORTED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarkedRecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarkedRecipesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ArrayList<Post> posts = new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth auth;

    protected DataReceiver dataReceiver;

    protected IntentFilter intentFilter;

    PostsTapeAdapter adapter;


    public MarkedRecipesFragment() {
    }

    public static MarkedRecipesFragment newInstance(String param1, String param2) {
        MarkedRecipesFragment fragment = new MarkedRecipesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onResume() {
        super.onResume();
        dataReceiver = new DataReceiver();
        intentFilter = new IntentFilter("updatedPost");
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        requireActivity().registerReceiver(dataReceiver, intentFilter, RECEIVER_EXPORTED);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(dataReceiver);
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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_marked_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.marked_recipes_recycler_view);
        if (NetworkUtils.isNetworkConnected(getContext())) {
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (!user.getMark_posts().isEmpty()) {
                        db.collection(Post.COLLECTION_NAME).whereIn("id", user.getMark_posts()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document: task.getResult()) {
                                        posts.add(document.toObject(Post.class));
                                    }
                                }
                                File dir = new File(getContext().getFilesDir() + "/OtherRecipes");
                                if(dir.listFiles().length != 0) {
                                    for (File post : dir.listFiles()) {
                                        try {
                                            Post post1 = Post.readSavedRecipe(post);
                                            if (!posts.contains(post1)) {
                                                posts.add(post1);
                                            }
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                                adapter = new PostsTapeAdapter(posts, getActivity());
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    } else {
                        TextView marked_fragment_text = view.findViewById(R.id.marked_fragment_textView);
                        marked_fragment_text.setText(R.string.withot_marked_posts);
                        adapter = new PostsTapeAdapter(posts, getActivity());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        } else {
            File dir = new File(getContext().getFilesDir() + "/OtherRecipes");
            if(dir.listFiles().length != 0){
                for (File post : dir.listFiles()) {
                    try {
                        posts.add(Post.readSavedRecipe(post));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                adapter = new PostsTapeAdapter(posts, getActivity());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            } else {
                TextView marked_fragment_text = view.findViewById(R.id.marked_fragment_textView);
                marked_fragment_text.setText(R.string.withot_marked_posts);
                PostsTapeAdapter adapter = new PostsTapeAdapter(posts, getActivity());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        }


    }
}