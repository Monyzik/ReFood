package com.example.refood;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


public class Post {
    @Expose
    public static final String COLLECTION_NAME = "posts";
    @Expose
    private String author, title, text, id;

    @Expose
    Boolean isLocal;
    @Expose
    String date;
    @Expose
    long like_count = 0, dislike_count = 0;
    @Expose
    ArrayList <Step> steps;
    @Expose
    ArrayList <String> likes_from_users, dislikes_from_users;
    @Expose
    String image;

    public Post(String author, String title, String text, String image, Date date, Boolean isLocal, long like_count, long dislike_count, ArrayList <Step> steps, ArrayList <String> likes_from_users, ArrayList <String> dislikes_from_users) {
        this.id = UUID.randomUUID().toString();
        this.author = author;
        this.title = title;
        this.text = text;
        this.image = image;
        this.isLocal = isLocal;
        this.like_count = like_count;
        this.dislike_count = dislike_count;
        this.steps = steps;
        this.likes_from_users = likes_from_users;
        this.dislikes_from_users = dislikes_from_users;
    }

    public Post() {}

    public static Post readSavedRecipe(File recipeDir) throws IOException {
        Post post = null;
        String mainImage = "";
        for (File file : Objects.requireNonNull(recipeDir.listFiles())) {
            if (file.getName().equals("main_file")) {
                ObjectMapper objectMapper = new ObjectMapper();
                post = objectMapper.readValue(file, Post.class);
            } else if (file.getName().equals("main_image.jpg")) {
                mainImage = file.getAbsolutePath();
            }
        }
        post.setImage(mainImage);
        for (File file : Objects.requireNonNull(recipeDir.listFiles())) {
            String name = file.getName();
            if (name.charAt(0) == 's') {
                post.steps.get(Integer.parseInt(name.substring(5, name.length() - 4))).setImagePath(file.getAbsolutePath());
            }
        }
        return post;
    }




    public static boolean saveRecipe(Post post, String path, ContentResolver contentResolver, Context context) {
        File main = new File(path, "main_file");
        File main_image = new File(path, "main_image.jpg");
        try {
            if (!Objects.equals(post.getImage(), "")) {
                FileOutputStream outputStream = new FileOutputStream(main_image);
                Bitmap bitmap_main_image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(post.image));
                bitmap_main_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                main_image.createNewFile();
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e("E", e.getMessage());
        }
        try {

            for (int i = 0; i < post.steps.size(); i++) {
                File step_image = new File(path, "step_" + i + ".jpg");
                FileOutputStream  outputStream = new FileOutputStream(step_image);
                if (post.steps.get(i).getImagePath() != null) {
                    Bitmap bitmap_step_image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(post.steps.get(i).getImagePath()));
                    bitmap_step_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    step_image.createNewFile();
                }

                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e("e", e.getMessage());
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(post);
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(main));
            writer.print(json);
            writer.close();
        } catch (IOException e) {
            return false;
        }
        try {
            main.createNewFile();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean getIsLocal() {
        return isLocal;
    }

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

    public void setIsLocal(boolean local) {
        this.isLocal = local;
    }
}