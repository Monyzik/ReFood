package com.example.refood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainPageFragment newInstance(String param1, String param2) {
        MainPageFragment fragment = new MainPageFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_page, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ArrayList<Post> posts = new ArrayList<>();


        ImageView imageView_recipe_of_the_day = view.findViewById(R.id.food_image_recipe_of_the_day);
        TextView title_recipe_of_the_day = view.findViewById(R.id.title_recipe_of_the_day);
        TextView author_recipe_of_the_day = view.findViewById(R.id.author_name_recipe_of_the_day);
        TextView likes_recipe_of_the_day = view.findViewById(R.id.likes_recipe_of_the_day);
        View recipe_of_the_day = view.findViewById(R.id.recipe_of_the_day);


//        for (File file: new File(getContext().getFilesDir() + "/Recipes").listFiles()) {
//            try {
//                if (count >= 3) break;
//                posts.add(Post.readSavedRecipe(file));
//                count++;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Post.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int count = 0;
                for (QueryDocumentSnapshot document: task.getResult()) {
                    Post post = document.toObject(Post.class);
                    posts.add(post);
                    ViewPager2 viewPager2 = view.findViewById(R.id.popular_recipes_recycler_view);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(posts, getActivity());
                    viewPager2.setAdapter(adapter);
                    count++;
                    if (count == 1) {
                        String image_path = post.getImage();
                        if (!Objects.equals(image_path, "")) {
                            if (post.getIsLocal()) {
                                imageView_recipe_of_the_day.setImageURI(Uri.parse(image_path));
                            } else {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference mainImageReference = storage.getReference(image_path);
                                mainImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageURL = String.valueOf(uri);
                                        Glide.with(getActivity().getApplicationContext()).load(imageURL).into(imageView_recipe_of_the_day);
                                    }
                                });
                            }
                        } else {
                            imageView_recipe_of_the_day.setImageResource(R.drawable.example_of_food_photo);
                        }
                        title_recipe_of_the_day.setText(post.getTitle());
                        author_recipe_of_the_day.setText(post.getAuthor());
                        likes_recipe_of_the_day.setText(post.getLike_count() + "");
                    }
                }
            }
        });





        recipe_of_the_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ReadOtherRecipe.class);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setPrettyPrinting();
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(posts.get(0));
                i.putExtra("post", json);
                getActivity().startActivity(i);
            }
        });



        super.onViewCreated(view, savedInstanceState);
    }
}