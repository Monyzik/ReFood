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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    FirebaseFirestore db;
    FirebaseAuth auth;

    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView author;
        private final ImageView foodImage;
        private final TextView like_count;
        private final TextView dislike_count;
        private final ConstraintLayout likes_group;
        private final ConstraintLayout dislikes_group;
        private final ImageView like_image;
        private final ImageView dislike_image;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.recycler_view_item_title);
            author = view.findViewById(R.id.recycler_view_item_author);
            foodImage = view.findViewById(R.id.recycler_view_item_image);

            likes_group = view.findViewById(R.id.likes_group);
            dislikes_group = view.findViewById(R.id.dislikes_group);

            like_count = view.findViewById(R.id.like_count);
            dislike_count = view.findViewById(R.id.dislike_count);

            like_image = view.findViewById(R.id.like_image);
            dislike_image = view.findViewById(R.id.dislike_image);
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

        public ConstraintLayout getDislikes_group() {
            return dislikes_group;
        }

        public ConstraintLayout getLikes_group() {
            return likes_group;
        }

        public TextView getDislike_count() {
            return dislike_count;
        }

        public TextView getLike_count() {
            return like_count;
        }

        public ImageView getDislike_image() {
            return dislike_image;
        }

        public ImageView getLike_image() {
            return like_image;
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
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        viewHolder.getTitle().setText(posts.get(position).getTitle());
        viewHolder.getAuthor().setText(posts.get(position).getAuthor_name());
        Post post = posts.get(position);
        viewHolder.like_count.setText(String.valueOf(post.getLike_count()));
        viewHolder.dislike_count.setText(String.valueOf(post.getDislike_count()));
        String image_path = post.getImage();
        if (post.getLikes_from_users().contains(auth.getCurrentUser().getUid())) {
            viewHolder.getLike_image().setImageResource(R.drawable.baseline_thumb_up_filled);
        } else if (post.getDislikes_from_users().contains(auth.getCurrentUser().getUid())) {
            viewHolder.getDislike_image().setImageResource(R.drawable.baseline_thumb_down_filled);
        }
        if (!Objects.equals(image_path, "")) {
            if (post.getIsLocal()) {
                viewHolder.getFoodImage().setImageURI(Uri.parse(image_path));
            } else {
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


        viewHolder.getLikes_group().setOnClickListener(new View.OnClickListener() {
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
                            viewHolder.getDislike_image().setImageResource(R.drawable.baseline_dislike);
                            viewHolder.getLike_image().setImageResource(R.drawable.baseline_thumb_up_filled);
                        } else if (isLiked) {
                            likedPosts.remove(post.getId());
                            likes_from_users.remove(auth.getCurrentUser().getUid());
                            viewHolder.getLike_image().setImageResource(R.drawable.baseline_like);
                        } else {
                            likedPosts.add(post.getId());
                            likes_from_users.add(auth.getCurrentUser().getUid());
                            viewHolder.getLike_image().setImageResource(R.drawable.baseline_thumb_up_filled);
                        }
                        db.collection(Post.COLLECTION_NAME).document(post.getId()).update("dislikes_from_users", dislikedPosts, "likes_from_users", likes_from_users,
                                "like_count", likes_from_users.size(), "dislike_count", dislikes_from_users.size());
                        db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).update("dislike_posts", dislikedPosts, "like_posts", likedPosts);
                        viewHolder.getLike_count().setText(String.valueOf(likes_from_users.size()));
                        viewHolder.getDislike_count().setText(String.valueOf(dislikes_from_users.size()));
                    }
                });
            }
        });

        viewHolder.getDislikes_group().setOnClickListener(new View.OnClickListener() {
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
                            viewHolder.getDislike_image().setImageResource(R.drawable.baseline_thumb_down_filled);
                            viewHolder.getLike_image().setImageResource(R.drawable.baseline_like);
                        } else if (isDisliked) {
                            dislikedPosts.remove(post.getId());
                            dislikes_from_users.remove(auth.getCurrentUser().getUid());
                            viewHolder.getDislike_image().setImageResource(R.drawable.baseline_dislike);
                        } else {
                            dislikedPosts.add(post.getId());
                            dislikes_from_users.add(auth.getCurrentUser().getUid());
                            viewHolder.getDislike_image().setImageResource(R.drawable.baseline_thumb_down_filled);
                        }
                        db.collection(Post.COLLECTION_NAME).document(post.getId()).update("dislikes_from_users", dislikedPosts, "likes_from_users", likes_from_users,
                                "like_count", likes_from_users.size(), "dislike_count", dislikes_from_users.size());
                        db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).update("dislike_posts", dislikedPosts, "like_posts", likedPosts);
                        viewHolder.getLike_count().setText(String.valueOf(likes_from_users.size()));
                        viewHolder.getDislike_count().setText(String.valueOf(dislikes_from_users.size()));
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
