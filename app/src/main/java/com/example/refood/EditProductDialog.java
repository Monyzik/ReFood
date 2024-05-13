package com.example.refood;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class EditProductDialog extends BottomSheetDialogFragment {

    private final int GALLERY_REQ_CODE = 1000;
    private final int GALLERY_REQ_CODE_MAIN_IMAGE = 1001;

    public static Bundle mMyFragmentBundle = new Bundle();

    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    String image_path;
    RecyclerView recyclerView;
    StepsAdapter stepsAdapter;
    ArrayList <Step> steps;
    FirebaseFirestore db;
    ImageView imageView;
    UpdateCall updateCall;
    Post post_in;

    public EditProductDialog(Post post_in, UpdateCall updateCall) {
        this.post_in = post_in;
        this.updateCall = updateCall;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Spinner spinner = v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories_food, R.layout.item_for_category_spinner);
        spinner.setAdapter(spinnerAdapter);

        Button apply_button = v.findViewById(R.id.apply_button);
        imageView = v.findViewById(R.id.imageView);
        EditText title = v.findViewById(R.id.editText_title);
        EditText info = v.findViewById(R.id.editText_info);
        ImageView imageView = v.findViewById(R.id.imageView);
        View add_step_button = v.findViewById(R.id.add_step_button);
        recyclerView = v.findViewById(R.id.recycler_steps);


        System.out.println("Ссылка " + post_in.image);
        if (post_in.getIsLocal()) {
            imageView.setImageURI(Uri.parse(post_in.image));
        } else {
            Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setDuration(1800).setBaseAlpha(0.7f).setHighlightAlpha(0.6f)
                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT).setAutoStart(true).build();
            ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
            shimmerDrawable.setShimmer(shimmer);
            imageView.setImageDrawable(shimmerDrawable);
            StorageReference mainImageReference = storage.getReference(post_in.getImage());
            mainImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = String.valueOf(uri);
                    Glide.with(getActivity().getApplicationContext()).load(imageURL).placeholder(shimmerDrawable).into(imageView);
                }
            });
        }


        title.setText(post_in.getTitle());
        info.setText(post_in.getText());
        spinner.setSelection((post_in.position_category));
        steps = post_in.steps;
        image_path = post_in.image;


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsAdapter = new StepsAdapter(steps, v.getContext(), new StepsAdapter.AdapterCallback() {
            @Override
            public void onMethodCallback(String data, int position) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mMyFragmentBundle.putInt("pos", position);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);

            }
        }, new StepsAdapter.AdapterDeleteItemCallback() {
            @Override
            public void deleteAt(int position) {
                steps.remove(position);
                stepsAdapter.notifyItemRemoved(position);
                stepsAdapter.notifyItemRangeChanged(position, steps.size());
            }
        });
        recyclerView.setAdapter(stepsAdapter);

        add_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps.add(new Step("", steps.size() + 1, "", null));
                stepsAdapter.notifyItemInserted(steps.size() - 1);
            }
        });

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!(title.getText().toString().equals("") || info.getText().toString().equals("") || image_path.equals(""))) {
                    db.collection(Post.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (int x = recyclerView.getChildCount(), i = 0; i < x; i++) {
                                    StepsAdapter.ViewHolder holder = (StepsAdapter.ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                                    Step step = steps.get(i);
                                    step.setInfo(holder.getInfo().getText().toString());
                                    step.setTime(holder.getTime().getText().toString());
                                    step.setTime(holder.getTime().getText().toString());
                                    step.setNumber(i + 1);
                                }


                                Post post = new Post(post_in.getId(), "i'm", "me", title.getText().toString(), info.getText().toString(), image_path, new Date(), post_in.getIsLocal(), post_in.like_count, post_in.dislike_count, steps, post_in.likes_from_users, post_in.dislikes_from_users, spinner.getSelectedItem() +"", spinner.getSelectedItemPosition());

                                try {
                                    Post.editPost(post_in, post, getActivity());
                                } catch (IOException e) {
                                    Log.e("e", e.getMessage());
                                }
                                try {
                                    post = Post.readSavedRecipe(new File(getActivity().getFilesDir() + "/Recipes/" + post.getId()));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                if (!post.getIsLocal()) {
                                    db.collection(Post.COLLECTION_NAME).document(post.getId()).set(post);
                                }
                                updateCall.update(post);

                                dismiss();
                            } else {
                                Toast.makeText(v.getContext(), R.string.fill, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE_MAIN_IMAGE);
            }
        });


        return v;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == GALLERY_REQ_CODE && data != null) {
                int pos = mMyFragmentBundle.getInt("pos");
                steps.get(pos).setImagePath(data.getData().toString());
                stepsAdapter.notifyItemChanged(pos);
            } else if (requestCode == GALLERY_REQ_CODE_MAIN_IMAGE && data != null) {
                image_path = data.getData().toString();
                imageView.setImageURI(data.getData());
            }

        }
    }
    public interface UpdateCall {
        void update(Post post);
    }




}
