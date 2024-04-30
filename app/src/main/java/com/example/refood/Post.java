package com.example.refood;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class Post {

    public static final String COLLECTION_NAME = "posts";

//    Map <String, Object> post = new HashMap<>();
    private String author, title, text, id;

    Date date;

    long like_count = 0, dislike_count = 0;

    ArrayList <Step> steps;

    ArrayList <String> likes_from_users, dislikes_from_users;
    private String image;

    public Post(String id, String author, String title, String text, String image, Date date, long like_count, long dislike_count, ArrayList <Step> steps, ArrayList <String> likes_from_users, ArrayList <String> dislikes_from_users) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.text = text;
        this.image = image;

        this.like_count = like_count;
        this.dislike_count = dislike_count;
        this.steps = steps;
        this.likes_from_users = likes_from_users;
        this.dislikes_from_users = dislikes_from_users;
    }

    public Post() {}

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public ArrayList<String> getDislikes_from_users() {
        return dislikes_from_users;
    }


    public long getDislike_count() {
        return dislike_count;
    }

    public ArrayList<String> getLikes_from_users() {
        return likes_from_users;
    }

    public long getLike_count() {
        return like_count;
    }

    public void editText(String newText) {
        this.text = newText;
    }

    public void editTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}
