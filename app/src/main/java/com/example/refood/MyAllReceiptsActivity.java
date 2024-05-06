package com.example.refood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MyAllReceiptsActivity extends AppCompatActivity {

    ArrayList <Post> posts = new ArrayList<>();

    RecyclerView myAllReceiptsRecyclerView;

    PostsTapeAdapter myReceiptsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_all_receipts);
        myAllReceiptsRecyclerView = findViewById(R.id.myAllReceiptsRecyclerView);
        File dir = new File(getFilesDir(), "Recipes");
        try {
            for (File file: Objects.requireNonNull(dir.listFiles())) {
                posts.add(Post.readSavedRecipe(file));
            }
        } catch (Exception e) {
            Log.e("e", e.getMessage());
        }

        myReceiptsAdapter = new PostsTapeAdapter(posts, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myAllReceiptsRecyclerView.setLayoutManager(linearLayoutManager);
        myAllReceiptsRecyclerView.setAdapter(myReceiptsAdapter);
    }
}