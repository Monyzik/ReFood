package com.example.refood;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddProductDialog extends BottomSheetDialogFragment {

    private final int GALLERY_REQ_CODE = 1000;
    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

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
                        TapeFragment.posts.add(new Post("Name user", title.getText().toString(), info.getText().toString(), imageView));
                        TapeFragment.posts_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    dismiss();
                } else {
                    Toast.makeText(v.getContext(), R.string.fill, Toast.LENGTH_SHORT).show();
                }
            }
        });
        image_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == GALLERY_REQ_CODE) {
                imageView.setImageURI(data.getData());
            }

        }
    }

}