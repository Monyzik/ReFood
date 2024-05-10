package com.example.refood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Objects;

public class PostsTapeAdapter extends RecyclerView.Adapter<PostsTapeAdapter.ViewHolder> {
    private ArrayList <Post> posts;

    FirebaseStorage storage;

    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView author;
        private final ImageView foodImage;
        private final TextView like_count;
        private final TextView dislike_count;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.recycler_view_item_title);
            author = view.findViewById(R.id.recycler_view_item_author);
            foodImage = view.findViewById(R.id.recycler_view_item_image);
            like_count = view.findViewById(R.id.like_count);
            dislike_count = view.findViewById(R.id.dislike_count);
        }

        public TextView getTitle() {
            return title;
        }

        public ImageView getFoodImage() {
            return foodImage;
        }

        public TextView getAuthor() {
            return author;
        }
    }

    public PostsTapeAdapter(ArrayList <Post> newposts, Activity newactivity) {
        posts = newposts;
        activity = newactivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_post_for_recycler_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTitle().setText(posts.get(position).getTitle());
        viewHolder.getAuthor().setText(posts.get(position).getAuthor_name());
        Post post = posts.get(position);
        viewHolder.like_count.setText(post.getLike_count() + "");
        viewHolder.dislike_count.setText(post.getDislike_count() + "");
        String image_path = post.getImage();
        if (!Objects.equals(image_path, "")) {
            if (post.getIsLocal()) {
                viewHolder.getFoodImage().setImageURI(Uri.parse(image_path));
            } else {
                storage = FirebaseStorage.getInstance();
                StorageReference mainImageReference = storage.getReference(post.getImage());
                mainImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = String.valueOf(uri);
                        Glide.with(activity.getApplicationContext()).load(imageURL).into(viewHolder.getFoodImage());
                    }
                });
            }
        } else {
            viewHolder.getFoodImage().setImageResource(R.drawable.example_of_food_photo);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ReadOtherRecipe.class);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setPrettyPrinting();
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(posts.get(viewHolder.getAdapterPosition()));
                i.putExtra("post", json);
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
