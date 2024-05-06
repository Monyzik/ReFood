package com.example.refood;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User {

    public static final String COLLECTION_NAME = "users";
    private static final String TAG = "collection user update";

    Map <String, Object> user_data = new HashMap<>();

    String name, avatar_path, id;

    Long likes_on_user_posts, dislikes_on_user_post;

    ArrayList <String> mark_posts = new ArrayList<>();
    ArrayList <String> like_posts = new ArrayList<>();
    ArrayList <String> dislike_posts = new ArrayList<>();

    ArrayList <String> id_user_posts = new ArrayList<>();

    public User(String id, String name, String avatar_path, Long likes_on_user_posts, Long dislikes_on_user_post, ArrayList <String> mark_posts, ArrayList <String> like_posts, ArrayList <String> dislike_posts, ArrayList <String> id_user_posts) {
        this.name = name;
        this.avatar_path = avatar_path;
        this.id = id;
        this.likes_on_user_posts = likes_on_user_posts;
        this.dislikes_on_user_post = dislikes_on_user_post;
        this.mark_posts = mark_posts;
        this.like_posts = like_posts;
        this.dislike_posts = dislike_posts;
        this.id_user_posts = id_user_posts;
    }

    public User() {}


    public ArrayList<String> getDislike_posts() {
        return dislike_posts;
    }

    public ArrayList<String> getLike_posts() {
        return like_posts;
    }

    public ArrayList<String> getMark_posts() {
        return mark_posts;
    }

    public String getAvatar_path() {
        return avatar_path;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
