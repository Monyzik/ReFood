package com.example.refood;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ActivityCategoryTape extends AppCompatActivity {
    ArrayList<Post> posts = new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseFirestore db;
    String category;
    TextView category_title;
    ImageView back_button;
    View layout;
    TextView no_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_tape);

        if (NetworkUtils.isNetworkConnected(this)) {
            layout = findViewById(R.id.layout);
            no_connection = findViewById(R.id.no_connection3);

            no_connection.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);


            category_title = findViewById(R.id.category_title);
            back_button = findViewById(R.id.back_from_category_tape);


            category = getIntent().getStringExtra("category");
            category_title.setText(category);


            db = FirebaseFirestore.getInstance();
            recyclerView = findViewById(R.id.category_recycler_view);
            Activity activity = this;


            db.collection(Post.COLLECTION_NAME).whereEqualTo("category", category).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            posts.add(document.toObject(Post.class));
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        PostsTapeAdapter adapter = new PostsTapeAdapter(posts, activity);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });

            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
