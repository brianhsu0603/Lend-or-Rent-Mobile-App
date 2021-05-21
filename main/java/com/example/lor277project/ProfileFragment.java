package com.example.lor277project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import android.view.View.OnClickListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import androidx.annotation.NonNull;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;


public class ProfileFragment extends Fragment {



    public ProfileFragment() {
        // Required empty public constructor
    }


    private Button logout;
    private TextView forgot,rent,lend;
    private FirebaseUser user;
    private DatabaseReference mRef;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        final TextView emailTitle = (TextView) view.findViewById(R.id.emailTitle);
        final TextView nameTitle = (TextView) view.findViewById(R.id.nameTitle);

        final TextView emailView = (TextView) view.findViewById(R.id.email);
        final TextView nameView = (TextView) view.findViewById(R.id.name);

        mRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    String fullName = userProfile.name;
                    String email = userProfile.email;
                    emailView.setText(email);
                    nameView.setText(fullName);;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(getActivity(),"Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });
        forgot = (TextView) view.findViewById(R.id.forgot);
        rent = (TextView) view.findViewById(R.id.rent);
        lend = (TextView) view.findViewById(R.id.lend);


        forgot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogPW dialog = new DialogPW();
                dialog.show(getFragmentManager(),"dialog");

            }
        });

        rent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RentedItemsActivity.class);
                startActivity(intent);
            }
        });

        lend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyItemsActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
}