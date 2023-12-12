package com.example.cocktailapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CocktailRecyclerAdapter extends RecyclerView.Adapter<CocktailRecyclerAdapter.CocktailViewHolder> {



    interface CocktailClickListener{
        void onCocktailSelected(Cocktail selectedCocktail);
    }
    Context context;
    ArrayList<Cocktail> list;
    private ArrayList<String> drinkNames = new ArrayList<>();

    CocktailClickListener listener;
    public CocktailRecyclerAdapter(Context context, ArrayList<Cocktail> list) {
        this.context = context;
        this.list = list;

    }

    public class CocktailViewHolder extends RecyclerView.ViewHolder{

        public CocktailViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    @NonNull
    @Override
    public CocktailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.cocktail_row,parent,false);

        return new CocktailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailViewHolder holder, int position) {
        TextView tv = holder.itemView.findViewById(R.id.cocktailName);
        ImageView imageView = holder.itemView.findViewById(R.id.imageDrink);
        tv.setText(list.get(position).name);

        //call downloadImage() to fetch the image
        NetworkingManager networkingManager = new NetworkingManager();
        networkingManager.listener = new NetworkingManager.NetworkingInterfaceListener() {
            @Override
            public void networkingFinishWithJsonString(String json) {}

            @Override
            public void networkingFinishWithBitMapImage(Bitmap bitmap) {
                // Update the ImageView with the fetched image
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }

            @Override
            public void networkingFinishWithCategoryJsonString(String category) {

            }
        };
        networkingManager.downloadImage(list.get(position).getImage());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCocktailSelected(list.get(holder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
