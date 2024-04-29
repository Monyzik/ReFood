package com.example.refood;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import io.getstream.avatarview.AvatarView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int GALLERY_REQ_CODE = 1000;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;

    TextView singoutTextView;

    ImageView avatarView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void signOut() {
        if (firebaseAuth.getCurrentUser() != null) {
            Log.e("logoutuser", firebaseAuth.getCurrentUser().toString());
            FirebaseAuth.getInstance().signOut();
        } else {
            Log.e("sing out exception", "can't log out user");
        }
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        super.onViewCreated(view, savedInstanceState);
        avatarView = view.findViewById(R.id.image_view_profile);
        singoutTextView = view.findViewById(R.id.logout);
//        Picasso.with(view.getContext()).load(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhotoUrl()).into(avatarView);

        System.out.println(firebaseAuth.getCurrentUser().getUid());

        db.collection(User.COLLECTION_NAME).document(Objects.requireNonNull(firebaseAuth.getCurrentUser().getUid())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user.avatar_path != null) {
                        StorageReference profileAvatarReference = storage.getReference(user.avatar_path);
                        Glide.with(view.getContext()).load(profileAvatarReference).into(avatarView);
                    } else {
                        avatarView.setImageResource(R.drawable.baseline_person);
                    }
            }
        });
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });

//        avatarView.setImageURI(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhotoUrl());

        singoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == GALLERY_REQ_CODE) {
                StorageReference storageReference = storage.getReference().child("images/users_avatars/");
                StorageReference storageReferenceUserAvatar = storageReference.child(firebaseAuth.getCurrentUser().getUid());
                StorageReference avatarReference = storageReferenceUserAvatar.child(firebaseAuth.getCurrentUser().getUid());
                db.collection(User.COLLECTION_NAME).document(firebaseAuth.getCurrentUser().getUid()).update("avatar_path", avatarReference.getPath());
                avatarReference.putFile(data.getData());
                avatarView.setImageURI(data.getData());
            }

        }
    }
}