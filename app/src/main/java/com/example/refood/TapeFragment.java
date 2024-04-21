package com.example.refood;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TapeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TapeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static ArrayList <Post> posts = new ArrayList<>();

    private RecyclerView posts_recyclerView;

    private FloatingActionButton add_button;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public TapeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TapeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TapeFragment newInstance(String param1, String param2) {
        TapeFragment fragment = new TapeFragment();
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
        return inflater.inflate(R.layout.fragment_tape, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        posts_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posts_recyclerView = view.findViewById(R.id.tape_recycler_view);
        posts_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        posts_recyclerView.setAdapter(new PostsTapeAdapter(posts));


        NavController navController = Navigation.findNavController(view);

        add_button = view.findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.add_Product_Fragment);
            }
        });
    }
}