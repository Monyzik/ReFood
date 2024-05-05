package com.example.refood;

import android.content.Context;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class MyReceiptsAdapter extends RecyclerView.Adapter<MyReceiptsAdapter.ViewHolder> {
    private ArrayList <Post> posts;

    private FirebaseStorage storage;

    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView foodImage, isLocalImage;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.myrecieptTitle);
            foodImage = view.findViewById(R.id.myrecieptImage);
            isLocalImage = view.findViewById(R.id.isLocalCloud);
        }

        public ImageView getIsLocalImage() {
            return isLocalImage;
        }

        public TextView getTitle() {
            return title;
        }

        public ImageView getFoodImage() {
            return foodImage;
        }

    }

    public MyReceiptsAdapter(ArrayList <Post> newposts, Context context) {
        this.posts = newposts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_my_reciept_for_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTitle().setText(posts.get(position).getTitle());
        String path = posts.get(position).getImage();
        System.out.println(posts.get(position).getIsLocal());
        boolean isLocal = posts.get(position).getIsLocal();
        if (isLocal) {
            viewHolder.getIsLocalImage().setImageResource(R.drawable.baseline_cloud_off_24);
        } else {
            viewHolder.getIsLocalImage().setImageResource(R.drawable.baseline_cloud_queue);
        }
        if (path != null) {
            if (isLocal) {
                viewHolder.getFoodImage().setImageURI(Uri.parse(path));
            } else {
                storage = FirebaseStorage.getInstance();
                StorageReference profileAvatarReference = storage.getReference(path);
                profileAvatarReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = uri.toString();
                        Glide.with(context).load(imageURL).into(viewHolder.getFoodImage());
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
