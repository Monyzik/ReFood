package com.example.refood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MyAllReceiptsActivity extends AppCompatActivity {

    Set<Post> posts = new HashSet<>();

    FirebaseFirestore db;
    FirebaseAuth auth;

    ImageView back;

    RecyclerView myAllReceiptsRecyclerView;

    MyReceiptsAdapter myReceiptsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_all_receipts);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        myAllReceiptsRecyclerView = findViewById(R.id.myAllReceiptsRecyclerView);
        back = findViewById(R.id.back_from_all_my_receipts);

        db.collection(Post.COLLECTION_NAME).whereEqualTo(Post.USER_NAME, auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        posts.add(document.toObject(Post.class));
                    }
                    File dir = new File(getFilesDir(), "Recipes");
                    try {
                        for (File file: Objects.requireNonNull(dir.listFiles())) {
                            Post readPost = Post.readSavedRecipe(file);
                            if (!posts.contains(readPost)) {
                                posts.add(readPost);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("e", e.getMessage());
                    }
                    myReceiptsAdapter = new MyReceiptsAdapter(new ArrayList<>(posts), getApplicationContext(), MyAllReceiptsActivity.this);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                    myAllReceiptsRecyclerView.setLayoutManager(gridLayoutManager);
                    myAllReceiptsRecyclerView.setAdapter(myReceiptsAdapter);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}