package com.example.lor277project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView signUp, forgot;
    private EditText email, password;
    private Button login;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUp = (TextView) findViewById(R.id.signUp);
        forgot = (TextView) findViewById(R.id.forgot);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("LOR - LEND or RENT");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPWActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String email_ = email.getText().toString().trim();
               String password_ = password.getText().toString().trim();

                if(email_.isEmpty()) {
                    email.setError("Email address is required!");
                    email.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email_).matches()) {
                    email.setError("Please provide valid email address!");
                    email.requestFocus();
                    return;
                }

                if(password_.isEmpty()) {
                    password.setError("Password is required!");
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email_,password_).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()){
                                Toast.makeText(MainActivity.this, "Welcome!",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, TabsActivity.class));
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                user.sendEmailVerification();
                                Toast.makeText(MainActivity.this, "Check your email to verify your account!",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Failed to login, please check your credentials",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}