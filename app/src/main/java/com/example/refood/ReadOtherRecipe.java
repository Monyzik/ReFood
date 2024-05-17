package com.example.refood;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class ReadOtherRecipe extends AppCompatActivity {

    ImageView backImageView, mark_post, likeImage, dislikeImage;
    CardView likePostCardView, dislikePostCardView;
    TextView likeCount, dislikeCount;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Activity activity;
    SparkButton starSparkButton;
    boolean clicked_mark;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_recipe);
        activity = this;
        String json = getIntent().getStringExtra("post");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            post = objectMapper.readValue(json, Post.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        TextView title = findViewById(R.id.title);
        TextView info = findViewById(R.id.info_text);
        ImageView food_image = findViewById(R.id.food_image);
        TextView author = findViewById(R.id.author_name);
        backImageView = findViewById(R.id.back_from_read_receipt);
        likePostCardView = findViewById(R.id.likeCardView);
        dislikePostCardView = findViewById(R.id.dislikeCardView);
        likeCount = findViewById(R.id.like_count);
        dislikeCount = findViewById(R.id.dislike_count);
        likeImage = findViewById(R.id.likeImage);
        dislikeImage = findViewById(R.id.dislikeImage);
        starSparkButton = findViewById(R.id.star_sparkButton);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_read_steps);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        TextView category = findViewById(R.id.category_text_read_recipe);

        title.setText(post.getTitle());
        info.setText(post.getText());

        if (post.getImage() != null && !post.getImage().equals("")) {
            if (post.getIsLocal()) {
                food_image.setImageURI(Uri.parse(post.getImage()));
            } else {
                StorageReference mainImageReference = storage.getReference(post.getImage());
                mainImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = String.valueOf(uri);
                        Glide.with(ReadOtherRecipe.this).load(imageURL).into(food_image);
                    }
                });
            }
        } else {
            food_image.setImageResource(R.drawable.example_of_food_photo);
        }
        db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                clicked_mark = user.getMark_posts().contains(post.getId());
                if (clicked_mark) {
                    starSparkButton.setChecked(true);
                }
            }
        });
        if (post.getLikes_from_users().contains(auth.getCurrentUser().getUid())) {
            likeImage.setImageResource(R.drawable.baseline_thumb_up_filled);
        } else if (post.getDislikes_from_users().contains(auth.getCurrentUser().getUid())) {
            dislikeImage.setImageResource(R.drawable.baseline_thumb_down_filled);
        }
        author.setText(post.getAuthor_name());
        likeCount.setText(String.valueOf(post.getLike_count()));
        dislikeCount.setText(String.valueOf(post.getDislike_count()));

        likePostCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        ArrayList<String> likedPosts = user.getLike_posts(), dislikedPosts = user.getDislike_posts();
                        ArrayList<String> likes_from_users = post.getLikes_from_users(), dislikes_from_users = post.getDislikes_from_users();
                        boolean isLiked = likedPosts.contains(post.getId()), isDisliked = dislikedPosts.contains(post.getId());
                        if (isDisliked) {
                            likedPosts.add(post.getId());
                            dislikedPosts.remove(post.getId());
                            likes_from_users.add(auth.getCurrentUser().getUid());
                            dislikes_from_users.remove(auth.getCurrentUser().getUid());
                            dislikeImage.setImageResource(R.drawable.baseline_dislike);
                            likeImage.setImageResource(R.drawable.baseline_thumb_up_filled);
                        } else if (isLiked) {
                            likedPosts.remove(post.getId());
                            likes_from_users.remove(auth.getCurrentUser().getUid());
                            likeImage.setImageResource(R.drawable.baseline_like);
                        } else {
                            likedPosts.add(post.getId());
                            likes_from_users.add(auth.getCurrentUser().getUid());
                            likeImage.setImageResource(R.drawable.baseline_thumb_up_filled);
                        }

                        post.setDislikes_from_users(dislikes_from_users);
                        post.setLikes_from_users(likes_from_users);
                        post.setLike_count(likes_from_users.size());
                        post.setDislike_count(dislikes_from_users.size());

                        db.collection(Post.COLLECTION_NAME).document(post.getId()).update("dislikes_from_users", dislikes_from_users, "likes_from_users", likes_from_users,
                                "like_count", likes_from_users.size(), "dislike_count", dislikes_from_users.size());
                        db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).update("dislike_posts", dislikedPosts, "like_posts", likedPosts);
                        likeCount.setText(String.valueOf(likes_from_users.size()));
                        dislikeCount.setText(String.valueOf(dislikes_from_users.size()));
                    }
                });
            }
        });

        dislikePostCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        ArrayList<String> likedPosts = user.getLike_posts(), dislikedPosts = user.getDislike_posts();
                        ArrayList<String> likes_from_users = post.getLikes_from_users(), dislikes_from_users = post.getDislikes_from_users();
                        boolean isLiked = likedPosts.contains(post.getId()), isDisliked = dislikedPosts.contains(post.getId());
                        if (isLiked) {
                            likedPosts.remove(post.getId());
                            dislikedPosts.add(post.getId());
                            likes_from_users.remove(auth.getCurrentUser().getUid());
                            dislikes_from_users.add(auth.getCurrentUser().getUid());
                            dislikeImage.setImageResource(R.drawable.baseline_thumb_down_filled);
                            likeImage.setImageResource(R.drawable.baseline_like);
                        } else if (isDisliked) {
                            dislikedPosts.remove(post.getId());
                            dislikes_from_users.remove(auth.getCurrentUser().getUid());
                            dislikeImage.setImageResource(R.drawable.baseline_dislike);
                        } else {
                            dislikedPosts.add(post.getId());
                            dislikes_from_users.add(auth.getCurrentUser().getUid());
                            dislikeImage.setImageResource(R.drawable.baseline_thumb_down_filled);
                        }

                        post.setDislikes_from_users(dislikes_from_users);
                        post.setLikes_from_users(likes_from_users);
                        post.setLike_count(likes_from_users.size());
                        post.setDislike_count(dislikes_from_users.size());

                        db.collection(Post.COLLECTION_NAME).document(post.getId()).update("dislikes_from_users", dislikes_from_users, "likes_from_users", likes_from_users,
                                "like_count", likes_from_users.size(), "dislike_count", dislikes_from_users.size());
                        db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).update("dislike_posts", dislikedPosts, "like_posts", likedPosts);
                        likeCount.setText(String.valueOf(likes_from_users.size()));
                        dislikeCount.setText(String.valueOf(dislikes_from_users.size()));
                    }
                });
            }
        });


        category.setText(post.category);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReadStepAdapter adapter = new ReadStepAdapter(post.steps, post.getIsLocal(), ReadOtherRecipe.this);
        recyclerView.setAdapter(adapter);

        starSparkButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        clicked_mark = user.getMark_posts().contains(post.getId());
                        ArrayList <String> mark_posts = user.getMark_posts();
                        if (buttonState) {
                            if (Post.saveRecipe(post, getFilesDir() + "/OtherRecipes", activity.getContentResolver(), activity)) {
                                Toast.makeText(activity, "Сохранено", Toast.LENGTH_SHORT).show();
                            }
                            mark_posts.add(post.getId());
                            clicked_mark = true;
                        } else {
                            try {
                                Post.deletePost(post.getId(), activity.getFilesDir() + "/OtherRecipes");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            mark_posts.remove(post.getId());
                            clicked_mark = false;
                        }
                        db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).update("mark_posts", mark_posts);
                    }
                });
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getIntent().getIntExtra("requestCode", -1) == 19736) {
            Intent data = new Intent("updatedPost");
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(post);
            data.putExtra("post", json);
            data.putExtra("position", getIntent().getIntExtra("pos", -2));
            sendBroadcast(data);
            finish();
        }

    }
}