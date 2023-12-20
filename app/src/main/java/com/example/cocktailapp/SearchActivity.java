package com.example.cocktailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity
        implements NetworkingManager.NetworkingInterfaceListener,
        CocktailRecyclerAdapter.CocktailClickListener,
        DatabaseManager.DatabaseManagerInterfaceListener{
    NetworkingManager networkingManager;
    JsonManager jsonManager;
    DatabaseManager databaseManager;
    CocktailRecyclerAdapter adapter;
    ArrayList<Cocktail> list = new ArrayList<>(0);
    RecyclerView recyclerView;
    SearchView menuSearchItem;

    ArrayList<String> categoryList;
    private int selectedCategoryPosition = 0;
    private String querry = "";
    Cocktail selectedDrink;
    Spinner categorySpinner;
    Button clearFilterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        categorySpinner = findViewById(R.id.categorySpinner);
        categorySpinner.setVisibility(View.GONE);

        clearFilterBtn = findViewById(R.id.clearFilterButton);

        if(savedInstanceState != null){
            list = savedInstanceState.getParcelableArrayList("list");
            selectedCategoryPosition = savedInstanceState.getInt("selectedPosition",0);


            querry = savedInstanceState.getString("searchQuerry","");
            if(querry.length() > 1){
                categorySpinner.setVisibility(View.VISIBLE);
            }

            if(menuSearchItem !=null){
                menuSearchItem.setQuery(querry,false);
            }

        }

        networkingManager = ((MyApp)getApplication()).networkingManager;
        jsonManager = ((MyApp)getApplication()).jsonManager;
        networkingManager.listener = this;
        databaseManager = ((MyApp)getApplication()).databaseManager;
        databaseManager.listener = this;

        recyclerView = findViewById(R.id.cocktailRecyclerView);
        adapter = new CocktailRecyclerAdapter(this, list);
        adapter.listener = this;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //fetch list of categories
        networkingManager.getDrinkCategories();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        menuSearchItem = (SearchView) menu.findItem(R.id.search_bar_item).getActionView();
        menuSearchItem.setQueryHint(getString(R.string.searchDrink));
        menuSearchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 1){
                    querry = newText;
                    networkingManager.getCocktails(newText);
                    categorySpinner.setVisibility(View.VISIBLE);
                } else {
                    adapter.list = new ArrayList<>(0);
                    adapter.notifyDataSetChanged();
                    categorySpinner.setVisibility(View.INVISIBLE);
                    //set spinner to default selection
                    categorySpinner.setSelection(0);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void networkingFinishWithJsonString(String json) {
        list = jsonManager.fromJsonToArrayOfDrinks(json);

        adapter.list = list;
        adapter.notifyDataSetChanged();
    }

    //show/hide clear filter button
    private void showClearFilterButton(boolean show){
        if(clearFilterBtn != null){
            clearFilterBtn.setVisibility(show ? View.VISIBLE : View.GONE);
            clearFilterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //clear filter and reset UI
                    categorySpinner.setSelection(0);
                }
            });
        }
    }

    //populate()
    private void populateSpinner(ArrayList<String> categoryList){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categoryList.get(position);
                ArrayList<Cocktail> filteredList = filterByCategory(selectedCategory, list);

                if (filteredList.isEmpty()) {
                    Toast.makeText(SearchActivity.this, getString(R.string.noItem), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchActivity.this, filteredList.size() + " "+getString(R.string.foundItem), Toast.LENGTH_SHORT).show();
                }
                adapter.list = filteredList;
                adapter.notifyDataSetChanged();

                // Update the selectedCategoryPosition
                selectedCategoryPosition = position;

                //show/hide clearFilter button based on selection
                showClearFilterButton(position != 0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //hide clearFilter button
                showClearFilterButton(false);

            }
        });
        // Select the saved position in the spinner
        categorySpinner.setSelection(selectedCategoryPosition);
    }

    private ArrayList<Cocktail> filterByCategory(String category, ArrayList<Cocktail> drinkList) {
        ArrayList<Cocktail> filteredList = new ArrayList<>();
        for (Cocktail drink : drinkList) {
            if (drink.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(drink);
            }
        }
        return filteredList;
    }

    @Override
    public void networkingFinishWithBitMapImage(Bitmap bitmap) {
        // Handle Bitmap image when fetched from network
    }

    @Override
    public void networkingFinishWithCategoryJsonString(String category) {
        categoryList = jsonManager.fromJsonToArrayOfCategories(category);
        //set title for spinner
        categoryList.add(0,getString(R.string.sortCategory));
        populateSpinner(categoryList);

        // Restore the selected position after populating the spinner
        if (selectedCategoryPosition < categoryList.size()) {
            categorySpinner.setSelection(selectedCategoryPosition);
        }
    }

    @Override
    public void onCocktailSelected(Cocktail selectedCocktail) {
        selectedDrink = selectedCocktail;
        // Dialog to prompt saving the selected drink
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    ((MyApp)getApplication()).databaseManager.addDrinkInBGThread(selectedCocktail);
                    finish();

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", list);
        outState.putInt("selectedPosition",selectedCategoryPosition);
        outState.putString("searchQuerry",querry);
        menuSearchItem.getQuery();
    }


    @Override
    public void databaseGetListOfDrinks(List<Cocktail> l) {

    }

    @Override
    public void drinkInserted(boolean done) {
        if(!done){
            Toast.makeText(SearchActivity.this,selectedDrink.getName() + getString(R.string.added),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(SearchActivity.this,selectedDrink.getName() + getString(R.string.addSucc),Toast.LENGTH_SHORT).show();
            databaseManager.getAllDrinksInBGThread();
            adapter.notifyDataSetChanged();
        }

    }
}
