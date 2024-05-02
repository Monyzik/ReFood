package com.example.refood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    Context context;

    AdapterCallback callback;

    AdapterDeleteItemCallback deleteItemCallback;
    private final ArrayList<Step> steps;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText time;
        private final EditText info;
        private final ImageView foodImage;
        private final View delete;
        private final TextView number;


        public ViewHolder (View view) {
            super(view);
            time = view.findViewById(R.id.editText_timer);
            info = view.findViewById(R.id.editText_info_about_step);
            foodImage = view.findViewById(R.id.image_food);
            delete = view.findViewById(R.id.delete_button);
            number = view.findViewById(R.id.step_num_text);


        }

        public EditText getTime() {
            return time;
        }

        public ImageView getFoodImage() {
            return foodImage;
        }

        public EditText getInfo() {
            return info;
        }

        public View getDelete() {
            return delete;
        }

        public TextView getNumber() {
            return number;
        }

    }

    public StepsAdapter(ArrayList <Step> newSteps, Context context, AdapterCallback callback, AdapterDeleteItemCallback deleteItemCallback) {
        steps = newSteps;
        this.context = context;
        this.callback = callback;
        this.deleteItemCallback = deleteItemCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_step_for_recycler_view, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        String path = steps.get(position).getImagePath();

        if (path != null) {
            Glide.with(context).load(Uri.parse(path)).into(viewHolder.getFoodImage());
        } else {
            viewHolder.foodImage.setImageResource(R.drawable.example_of_food_photo);
        }

        steps.get(position).setNumber(position + 1);

        viewHolder.getNumber().setText(String.valueOf(steps.get(position).getNumber()));
        viewHolder.getTime().setText(String.valueOf(steps.get(position).getTime()));
        viewHolder.getInfo().setText(steps.get(position).getInfo());

        viewHolder.getInfo().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                steps.get(viewHolder.getAdapterPosition()).setInfo(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewHolder.getTime().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                steps.get(viewHolder.getAdapterPosition()).setTime(s.toString());
                System.out.println(viewHolder.getAdapterPosition());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewHolder.getFoodImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.getAdapterPosition() != -1) {
                    callback.onMethodCallback(path, viewHolder.getAdapterPosition());
                }
            }
        });
        
        viewHolder.getDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.getAdapterPosition() != -1) {
                    deleteItemCallback.deleteAt(viewHolder.getAdapterPosition());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public interface AdapterCallback {
        void onMethodCallback(String data, int position);
    }

    public interface AdapterDeleteItemCallback {
        void deleteAt(int position);
    }

}
