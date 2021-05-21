package com.example.lor277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MyItemsActivity extends AppCompatActivity {
    MyAdapter adapter_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        getSupportActionBar().setTitle("MY ITEMS");

        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("uid").startAt(uid).endAt(uid+"\uf8ff"), Item.class)
                        .build();

        adapter_ = new MyItemsActivity.MyAdapter(options);
        recyclerView.setAdapter(adapter_);

    }

    public class MyAdapter extends FirebaseRecyclerAdapter<Item, com.example.lor277project.MyAdapter.myviewholder>
    {
        public MyAdapter(@NonNull FirebaseRecyclerOptions<Item> options) {
            super(options);
        }

        @Override
        public void onBindViewHolder(@NonNull com.example.lor277project.MyAdapter.myviewholder holder, int position, @NonNull Item item)
        {
            holder.name.setText(item.getName());
            holder.price.setText("$"+item.getPrice()+" USD/Day");
            holder.location.setText(item.getLocation());
            Glide.with(holder.img.getContext()).load(item.getUri()).into(holder.img);
            holder.v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(MyItemsActivity.this, ViewMyItemsActivity.class);
                    intent.putExtra("ItemKey",getRef(position).getKey());
                    startActivity(intent);
                }
            });
        }

        @NonNull
        @Override
        public com.example.lor277project.MyAdapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
            return new com.example.lor277project.MyAdapter.myviewholder(view);
        }


        class myviewholder extends RecyclerView.ViewHolder
        {
            ImageView img;
            TextView name,price,location;
            View v;
            public myviewholder(@NonNull View itemView)
            {
                super(itemView);
                img=(ImageView)itemView.findViewById(R.id.img1);
                name=(TextView)itemView.findViewById(R.id.name);
                price=(TextView)itemView.findViewById(R.id.price);
                location=(TextView)itemView.findViewById(R.id.location);
                v = itemView;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter_.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter_.stopListening();
    }
}