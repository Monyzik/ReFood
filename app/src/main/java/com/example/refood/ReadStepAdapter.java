package com.example.refood;

import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ReadStepAdapter extends RecyclerView.Adapter<ReadStepAdapter.ViewHolder> {
    ArrayList<Step> steps;
    private FirebaseStorage storage;

    Context context;

    private final boolean isLocal;

    public ReadStepAdapter(ArrayList<Step> steps, boolean isLocal, Context context) {
        this.steps = steps;
        this.isLocal = isLocal;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_read_recipe_for_recycler, parent, false);
        return new ReadStepAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNum().setText(String.valueOf(position + 1));
        if (Objects.equals(steps.get(position).getInfo(), "") || steps.get(position).getInfo() == null) {
            holder.getInfo().setVisibility(View.GONE);
        } else {
            holder.getInfo().setText(steps.get(holder.getAdapterPosition()).getInfo());
        }
        if (steps.get(holder.getAdapterPosition()).getImagePath() != null) {
            holder.getCardView().setVisibility(View.VISIBLE);
            if (isLocal) {
                holder.getFoodImage().setImageURI(Uri.parse(steps.get(holder.getAdapterPosition()).getImagePath()));
            } else {
                storage = FirebaseStorage.getInstance();
                StorageReference stepImageReference = storage.getReference(steps.get(holder.getAdapterPosition()).getImagePath());
                stepImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = String.valueOf(uri);
                        Glide.with(context).load(imageURL).into(holder.getFoodImage());
                    }
                });
            }
        }
        if (Objects.equals(steps.get(position).getTime(), "") || steps.get(position).getTime() == null) {
            holder.getTime().setVisibility(View.GONE);
            holder.time_title.setVisibility(View.GONE);
        } else {
            holder.getTime().setText(steps.get(holder.getAdapterPosition()).getTime());
        }

    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView num;
        private final TextView info;
        private final ImageView foodImage;
        private final View cardView;
        private final TextView time_num;
        private final TextView time_title;

        public ViewHolder(View view) {
            super(view);
            num = view.findViewById(R.id.step_num_read);
            info = view.findViewById(R.id.info_read);
            foodImage = view.findViewById(R.id.food_image_read);
            cardView = view.findViewById(R.id.card_view_food_image_read);
            time_num = view.findViewById(R.id.time_num);
            time_title = view.findViewById(R.id.time);
        }

        public ImageView getFoodImage() {
            return foodImage;
        }

        public TextView getInfo() {
            return info;
        }

        public TextView getNum() {
            return num;
        }

        public TextView getTime() {
            return time_num;
        }

        public View getCardView() {
            return cardView;
        }

    }
}
