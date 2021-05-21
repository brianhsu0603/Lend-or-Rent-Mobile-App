package com.example.lor277project;
import android.content.Intent;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.Dialog;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.content.DialogInterface;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class DialogPW extends AppCompatDialogFragment {
    private EditText email;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgotpw, null);
        email = (EditText) view.findViewById(R.id.email);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        builder.setView(view).setTitle("Reset Password")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){

                    }
                })
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String email_ = email.getText().toString().trim();
                                if (email_.isEmpty()) {
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
                                mAuth.sendPasswordResetEmail(email_).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), "Check your email to reset password!", Toast.LENGTH_LONG).show();
                                        } else {
                                            progressBar.setVisibility(View.GONE);

                                            Toast.makeText(getActivity(), "Something went wrong, please try again!", Toast.LENGTH_LONG).show();

                                        }

                                    }
                                });
                            }
                        });
        return builder.create();
    }
}
