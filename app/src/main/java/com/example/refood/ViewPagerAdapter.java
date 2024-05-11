package com.example.refood;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Objects;

public class ViewPagerAdapter extends RecyclerView.Adapter<com.example.refood.ViewPagerAdapter.ViewPagerViewHolder> {
    private ArrayList <Post> posts;
    private Activity activity;

    FirebaseAuth auth;


    public static class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView author;
        private final ImageView foodImage;
        private final TextView num_likes;

        private final ImageView like_image;


        public ViewPagerViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title_popular_recipe);
            author = view.findViewById(R.id.author_name_popular_recipe);
            foodImage = view.findViewById(R.id.image_food_popular_recipe);
            num_likes = view.findViewById(R.id.likes_num_popular_recipe);
            like_image = view.findViewById(R.id.like_image_view_pager);

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

        public ImageView getLike_image() {
            return like_image;
        }
    }

    public ViewPagerAdapter(ArrayList <Post> posts, Activity activity) {
        this.posts = posts;
        this.activity = activity;
    }

    @Override
    public com.example.refood.ViewPagerAdapter.ViewPagerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_popular_recipe_for_view_pager, viewGroup, false);

        return new com.example.refood.ViewPagerAdapter.ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewPagerViewHolder viewHolder, final int position) {
        Post post = posts.get(position);
        auth = FirebaseAuth.getInstance();
        viewHolder.author.setText(posts.get(viewHolder.getAdapterPosition()).getAuthor_name());
        viewHolder.num_likes.setText(posts.get(viewHolder.getAdapterPosition()).getLike_count() + "");
        viewHolder.title.setText(posts.get(viewHolder.getAdapterPosition()).getTitle());

        if (post.getLikes_from_users().contains(auth.getCurrentUser().getUid())) {
            viewHolder.getLike_image().setImageResource(R.drawable.baseline_thumb_up_filled);
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
        String image_path = posts.get(viewHolder.getAdapterPosition()).getImage();
        if (!Objects.equals(image_path, "")) {
            if (posts.get(viewHolder.getAdapterPosition()).getIsLocal()) {
                viewHolder.getFoodImage().setImageURI(Uri.parse(image_path));
            } else {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference mainImageReference = storage.getReference(image_path);
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
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
