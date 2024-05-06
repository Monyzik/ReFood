package com.example.refood;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReadStepAdapter extends RecyclerView.Adapter<ReadStepAdapter.ViewHolder> {
    ArrayList<Step> steps;

    public ReadStepAdapter(ArrayList<Step> steps) {
        this.steps = steps;
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
        holder.num.setText(position + 1 + "");
        holder.info.setText(steps.get(holder.getAdapterPosition()).getInfo());
        if (steps.get(holder.getAdapterPosition()).getImagePath() != null){
            holder.cardView.setVisibility(View.VISIBLE);
            holder.foodImage.setImageURI(Uri.parse(steps.get(holder.getAdapterPosition()).getImagePath()));
        }
        holder.time.setText(steps.get(holder.getAdapterPosition()).getTime());

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
        private final TextView time;

        public ViewHolder(View view) {
            super(view);
            num = view.findViewById(R.id.step_num_read);
            info = view.findViewById(R.id.info_read);
            foodImage = view.findViewById(R.id.food_image_read);
            cardView = view.findViewById(R.id.card_view_food_image_read);
            time = view.findViewById(R.id.time_num);
        }
    }
}
