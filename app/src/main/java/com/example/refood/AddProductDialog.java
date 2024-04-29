package com.example.refood;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddProductDialog extends BottomSheetDialogFragment {

    private final int GALLERY_REQ_CODE = 1000;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

        firebaseAuth = FirebaseAuth.getInstance();


        FloatingActionButton apply_button = v.findViewById(R.id.apply_button);
        imageView = v.findViewById(R.id.imageView);
        Spinner spinner = v.findViewById(R.id.category);
        EditText title = v.findViewById(R.id.label);
        EditText info = v.findViewById(R.id.info);
        View image_group = v.findViewById(R.id.image_group);

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(title.getText().toString().equals("") || info.getText().toString().equals(""))){
                    db.collection(User.COLLECTION_NAME).document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);
                                Post post = new Post(user.name, title.getText().toString(), info.getText().toString(), "Some dirs", 0, 0, new ArrayList<>(), new ArrayList<>());
                                db.collection(Post.COLLECTION_NAME).document().set(post);
                                TapeFragment.updateRecyclerViewUI(v);
                                dismiss();
                            }
                        }
                    });
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

}