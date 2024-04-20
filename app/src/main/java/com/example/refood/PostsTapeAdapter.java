package com.example.refood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostsTapeAdapter extends RecyclerView.Adapter<PostsTapeAdapter.ViewHolder> {
    private ArrayList <Post> posts;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView author;
        private final ImageView foodImage;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.recycler_view_item_title);
            author = view.findViewById(R.id.recycler_view_item_author);
            foodImage = view.findViewById(R.id.recycler_view_item_image);
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

    public PostsTapeAdapter(ArrayList <Post> newposts) {
        posts = newposts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_post_for_recycler_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTitle().setText(posts.get(position).getTitle());
        viewHolder.getAuthor().setText(posts.get(position).getAuthor());
        viewHolder.getFoodImage().setImageResource(R.drawable.example_of_food_photo);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
