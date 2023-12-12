package com.example.cocktailapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CocktailDao {

    @Insert
    void addNewDrink(Cocktail c);

    @Query("select * from Cocktail")
    List<Cocktail> getAllDrinks();

    @Delete
    void deleteDrink(Cocktail todelete);

    @Query("select COUNT(*) from Cocktail where name = :drinkName")
    int findDrinkByName(String drinkName);

    @Query("select * from Cocktail where name like :dName")
    List<Cocktail> checkForDrink(String dName);
}
