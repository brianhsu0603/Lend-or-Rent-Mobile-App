package com.example.lor277project;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class ViewItemActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView name,description,price,category,location,time,availability,owner;
    SupportMapFragment mapFragment;
    Button rent;
    ImageView imageView;
    GoogleMap map;
    static String owner_,location_= "";

    static String availability_,time_= "";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        getSupportActionBar().setTitle("ITEM DETAILS");

        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        location = findViewById(R.id.location);
        time = findViewById(R.id.time);
        availability = findViewById(R.id.availability);
        owner = findViewById(R.id.owner);
        rent = findViewById(R.id.rent);
        imageView = findViewById(R.id.photo);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Items");
        String ItemKey = getIntent().getStringExtra("ItemKey");
        ref.child(ItemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String category_ = dataSnapshot.child("category").getValue().toString();
                    String name_ = dataSnapshot.child("name").getValue().toString();
                    String description_ = dataSnapshot.child("description").getValue().toString();
                    String price_ = dataSnapshot.child("price").getValue().toString();
                    location_ = dataSnapshot.child("location").getValue().toString();
                    String uri = dataSnapshot.child("uri").getValue().toString();
                    String time_ = dataSnapshot.child("time").getValue().toString();
                    owner_ = dataSnapshot.child("uid").getValue().toString();
                    availability_ = dataSnapshot.child("availability").getValue().toString();


                    Geocoder geocoder = new Geocoder(ViewItemActivity.this);
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocationName(location_,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).title(location_));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));


                    Picasso.get().load(uri).into(imageView);

                    category.setText(category_);
                    name.setText(name_);
                    description.setText(description_);
                    price.setText("$" + price_ + " USD/Day");
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

        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (availability_.equals("No")) {
                    Toast.makeText(ViewItemActivity.this, "Sorry, this item is rented by someone else!", Toast.LENGTH_LONG).show();
                } else {

                    ref.child(ItemKey).child("availability").setValue("No");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String renterID = mAuth.getCurrentUser().getUid();
                    ref.child(ItemKey).child("renter").setValue(renterID);
                    Intent intent = new Intent(ViewItemActivity.this, TabsActivity.class);
                    startActivity(intent);
                    Toast.makeText(ViewItemActivity.this, "Rented successfully!", Toast.LENGTH_LONG).show();


                            }
                        }
                    });



    }


}

