package com.example.phonefirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    Button btnLogout;
    TextView phoneText;

    FirebaseAuth fbAuth;
    FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fbAuth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btn_logout);
        phoneText = findViewById(R.id.phone_text);

        fbUser = fbAuth.getCurrentUser();

        if(fbUser != null) {
            phoneText.setText(fbUser.getPhoneNumber());
        }

        btnLogout.setOnClickListener(v -> {
            fbAuth.signOut();

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}