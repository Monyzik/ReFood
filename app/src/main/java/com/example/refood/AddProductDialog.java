package com.example.refood;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AddProductDialog extends BottomSheetDialogFragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        steps = new ArrayList<>();

        Button apply_button = v.findViewById(R.id.apply_button);
        imageView = v.findViewById(R.id.imageView);
        EditText title = v.findViewById(R.id.editText_title);
        EditText info = v.findViewById(R.id.editText_info);
        ImageView imageView = v.findViewById(R.id.imageView);
        View add_step_button = v.findViewById(R.id.add_step_button);
        recyclerView = v.findViewById(R.id.recycler_steps);

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
                if (!(title.getText().toString().equals("") || info.getText().toString().equals(""))) {
                    db.collection(Post.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String post_id = "id" + (task.getResult().size() + 1);

//                    StorageReference storageReference = storage.getReference("images/posts_images/");
//                    StorageReference postReference = storageReference.child(post_id[0]);

                                for (int x = recyclerView.getChildCount(), i = 0; i < x; i++) {
                                    StepsAdapter.ViewHolder holder = (StepsAdapter.ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                                    Step step = steps.get(i);
                                    step.setInfo(holder.getInfo().getText().toString());
                                    step.setTime("");
                                    step.setNumber(i + 1);
//                        StorageReference stepPhotoReference = postReference.child("Step" + step.getNumber());
//                        stepPhotoReference.putFile()
//                        step.setImagePath(stepPhotoReference.getPath());
                                }


                                db.collection(User.COLLECTION_NAME).document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            Post post = new Post(user.name, title.getText().toString(), info.getText().toString(), image_path, new Date(), true, 0, 0,
                                                    steps, new ArrayList<>(), new ArrayList<>());


//                                db.collection(Post.COLLECTION_NAME).document(post_id[0]).set(post);
//                                TapeFragment.updateRecyclerViewUI(v);
                                            dismiss();
                                        }
                                    }
                                });
                                File recipes = new File(getContext().getFilesDir() + "/Recipes");
                                String post_id_internal = "post_id_" + Objects.requireNonNull(recipes.listFiles()).length;
                                Post post = new Post("i'm",  title.getText().toString(), info.getText().toString(), image_path, new Date(), true, 0, 0, steps, new ArrayList<>(), new ArrayList<>());
                                File post_files = new File(getContext().getFilesDir() + "/Recipes/" + post.getId());
                                File post_file = new File(getContext().getFilesDir() + "/Recipes/" + post.getId() + "/main_file");
                                post_files.mkdirs();
                                if (Post.saveRecipe(post, post_files.getAbsolutePath(), getContext().getContentResolver(), getContext())) {
                                    System.out.println("успешно сохранено");
                                } else {
                                    System.out.println("Ошибка!!!!!!!!!!!");
                                }
                                System.out.println(post_file.getPath());
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



}