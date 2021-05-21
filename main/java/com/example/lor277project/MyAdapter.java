package com.example.lor277project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;




public class MyAdapter extends FirebaseRecyclerAdapter<Item,MyAdapter.myviewholder>
{
    public MyAdapter(@NonNull FirebaseRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Item item)
    {
        holder.name.setText(item.getName());
        holder.price.setText("$"+item.getPrice()+" USD/Day");
        holder.location.setText(item.getLocation());
        Glide.with(holder.img.getContext()).load(item.getUri()).into(holder.img);
        /*holder.v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), ViewItemActivity.class);
                intent.putExtra("ItemKey",getRef(position).getKey());
                startActivity(intent);
            }
        });*/
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new myviewholder(view);
    }



    static class myviewholder extends RecyclerView.ViewHolder
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