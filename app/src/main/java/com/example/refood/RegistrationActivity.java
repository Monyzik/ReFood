package com.example.refood;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView, confirmPasswordEditText, username;
    private Button button;
    private ProgressBar progressBar;
    private TextView toSignInActivity;
    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailTextView = findViewById(R.id.username_edt);
        passwordTextView = findViewById(R.id.password_edt);
        button = findViewById(R.id.registration_btn);
        progressBar = findViewById(R.id.progress_bar_reg);
        toSignInActivity = findViewById(R.id.to_login_activity);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edt);
        username = findViewById(R.id.login_edt);

        toSignInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = emailTextView.getText().toString(), password = passwordTextView.getText().toString(), confirm_password = confirmPasswordEditText.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "You need to write email!", Toast.LENGTH_SHORT).show();
            return;
        } if (password.isEmpty()) {
            Toast.makeText(this, "You need to write password!", Toast.LENGTH_SHORT).show();
            return;
        } if (password.length() < 6) {
            Toast.makeText(this, "Your password must contain more then 6 characters", Toast.LENGTH_SHORT).show();
            return;
        } if (!Objects.equals(confirm_password, password)) {
            Toast.makeText(this, "You uncorrect confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult().getUser() != null) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    User user = new User(firebaseUser.getUid(), username.getText().toString(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                    db.collection(User.COLLECTION_NAME).document(firebaseUser.getUid()).set(user);
                    Log.i("new registration", "New user successful register");
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));

                } else {
                    Log.e("fail registration", "Registration failed");
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
