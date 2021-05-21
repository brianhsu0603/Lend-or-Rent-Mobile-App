package com.example.lor277project;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;

public class ForgotPWActivity extends AppCompatActivity {

    private EditText email;
    private Button reset;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_p_w);
        getSupportActionBar().setTitle("RESET PASSWORD");


        email = (EditText) findViewById(R.id.email);
        reset = (Button) findViewById(R.id.reset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email_ = email.getText().toString().trim();
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
                progressBar.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email_).addOnCompleteListener(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(ForgotPWActivity.this, "Check your email to reset password!",Toast.LENGTH_LONG).show();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(ForgotPWActivity.this, "Something went wrong, please try again!",Toast.LENGTH_LONG).show();

                        }
                    }

                });


            }
        });



    }
}