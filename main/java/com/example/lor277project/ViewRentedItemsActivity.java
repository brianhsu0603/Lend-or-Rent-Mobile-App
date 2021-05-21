package com.example.lor277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewRentedItemsActivity extends AppCompatActivity {

    TextView name,description,price,category,location,time,availability,owner;
    ImageView imageView;
    Button return_;
    static String owner_ ="";

    static String availability_ = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rented_items);
        getSupportActionBar().setTitle("ITEM DETAILS");

        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        location = findViewById(R.id.location);
        time = findViewById(R.id.time);
        availability = findViewById(R.id.availability);
        owner = findViewById(R.id.owner);
        return_ = findViewById(R.id.return_);
        imageView = findViewById(R.id.photo);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Items");
        String ItemKey = getIntent().getStringExtra("ItemKey");
        ref.child(ItemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){
                    String category_ = dataSnapshot.child("category").getValue().toString();
                    String name_ = dataSnapshot.child("name").getValue().toString();
                    String description_ = dataSnapshot.child("description").getValue().toString();
                    String price_ = dataSnapshot.child("price").getValue().toString();
                    String location_ = dataSnapshot.child("location").getValue().toString();
                    String uri = dataSnapshot.child("uri").getValue().toString();
                    String time_ = dataSnapshot.child("time").getValue().toString();
                    owner_ = dataSnapshot.child("uid").getValue().toString();
                    availability_ = dataSnapshot.child("availability").getValue().toString();


                    Picasso.get().load(uri).into(imageView);

                    category.setText(category_);
                    name.setText(name_);
                    description.setText(description_);
                    price.setText("$"+price_ +" USD/Day");
                    location.setText(location_);
                    time.setText(time_);
                    availability.setText(availability_);

                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");

                    mRef.child(owner_).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User userProfile = snapshot.getValue(User.class);
                            if (userProfile != null) {
                                String uName = userProfile.name;
                                String uEmail = userProfile.email;
                                owner.setText("ID: " + owner_ + "\n" + "Name: " + uName + "\n" + "Email: " + uEmail);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return_.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ref.child(ItemKey).child("availability").setValue("Yes");
                ref.child(ItemKey).child("renter").setValue("");
                Toast.makeText(ViewRentedItemsActivity.this, "Return Successfully!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ViewRentedItemsActivity.this, RentedItemsActivity.class);
                startActivity(intent);

            }
        });
    }
}