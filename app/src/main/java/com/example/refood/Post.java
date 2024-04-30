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
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class Post {
    @Expose
    public static final String COLLECTION_NAME = "posts";

//    Map <String, Object> post = new HashMap<>();
    @Expose
    private String author, title, text, id;
    @Expose
    String date;
    @Expose
    long like_count = 0, dislike_count = 0;
    @Expose
    ArrayList <Step> steps;
    @Expose
    ArrayList <String> likes_from_users, dislikes_from_users;
    @Expose
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
//    ---------------------------------------------------------------------
public Post(String id, String title, String text, String image, Date date, long like_count, long dislike_count, ArrayList <Step> steps, ArrayList <String> likes_from_users, ArrayList <String> dislikes_from_users) {
    this.id = id;
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

//    -------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String  date) {
        this.date = date;
    }

    public void setLike_count(long like_count) {
        this.like_count = like_count;
    }

    public void setDislike_count(long dislike_count) {
        this.dislike_count = dislike_count;
    }

    public void setLikes_from_users(ArrayList<String> likes_from_users) {
        this.likes_from_users = likes_from_users;
    }

    public void setDislikes_from_users(ArrayList<String> dislikes_from_users) {
        this.dislikes_from_users = dislikes_from_users;
    }
}
