package com.example.phonefirebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText phoneNumber, otpCode;
    Button btnSubmitPhone, btnSubmitOtp;
    LinearLayout phoneInput, otpInput;
    
    String verificationID;

    FirebaseAuth fbAuth;
    FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = findViewById(R.id.phone_number);
        otpCode = findViewById(R.id.otp_code);
        btnSubmitPhone = findViewById(R.id.btn_submit_phone);
        btnSubmitOtp = findViewById(R.id.btn_submit_otp);
        phoneInput = findViewById(R.id.phone_input);
        otpInput = findViewById(R.id.otp_input);

        phoneInput.setVisibility(View.VISIBLE);
        otpInput.setVisibility(View.GONE);

        fbAuth = FirebaseAuth.getInstance();

        btnSubmitPhone.setOnClickListener(v -> {
            if(TextUtils.isEmpty(phoneNumber.getText().toString())){
                Toast.makeText(MainActivity.this, "Tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                String phone = phoneNumber.getText().toString();

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fbAuth)
                        .setPhoneNumber("+62"+phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        btnSubmitOtp.setOnClickListener(v -> {
            if(TextUtils.isEmpty(otpCode.getText().toString())){
                Toast.makeText(MainActivity.this, "Kode OTP tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(verificationID, otpCode.getText().toString());
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithCredential(credentials)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Kode OTP salah", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String otpCode = credential.getSmsCode();
            if(otpCode != null){
                PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(verificationID, otpCode);

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithCredential(credentials)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                }
                            }
                        });
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, "Verifikasi gagal.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);
            verificationID = verificationId;

            otpInput.setVisibility(View.VISIBLE);
            phoneInput.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        fbUser = fbAuth.getCurrentUser();

        if(fbUser != null){
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }
}