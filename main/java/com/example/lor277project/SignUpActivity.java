package com.example.lor277project;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText name, email, password, confirm;
    private Button signUp;
    private ProgressBar progressBar;
    private Item[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("SIGN UP");
        mAuth = FirebaseAuth.getInstance();
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirm);
        signUp = (Button) findViewById(R.id.signUp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        signUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                signUp();
            }
        });
    }


    private void signUp() {
        String name_ = name.getText().toString().trim();
        String email_ = email.getText().toString().trim();
        String password_ = password.getText().toString().trim();
        String confirm_ = confirm.getText().toString().trim();

        if(name_.isEmpty()) {
            name.setError("Full name is required!");
            name.requestFocus();
            return;
        }

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

        if(password_.length()<6){
            password.setError("Password length should be at least 6!");
            password.requestFocus();
            return;
        }

        if(confirm_.isEmpty()) {
            confirm.setError("Please re-enter password!");
            confirm.requestFocus();
            return;
        }

        if (!password_.equals(confirm_)){
            confirm.setError("Doesn't match with password!");
            confirm.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email_, password_)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if(task.isSuccessful()){
                            User user = new User(name_,email_);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users");
                            myRef.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task){
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this,"Sign up successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    }
                                    else{
                                        Toast.makeText(SignUpActivity.this,"Failed to sign up, please try again",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "Email already registered, please try again!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}