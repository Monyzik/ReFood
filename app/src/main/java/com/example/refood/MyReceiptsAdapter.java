package com.example.refood;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class MyReceiptsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList <Post> posts;

    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private final Activity activity;
    private final Context context;
    private final boolean flag_add_button;

    private final FragmentManager fragmentManager;

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView foodImage, isLocalImage;

        public ViewHolderItem(View view) {
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

    public static class ViewHolderAddButton extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolderAddButton(View view) {
            super(view);
        }
    }

    public MyReceiptsAdapter(ArrayList <Post> newposts, Context context, Activity newactivity, FragmentManager fragmentManager, boolean flag_add_button) {
        this.posts = newposts;
        this.context = context;
        activity = newactivity;
        this.fragmentManager = fragmentManager;
        this.flag_add_button = flag_add_button;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View itemLayoutView;
        switch (viewType)
        {
            case 0:
                itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_reciept_for_recycler_view, viewGroup, false);
                vh = new ViewHolderItem(itemLayoutView);
                break;
            case 1:
                itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_add_button_for_recycler_view, viewGroup, false);
                vh = new ViewHolderAddButton(itemLayoutView);
                break;
        }
        return (RecyclerView.ViewHolder) vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position < posts.size()){
            ViewHolderItem viewHolder = (ViewHolderItem) holder;
            storage = FirebaseStorage.getInstance();
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();

            viewHolder.getTitle().setText(posts.get(position).getTitle());
            String path = posts.get(position).getImage();
            boolean isLocal = posts.get(position).getIsLocal();
            if (isLocal) {
                viewHolder.getIsLocalImage().setImageResource(R.drawable.baseline_cloud_off_24);
            } else {
                viewHolder.getIsLocalImage().setImageResource(R.drawable.baseline_cloud_queue);
            }
            if (!Objects.equals(path, "") && path != null) {
                if (isLocal) {
                    viewHolder.getFoodImage().setImageURI(Uri.parse(path));
                } else {
                    StorageReference postImagePhoto = storage.getReference(path);
                    postImagePhoto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

            viewHolder.getIsLocalImage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
                    builder.setTitle(R.string.dialog_load_message);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Post post = posts.get(viewHolder.getAdapterPosition());
                            boolean isLocal = post.getIsLocal();
                            if (isLocal) {
                                post.setIsLocal(false);
                                post.setAuthor(auth.getCurrentUser().getUid());
                                db.collection(User.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            post.setAuthor_name(user.getName());
                                            StorageReference storageReference = storage.getReference().child("images").child("posts_images");
                                            StorageReference postStorage = storageReference.child(post.getId());
                                            StorageReference mainImageStorage = postStorage.child("main_image.jpeg");
                                            InputStream stream;
                                            try {
                                                stream = new FileInputStream(post.getImage());
                                            } catch (FileNotFoundException e) {
                                                throw new RuntimeException(e);
                                            }
                                            UploadTask uploadTask = mainImageStorage.putStream(stream);
                                            post.setImage(mainImageStorage.getPath());
                                            for (Step step : post.getSteps()) {
                                                if (!Objects.equals(step.getImagePath(), " ") && step.getImagePath() != null) {
                                                    StorageReference stepStorage = postStorage.child("step" + step.getNumber() + ".jpeg");
                                                    try {
                                                        stream = new FileInputStream(step.getImagePath());
                                                    } catch (FileNotFoundException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    uploadTask = stepStorage.putStream(stream);
                                                    step.setImagePath(stepStorage.getPath());
                                                }
                                            }
                                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                                }
                                            });
                                            db.collection(Post.COLLECTION_NAME).document(post.getId()).set(post);
                                            posts.set(viewHolder.getAdapterPosition(), post);
                                        }
                                    }
                                });
                            } else {
                                db.collection(Post.COLLECTION_NAME).document(post.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        StorageReference mainImageReference = storage.getReference(post.getImage());
                                        mainImageReference.delete();
                                        for (Step step : post.getSteps()) {
                                            if (step.getImagePath() != null && !Objects.equals(step.getImagePath(), "")) {
                                                StorageReference stepImageReference = storage.getReference(step.getImagePath());
                                                stepImageReference.delete();
                                            }
                                        }
                                        try {
                                            posts.set(viewHolder.getAdapterPosition(), Post.readSavedRecipe(new File(context.getFilesDir(), "Recipes/" + post.getId())));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        notifyItemChanged(viewHolder.getAdapterPosition());
                                        Toast.makeText(context, "Общий доступ успешно отзван", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });

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
        else {
            ViewHolderAddButton viewHolder = (ViewHolderAddButton) holder;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddProductDialog bottomSheet = new AddProductDialog();
                    bottomSheet.show(fragmentManager,"ModalBottomSheet");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (flag_add_button) {
            return posts.size() + 1;
        }
        return posts.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == posts.size())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
