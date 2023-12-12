package com.example.cocktailapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonManager {


    //for category
    ArrayList<String> fromJsonToArrayOfCategories(String jsonStr){
        ArrayList<String> catList = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("drinks");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject catObjet = jsonArray.getJSONObject(i);

                // Extract data from the cocktail JSON object
                String category = catObjet.getString("strCategory");
                catList.add(category);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return catList;
    }

    //for list of drinks
    ArrayList<Cocktail> fromJsonToArrayOfDrinks(String jsonStr){
        ArrayList<Cocktail> list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("drinks");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cocktailJson = jsonArray.getJSONObject(i);

                // Extract data from the cocktail JSON object
                String name = cocktailJson.getString("strDrink");
                String category = cocktailJson.getString("strCategory");
                String instruction = cocktailJson.getString("strInstructions");
                String image = cocktailJson.getString("strDrinkThumb");
                String ingredient = cocktailJson.getString("strIngredient1") + ", "
                        + cocktailJson.getString("strIngredient2") + ", "
                        + cocktailJson.getString("strIngredient3");

                // Create and add Cocktail object to the list
                list.add(new Cocktail(name, category, instruction, image, ingredient));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    //for drink object
    Cocktail fromJsonToCocktailObj(String json){
        Cocktail cocktail = new Cocktail();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("drinks");

            cocktail.setName(jsonArray.getJSONObject(0).getString("strDrink"));
            cocktail.setCategory(jsonArray.getJSONObject(0).getString("strCategory"));
            cocktail.setInstruction(jsonArray.getJSONObject(0).getString("strInstructions"));
            cocktail.setImage(jsonArray.getJSONObject(0).getString("strDrinkThumb"));
            cocktail.setIngredient(jsonArray.getJSONObject(0).getString("strIngredient1")+ ", "
                    + jsonArray.getJSONObject(0).getString("strIngredient2") + ", "
                    + jsonArray.getJSONObject(0).getString("strIngredient3"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return cocktail;
    }

}
