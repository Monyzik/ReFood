package com.example.refood;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class ReadOtherRecipe extends AppCompatActivity {

    ImageView backImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_recipe);
        String json = getIntent().getStringExtra("post");
        ObjectMapper objectMapper = new ObjectMapper();
        Post post;
        try {
            post = objectMapper.readValue(json, Post.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        TextView title = findViewById(R.id.title);
        TextView info = findViewById(R.id.info_text);
        ImageView food_image = findViewById(R.id.food_image);
        TextView author = findViewById(R.id.author_name);
        TextView likes = findViewById(R.id.like_count);
        TextView dislikes = findViewById(R.id.dislike_count);
        Button add_to_favorite = findViewById(R.id.add_to_favorites);
        backImageView = findViewById(R.id.back_from_read_receipt);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_read_steps);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText(post.getTitle());
        info.setText(post.getText());
        if (post.getImage() != null && !post.getImage().equals("")) {
            System.out.println(post.getImage() + "");
            food_image.setImageURI(Uri.parse(post.getImage()));
        }
        else {
            food_image.setImageResource(R.drawable.example_of_food_photo);
        }
        author.setText(post.getAuthor());
        likes.setText(String.valueOf(post.getLike_count()));
        dislikes.setText(String.valueOf(post.getDislike_count()));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReadStepAdapter adapter = new ReadStepAdapter(post.steps);
        recyclerView.setAdapter(adapter);

    }
}