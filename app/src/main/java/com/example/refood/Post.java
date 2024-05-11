package com.example.refood;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;



public class Post {
    @Expose
    public static final String COLLECTION_NAME = "posts";
    public static final String USER_NAME = "author";
    @Expose
    private String author, author_name, title, text, id;

    @Expose
    Boolean isLocal;
    @Expose
    Long date;
    @Expose
    long like_count = 0, dislike_count = 0;
    @Expose
    ArrayList <Step> steps;
    @Expose
    ArrayList <String> likes_from_users, dislikes_from_users;
    @Expose
    String image;
    @Expose
    String category;

    public Post(String author, String author_name, String title, String text, String image, Date date, Boolean isLocal, long like_count, long dislike_count, ArrayList <Step> steps, ArrayList <String> likes_from_users, ArrayList <String> dislikes_from_users, String category) {
        this.id = UUID.randomUUID().toString();
        Date now = new Date();
        this.date = now.getTime();
        this.author = author;
        this.author_name = author_name;
        this.title = title;
        this.text = text;
        this.image = image;
        this.isLocal = isLocal;
        this.like_count = like_count;
        this.dislike_count = dislike_count;
        this.steps = steps;
        this.likes_from_users = likes_from_users;
        this.dislikes_from_users = dislikes_from_users;
        this.category = category;
    }

    public Post() {}

    public static final Comparator<Post> COUNT_OF_LIKES_COMPARATOR = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return (int) (o1.getLike_count() - o2.getLike_count());
        }
    };

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




    public static boolean saveRecipe(Post post, String dir_path, ContentResolver contentResolver, Context context) {
        File post_files = new File(dir_path, post.getId());
        String path = post_files.getAbsolutePath();
        post_files.mkdirs();
        File main = new File(path, "main_file");
        File main_image = new File(path, "main_image.jpg");
        try {
            if (!Objects.equals(post.getImage(), "") && post.getImage() != null) {
                System.out.println(post.getImage());
                FileOutputStream outputStream = new FileOutputStream(main_image);
                Bitmap bitmap_main_image = null;
                try {
                    bitmap_main_image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(new File(post.getImage())));
                } catch (Exception e) {
                    Log.e("сохранение изображения", e.getMessage());
                }
                try {
                    bitmap_main_image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(post.image));
                } catch (Exception e) {
                    Log.e("сохранение изображения", e.getMessage());
                }
                bitmap_main_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                main_image.createNewFile();
                outputStream.flush();
                outputStream.close();
            } else {
                FileOutputStream outputStream = new FileOutputStream(main_image);
                Bitmap bitmap_main_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.main_dishes);
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
                if (post.steps.get(i).getImagePath() != null) {
                    File step_image = new File(path, "step_" + i + ".jpg");
                    FileOutputStream  outputStream = new FileOutputStream(step_image);
                    Bitmap bitmap_step_image = null;
                    try {
                        bitmap_step_image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(new File(post.steps.get(i).getImagePath())));
                    } catch (Exception e) {
                        Log.e("сохранение изображения", e.getMessage());
                    }
                    try {
                        bitmap_step_image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(post.steps.get(i).getImagePath()));
                    } catch (Exception e) {
                        Log.e("сохранение изображения", e.getMessage());
                    }
                    bitmap_step_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    step_image.createNewFile();
                    outputStream.flush();
                    outputStream.close();
                }

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


    public static void deletePost(Post post, String dir_path) throws IOException {
        File dir = new File(dir_path);
        for (File file: dir.listFiles()) {
            if (file.getName().equals(post.id)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    FileUtils.deleteDirectory(file);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.hashCode() == this.hashCode();
    }

    public Long getDate() {
        return date;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
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
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public void setDate(Long date) {
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