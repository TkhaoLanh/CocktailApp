package com.example.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CocktailDetail extends AppCompatActivity
        implements NetworkingManager.NetworkingInterfaceListener
        {
    NetworkingManager networkingManager;
    ImageView image;
    TextView category, instruction, ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_detail);

        image = findViewById(R.id.drinkImage);
        category = findViewById(R.id.category);
        instruction = findViewById(R.id.instruction);
        ingredient = findViewById(R.id.ingredient);

        Cocktail cocktail = getIntent().getExtras().getParcelable("cocktail");
        this.setTitle(cocktail.name);
        networkingManager = ((MyApp)getApplication()).networkingManager;
        networkingManager.getDrink(cocktail);
        networkingManager.listener = this;
    }

    @Override
    public void networkingFinishWithJsonString(String json) {
        //get json drink
        Cocktail cocktail = ((MyApp)getApplication()).jsonManager.fromJsonToCocktailObj(json);
        networkingManager.downloadImage(cocktail.getImage());
        category.setText(getString(R.string.category) +" "+ cocktail.getCategory());
        instruction.setText(getString(R.string.instruction)+"\n"+ cocktail.getInstruction());
        ingredient.setText(getString(R.string.ingredient) +"\n"+ cocktail.getIngredient());

    }

            @Override
            public void networkingFinishWithBitMapImage(Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void networkingFinishWithCategoryJsonString(String category) {

            }
        }
