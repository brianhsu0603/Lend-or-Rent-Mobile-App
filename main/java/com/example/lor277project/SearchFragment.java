package com.example.lor277project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private RecyclerView recyclerView;
    private Spinner spinner;
    private MyAdapter adapter_;
    private static String category;

    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Items"), Item.class)
                        .build();

        adapter_ = new MyAdapter(options);
        recyclerView.setAdapter(adapter_);

        SearchView searchView = (SearchView) view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processSearch(s);
                return false;
            }
        });

        return view;
    }

    private void processSearch(String s){

            FirebaseRecyclerOptions<Item> options =
                    new FirebaseRecyclerOptions.Builder<Item>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("name").startAt(s).endAt(s + "\uf8ff"), Item.class)
                            .build();

            adapter_ = new MyAdapter(options);
            adapter_.startListening();
            recyclerView.setAdapter(adapter_);

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
        if (!category.equals("None")) {
            FirebaseRecyclerOptions<Item> options =
                    new FirebaseRecyclerOptions.Builder<Item>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("category").startAt(category).endAt(category + "\uf8ff"), Item.class)
                            .build();

            adapter_ = new MyAdapter(options);
            adapter_.startListening();
            recyclerView.setAdapter(adapter_);
        }
        else{
            FirebaseRecyclerOptions<Item> options =
                    new FirebaseRecyclerOptions.Builder<Item>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Items"), Item.class)
                            .build();

            adapter_ = new MyAdapter(options);
            adapter_.startListening();
            recyclerView.setAdapter(adapter_);

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                Intent intent = new Intent(getActivity(), ViewItemActivity.class);
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


}