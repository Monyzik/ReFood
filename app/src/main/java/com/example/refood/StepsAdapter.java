package com.example.refood;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    private ArrayList<Step> steps;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText time;
        private final EditText info;
        private final ImageView foodImage;
        private final View delete;
        private final TextView number;


        public ViewHolder (View view) {
            super(view);
            time = view.findViewById(R.id.editText_timer);
            info = view.findViewById(R.id.editText_info);
            foodImage = view.findViewById(R.id.image_food);
            delete = view.findViewById(R.id.delete_button);
            number = view.findViewById(R.id.step_num_text);


        }

        public TextView getTime() {
            return time;
        }

        public ImageView getFoodImage() {
            return foodImage;
        }

        public TextView getInfo() {
            return info;
        }

        public View getDelete() {
            return delete;
        }

        public TextView getNumber() {
            return number;
        }

    }

    public StepsAdapter(ArrayList <Step> newsteps) {
        steps = newsteps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_step_for_recycler_view, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.foodImage.setImageResource(R.drawable.example_of_food_photo);
        viewHolder.getNumber().setText(position + 1 + "");
        viewHolder.getDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps.remove(position);
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
