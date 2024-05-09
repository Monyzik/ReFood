package com.example.refood;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import io.getstream.avatarview.AvatarView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int GALLERY_REQ_CODE = 1000;
    private String mParam1;
    private String mParam2;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    TextView usernameTextView, myReceiptsTextView;
    ArrayList<Post> posts = new ArrayList<>();
    RecyclerView myRecieptsRecyclerView;
    MyReceiptsAdapter myReceiptsAdapter;
    ImageView avatarView, bigAvatarImage, singOut;

    public ProfileFragment() {
        // Required empty public constructor
    }


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
        bigAvatarImage = view.findViewById(R.id.big_avatar_image);
        singOut = view.findViewById(R.id.logout);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        myRecieptsRecyclerView = view.findViewById(R.id.myRecieptsRecyclerView);
        myReceiptsTextView = view.findViewById(R.id.my_receipts_TextView);


        db.collection(User.COLLECTION_NAME).document(Objects.requireNonNull(firebaseAuth.getCurrentUser().getUid())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                usernameTextView.setText(user.getName());
                if (user.avatar_path != null) {
                    StorageReference profileAvatarReference = storage.getReference(user.avatar_path);
                    profileAvatarReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (isAdded()) {
                                String imageURL = uri.toString();
                                Glide.with(requireContext()).load(imageURL).into(avatarView);
                                Glide.with(requireContext()).load(imageURL).into(bigAvatarImage);
                            }
                        }
                    });
                } else {
                    avatarView.setImageResource(R.drawable.baseline_person);
                }
            }
        });

        db.collection(Post.COLLECTION_NAME).whereEqualTo(Post.USER_NAME, firebaseAuth.getCurrentUser().getUid()).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        posts.add(document.toObject(Post.class));
                    }
                    File dir = new File(getContext().getFilesDir(), "Recipes");
                    try {
                        for (File file : Objects.requireNonNull(dir.listFiles())) {
                            Post readPost = Post.readSavedRecipe(file);
                            if (!posts.contains(readPost)) {
                                posts.add(readPost);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("e", e.getMessage());
                    }
                    myReceiptsAdapter = new MyReceiptsAdapter(posts, getContext(), getActivity());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    myRecieptsRecyclerView.setLayoutManager(linearLayoutManager);
                    myRecieptsRecyclerView.setAdapter(myReceiptsAdapter);
                }
            }
        });

        myReceiptsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAllReceiptsActivity = new Intent(getActivity(), MyAllReceiptsActivity.class);
                getActivity().startActivity(myAllReceiptsActivity);
            }
        });

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });
        singOut.setOnClickListener(new View.OnClickListener() {
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
                StorageReference avatarReference = storageReferenceUserAvatar.child(firebaseAuth.getCurrentUser().getUid() + ".jpg");
                db.collection(User.COLLECTION_NAME).document(firebaseAuth.getCurrentUser().getUid()).update("avatar_path", avatarReference.getPath());
                avatarReference.putFile(data.getData());
                Glide.with(this).load(data.getData()).into(avatarView);
                Glide.with(this).load(data.getData()).into(bigAvatarImage);
            }

        }
    }
}