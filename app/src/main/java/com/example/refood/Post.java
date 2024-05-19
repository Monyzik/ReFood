package com.example.refood;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
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
    @Expose
    public int position_category;


    public Post(String author, String author_name, String title, String text, String image, Date date, Boolean isLocal, long like_count, long dislike_count, ArrayList <Step> steps, ArrayList <String> likes_from_users, ArrayList <String> dislikes_from_users, String category, int position_category) {
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
        this.position_category = position_category;
    }
    public Post(String id, String author, String author_name, String title, String text, String image, Date date, Boolean isLocal, long like_count, long dislike_count, ArrayList <Step> steps, ArrayList <String> likes_from_users, ArrayList <String> dislikes_from_users, String category, int position_category) {
        this.id = id;
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
        this.position_category = position_category;
    }

    public Post() {}
    public Post(Post post) {
        this.id = UUID.randomUUID().toString();
        Date now = new Date();
        this.date = post.date;
        this.author = post.author;
        this.author_name = post.author_name;
        this.title = post.title;
        this.text = post.text;
        this.image = post.image;
        this.isLocal = post.isLocal;
        this.like_count = post.like_count;
        this.dislike_count = post.dislike_count;
        this.steps = post.steps;
        this.likes_from_users = post.likes_from_users;
        this.dislikes_from_users = post.dislikes_from_users;
        this.category = post.category;
        this.position_category = post.position_category;
    }

    public static final Comparator<Post> COUNT_OF_LIKES_COMPARATOR = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return (int) (o1.getLike_count() - o2.getLike_count());
        }
    };

    public static Post readSavedRecipe(File recipeDir) throws IOException {
        Post post = null;
        for (File file : Objects.requireNonNull(recipeDir.listFiles())) {
            if (file.getName().equals("main_file")) {
                ObjectMapper objectMapper = new ObjectMapper();
                post = objectMapper.readValue(file, Post.class);
            }
        }
        return post;
    }




    public static boolean saveRecipe(Post post, String dir_path, ContentResolver contentResolver, Context context) {
        File post_files = new File(dir_path, post.getId());
        post_files.mkdirs();
        String path = post_files.getAbsolutePath();
        File main = new File(path, "main_file");
        File main_image = new File(path, "main_image.jpeg");
        try {
            if (!Objects.equals(post.getImage(), "") && post.getImage() != null) {
                System.out.println(post.getImage());
                FileOutputStream outputStream = new FileOutputStream(main_image);
                Bitmap bitmap_main_image = null;
                try {
                    File image = new File(post.image);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bitmap_main_image = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                } catch (Exception e) {
                    Log.e("сохранение изображения", e.getMessage());
                }
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
                post.setImage(main_image.getAbsolutePath());
                outputStream.flush();
                outputStream.close();
            } else {
                FileOutputStream outputStream = new FileOutputStream(main_image);
                Bitmap bitmap_main_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.main_dishes);
                bitmap_main_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                main_image.createNewFile();
                post.setImage(main_image.getAbsolutePath());
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e("E", e.getMessage());
        }
        try {
            for (int i = 0; i < post.steps.size(); i++) {
                if (post.steps.get(i).getImagePath() != null) {
                    File step_image = new File(path, UUID.randomUUID().toString() + ".jpeg");
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
                    post.steps.get(i).setImagePath(step_image.getAbsolutePath());
                    outputStream.flush();
                    outputStream.close();
                }

            }
        } catch (Exception e) {
            Log.e("E", e.getMessage());
        }
        post.setIsLocal(true);
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
            System.out.println("создание mainfile");
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    public static void deletePost(String post_id, String dir_path) throws IOException {
        File dir = new File(dir_path);
        for (File file: dir.listFiles()) {
            if (file.getName().equals(post_id)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    FileUtils.deleteDirectory(file);
                }
            }
        }
    }

    public static void editPost(Post old_post, Post new_post, Activity activity) throws IOException {
        File dir = new File(activity.getFilesDir() + "/Recipes/" + old_post.id);
        Set<String> old_paths = new HashSet<>();
        Set<String> new_paths = new HashSet<>();
        old_paths.add(old_post.getImage());
        new_paths.add(new_post.getImage());
        for (int i = 0; i < old_post.steps.size(); i++) {
            old_paths.add(old_post.steps.get(i).getImagePath());
        }
        for (int i = 0; i < new_post.steps.size(); i++) {
            new_paths.add(new_post.steps.get(i).getImagePath());
        }
        Set<String> delete_paths = new HashSet<>(old_paths);
        Set<String> similar = new HashSet<>(old_paths);
//        Set<String> add_paths = new HashSet<>(new_paths);
//        add_paths.retainAll(old_paths);
        delete_paths.removeAll(new_paths);
        similar.retainAll(new_paths);
        Log.i("IIIIIIII", old_paths + "");
        Log.i("IIIIIIII", new_paths + "");
        Log.i("IIIIIIII", delete_paths + "");


        for (File file: dir.listFiles()) {
            if (file.getName().equals("main_file")) {
                file.delete();
            } else if (delete_paths.contains(file.getAbsolutePath())) {
                file.delete();
            }
        }
        File main = new File(dir.getAbsolutePath(), "main_file");
        File main_image = new File(dir.getAbsolutePath(), "main_image.jpeg");
        try {
            if (!Objects.equals(new_post.getImage(), "") && new_post.getImage() != null) {
                if (!similar.contains(new_post.getImage())) {
                    System.out.println(new_post.getImage());
                    System.out.println(similar);
                    FileOutputStream outputStream = new FileOutputStream(main_image);
                    Bitmap bitmap_main_image = null;
                    try {
                        File image = new File(new_post.image);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bitmap_main_image = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    } catch (Exception e) {
                        Log.e("сохранение изображения", e.getMessage());
                    }
                    try {
                        bitmap_main_image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.fromFile(new File(new_post.getImage())));
                    } catch (Exception e) {
                        Log.e("сохранение изображения", e.getMessage());
                    }
                    try {
                        bitmap_main_image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.parse(new_post.image));
                    } catch (Exception e) {
                        Log.e("сохранение изображения", e.getMessage());
                    }

                    bitmap_main_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    main_image.createNewFile();
                    new_post.setImage(main_image.getAbsolutePath());
                    System.out.println(main_image.getAbsolutePath());
                    outputStream.flush();
                    outputStream.close();
                }
            } else {
                FileOutputStream outputStream = new FileOutputStream(main_image);
                Bitmap bitmap_main_image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.main_dishes);
                bitmap_main_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                main_image.createNewFile();
                new_post.setImage(main_image.getAbsolutePath());
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e("E", e.getMessage());
        }

        for (int i = 0; i < new_post.steps.size(); i++) {
            if (new_post.steps.get(i).getImagePath() != null) {
                if (!similar.contains(new_post.steps.get(i).getImagePath())) {
                    File step_image = new File(dir.getAbsolutePath(), UUID.randomUUID().toString() + ".jpeg");
                    FileOutputStream outputStream = new FileOutputStream(step_image);
                    Bitmap bitmap_step_image = null;
                    try {
                        bitmap_step_image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.fromFile(new File(new_post.steps.get(i).getImagePath())));
                    } catch (Exception e) {
                        Log.e("сохранение изображения", e.getMessage());
                    }
                    try {
                        bitmap_step_image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.parse(new_post.steps.get(i).getImagePath()));
                    } catch (Exception e) {
                        Log.e("сохранение изображения", e.getMessage());
                    }
                    bitmap_step_image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    step_image.createNewFile();
                    new_post.steps.get(i).setImagePath(step_image.getAbsolutePath());
                    outputStream.flush();
                    outputStream.close();
                }
            }
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(new_post);
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(main));
            writer.print(json);
            writer.close();
        } catch (IOException e) {
            Log.e("e", e.getMessage());
        }
        try {
            main.createNewFile();
        } catch (IOException e) {
            Log.e("e", e.getMessage());
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