package com.example.refood;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddProductDialog extends BottomSheetDialogFragment {

    private final int GALLERY_REQ_CODE = 1000;

    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;

    RecyclerView recyclerView;


    FirebaseFirestore db;

    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ArrayList <Step> steps = new ArrayList<>();

        Button apply_button = v.findViewById(R.id.apply_button);
        imageView = v.findViewById(R.id.imageView);
        EditText title = v.findViewById(R.id.editText_title);
        EditText info = v.findViewById(R.id.editText_info);
        View image_group = v.findViewById(R.id.image_group);
        View add_step_button = v.findViewById(R.id.add_step_button);
        recyclerView = v.findViewById(R.id.recycler_steps);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        StepsAdapter stepsAdapter = new StepsAdapter(steps);
        recyclerView.setAdapter(stepsAdapter);

        add_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps.add(new Step(steps.size() + 1));
                recyclerView.getAdapter().notifyDataSetChanged();

            }
        });

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Post post = new Post();
                if (!(title.getText().toString().equals("") || info.getText().toString().equals(""))) {

//                    final String[] post_id = new String[1];
                    final int[] post_id = {0};
                    db.collection(Post.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                post_id[0] = task.getResult().size() + 1;
                            }
                        }
                    });


//                    StorageReference storageReference = storage.getReference("images/posts_images/");
//                    StorageReference postReference = storageReference.child(post_id[0]);
//
                    for (int x = recyclerView.getChildCount(), i = 0; i < x; i++) {
                        StepsAdapter.ViewHolder holder = (StepsAdapter.ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                        Step step = steps.get(i);
                        step.setInfo(holder.getInfo().getText().toString());
//                        StorageReference stepPhotoReference = postReference.child("Step" + step.getNumber());
//                        stepPhotoReference.putFile()
//                        step.setImagePath(stepPhotoReference.getPath());
                    }


                    db.collection(User.COLLECTION_NAME).document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);
                                Post post = new Post(post_id[0] + "", user.name, title.getText().toString(), info.getText().toString(), "Some dirs", new Date(), 0, 0,
                                        steps, new ArrayList<>(), new ArrayList<>());





//                                db.collection(Post.COLLECTION_NAME).document(post_id[0]).set(post);
//                                TapeFragment.updateRecyclerViewUI(v);
                                dismiss();
                            }
                        }
                    });



                    post.setId(post_id[0] + "");
                    post.setTitle(title.getText().toString());
                    post.setText(info.getText().toString());
                    post.setImage("Some dirs");
                    post.setDate(new Date() + "");
                    post.setLike_count(0);
                    post.setDislike_count(0);
                    post.setSteps(steps);

                    System.out.println(post_id[0]);

                    if (saveRecipe(post)) {
                        System.out.println("успешно сохранено");
                    } else {
                        System.out.println("Ошибка!!!!!!!!!!!");
                    }

                    File post_file = new File(getContext().getFilesDir() + "/Recipes", post.getId());
                    try {
                        Post post1 = readSavedRecipe(post_file);
                        System.out.println(post1.getId() + "");
                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }


//                    List<Object> lstObject = gson.fromJson(, Object.class);
                } else {
                    Toast.makeText(v.getContext(), R.string.fill, Toast.LENGTH_SHORT).show();
                }


            }
        });
//        image_group.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent iGallery = new Intent(Intent.ACTION_PICK);
//                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(iGallery, GALLERY_REQ_CODE);
//            }
//        });


        return v;
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == -1) {
//            if (requestCode == GALLERY_REQ_CODE) {
//                imageView.setImageURI(data.getData());
//            }
//
//        }
//    }


    public boolean saveRecipe(Post post) {
        Gson gson = new Gson();
        String json = gson.toJson(post);
        File post_file = new File(getContext().getFilesDir() + "/Recipes", post.getId());
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(post_file));
            writer.print(json);
            writer.close();
        } catch (IOException e) {
            return false;
        }
        try {
            post_file.createNewFile();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public Post readSavedRecipe(File file) throws IOException, JSONException {
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String json_string = reader.readLine();
        ObjectMapper objectMapper = new ObjectMapper();
        Post post = objectMapper.readValue(json_string, Post.class);
        return post;
    }


}