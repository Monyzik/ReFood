package com.example.refood;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toolbar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    NavController navigationController;

    protected Dialog splashDialog;

    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        File dir_my_recipes = new File(getFilesDir(), "Recipes");
        File dir_other_recipes = new File(getFilesDir(), "OtherRecipes");
        boolean flag_my_recipe = false;
        boolean flag_other_recipes = false;
        for (File file: getFilesDir().listFiles()) {
            if (file.getName().equals("Recipes")) {
                flag_my_recipe = true;
            }
            if (file.getName().equals("OtherRecipes")) {
                flag_other_recipes = true;
            }
        }
        if (!flag_my_recipe) {
            dir_my_recipes.mkdir();
        }
        if (!flag_other_recipes) {
            dir_other_recipes.mkdir();
        }
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigationController = Navigation.findNavController(MainActivity.this, R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navigationController);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}