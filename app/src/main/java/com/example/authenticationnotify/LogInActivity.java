package com.example.authenticationnotify;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

 import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    EditText textEditEmail;
    EditText textEditPassword;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    Button LoginBtn;
    TextView signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        textEditEmail = findViewById(R.id.email);
        textEditPassword = findViewById(R.id.password);
        LoginBtn = findViewById(R.id.log);
        signup = findViewById(R.id.signup);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        LoginBtn.setOnClickListener(view -> {
            String textEmail = textEditEmail.getText().toString();
            String textPassword = textEditPassword.getText().toString();
            Log.e("email", textEmail);
            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(LogInActivity.this, "please enter your email   ", Toast.LENGTH_SHORT).show();
                textEditEmail.setError(" email is required  ");
                textEditEmail.requestFocus();
            } else if (TextUtils.isEmpty(textPassword)) {
                Toast.makeText(LogInActivity.this, "please enter your password   ", Toast.LENGTH_SHORT).show();
                textEditPassword.setError(" password is required");
                textEditPassword.requestFocus();
            } else {
                loginFirebaseDB(textEmail, textPassword);
            }

        });
        signup.setOnClickListener(view -> {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LogInActivity.this).toBundle());
                }
        );


    }

    private void loginFirebaseDB(String textEmail, String textPassword) {
        mAuth.signInWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    Intent intent = new Intent(LogInActivity.this, Profile_Activity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LogInActivity.this).toBundle());

                } else {
                    Toast.makeText(LogInActivity.this, "something is wrong !", Toast.LENGTH_SHORT).show();
                }

            }


        });
    }
}


//
