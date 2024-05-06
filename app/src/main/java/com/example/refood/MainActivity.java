package com.example.refood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toolbar;

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

    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        File dir = new File(getFilesDir(), "Recipes");
        boolean flag = false;
        for (File file: getFilesDir().listFiles()) {
            if (file.getName().equals("Recipes")) {
                flag = true;
            }
        }
        if (!flag) {
            dir.mkdir();
        }
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigationController = Navigation.findNavController(MainActivity.this, R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navigationController);
    }
}