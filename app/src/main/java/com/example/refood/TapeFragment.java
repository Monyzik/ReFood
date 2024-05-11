package com.example.refood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
public class TapeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ArrayList <Post> posts = new ArrayList<>();

    public RecyclerView posts_recyclerView;
    public TextView no_connection;

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
                        PostsTapeAdapter adapter = new PostsTapeAdapter(posts, getActivity());
                        posts_recyclerView.setAdapter(adapter);
                    }
                }
            });
        }


    }

}