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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class MainPageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageView like_of_the_day_image;
    FirebaseAuth auth;
    View soups_category;
    View main_dishes_category;
    View hot_appetizers_category;
    View cold_platter_category;
    View salads_category;
    View deserts_category;

    View layout;
    TextView no_connection;

    public MainPageFragment() {
    }

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
        return inflater.inflate(R.layout.fragment_main_page, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (NetworkUtils.isNetworkConnected(getContext())) {
            layout = view.findViewById(R.id.layout);
            no_connection = view.findViewById(R.id.no_connection);
            layout.setVisibility(View.VISIBLE);
            no_connection.setVisibility(View.GONE);


                ArrayList<Post> posts = new ArrayList<>();

                auth = FirebaseAuth.getInstance();

                ImageView imageView_recipe_of_the_day = view.findViewById(R.id.food_image_recipe_of_the_day);
                TextView title_recipe_of_the_day = view.findViewById(R.id.title_recipe_of_the_day);
                TextView author_recipe_of_the_day = view.findViewById(R.id.author_name_recipe_of_the_day);
                TextView likes_recipe_of_the_day = view.findViewById(R.id.likes_recipe_of_the_day);
                View recipe_of_the_day = view.findViewById(R.id.recipe_of_the_day);
                like_of_the_day_image = view.findViewById(R.id.like_recipe_of_the_day_image);


                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Date now = new Date();
                db.collection(Post.COLLECTION_NAME).whereGreaterThanOrEqualTo("date", now.getTime() - 86400000).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Post> posts_to_sort = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                posts_to_sort.add(document.toObject(Post.class));
                            }
                            if (posts_to_sort.size() >= 1) {
                                Collections.sort(posts_to_sort, Post.COUNT_OF_LIKES_COMPARATOR);
                                Collections.reverse(posts_to_sort);
                                Post post = posts_to_sort.get(0);
                                recipe_of_the_day.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(getActivity(), ReadOtherRecipe.class);
                                        GsonBuilder gsonBuilder = new GsonBuilder();
                                        gsonBuilder.setPrettyPrinting();
                                        Gson gson = gsonBuilder.create();
                                        String json = gson.toJson(post);
                                        i.putExtra("post", json);
                                        getActivity().startActivity(i);
                                    }
                                });
                                title_recipe_of_the_day.setText(post.getTitle());
                                author_recipe_of_the_day.setText(post.getAuthor_name());
                                likes_recipe_of_the_day.setText(String.valueOf(post.getLike_count()));
                                if (post.getLikes_from_users().contains(auth.getCurrentUser().getUid())) {
                                    like_of_the_day_image.setImageResource(R.drawable.baseline_thumb_up_filled);
                                }
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
                            }
                        }
                    }
                });

                db.collection(Post.COLLECTION_NAME).orderBy("like_count", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                posts.add(post);
                            }
                            ViewPager2 viewPager2 = view.findViewById(R.id.popular_recipes_recycler_view);
                            ViewPagerAdapter adapter = new ViewPagerAdapter(posts, getActivity());
                            viewPager2.setAdapter(adapter);
                        }
                    }
                });


                View.OnClickListener categoryListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String value = "";
                        int id = v.getId();
                        if (id == R.id.soups_category) {
                            value = getResources().getString(R.string.soups);
                        } else if (id == R.id.main_dishes_category) {
                            value = getResources().getString(R.string.main_dishes);
                        } else if (id == R.id.hot_appetizers_category) {
                            value = getResources().getString(R.string.hot_appetizers);
                        } else if (id == R.id.cold_platter_category) {
                            value = getResources().getString(R.string.cold_platter);
                        } else if (id == R.id.salads_category) {
                            value = getResources().getString(R.string.salads);
                        } else {
                            value = getResources().getString(R.string.desserts);
                        }
                        Intent i = new Intent(getActivity(), ActivityCategoryTape.class);
                        i.putExtra("category", value);
                        getActivity().startActivity(i);
                    }
                };
                soups_category = view.findViewById(R.id.soups_category);
                main_dishes_category = view.findViewById(R.id.main_dishes_category);
                hot_appetizers_category = view.findViewById(R.id.hot_appetizers_category);
                cold_platter_category = view.findViewById(R.id.cold_platter_category);
                salads_category = view.findViewById(R.id.salads_category);
                deserts_category = view.findViewById(R.id.desserts_category);
                soups_category.setOnClickListener(categoryListener);
                main_dishes_category.setOnClickListener(categoryListener);
                hot_appetizers_category.setOnClickListener(categoryListener);
                cold_platter_category.setOnClickListener(categoryListener);
                salads_category.setOnClickListener(categoryListener);
                deserts_category.setOnClickListener(categoryListener);

            }



        super.onViewCreated(view, savedInstanceState);
    }
}