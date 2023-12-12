package com.example.cocktailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements DatabaseManager.DatabaseManagerInterfaceListener,
        CocktailRecyclerAdapter.CocktailClickListener{

    RecyclerView dblist;
    CocktailRecyclerAdapter adapter;
    DatabaseManager databaseManager;
    FloatingActionButton addDrinkBtn;

    ArrayList<Cocktail> cocktailArrayList = new ArrayList<>(0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addDrinkBtn = findViewById(R.id.addNewDrink);

        dblist = findViewById(R.id.dblist);
        dblist.setLayoutManager(new LinearLayoutManager(this));

        databaseManager = ((MyApp)getApplication()).databaseManager;
        databaseManager.getDatabase(this);

        databaseManager.listener = this;

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.DOWN, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT |
                ItemTouchHelper.UP |
                ItemTouchHelper.DOWN
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                databaseManager.deleteDrinkInBGThread(cocktailArrayList.get(position));

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(dblist);

        addDrinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        databaseManager.listener = this;
        databaseManager.getAllDrinksInBGThread();

    }

    @Override
    public void databaseGetListOfDrinks(List<Cocktail> l) {
        // Update the cocktailArrayList with the new list of drinks
        cocktailArrayList.clear();
        cocktailArrayList.addAll(l);

        //ready to refresh the list of drinks
        adapter = new CocktailRecyclerAdapter(this,(ArrayList<Cocktail>)l);
        dblist.setAdapter( adapter);
        adapter.listener = this;
       adapter.notifyDataSetChanged();
    }

    @Override
    public void drinkInserted(boolean done) {

    }

    @Override
    public void onCocktailSelected(Cocktail selectedCocktail) {
        Intent toDetail = new Intent(this,CocktailDetail.class);
        toDetail.putExtra("cocktail",selectedCocktail);
        startActivity(toDetail);

    }
}