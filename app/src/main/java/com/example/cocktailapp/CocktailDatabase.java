package com.example.cocktailapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Cocktail.class},version = 1)
public abstract class CocktailDatabase extends RoomDatabase {

    public abstract CocktailDao getDao();
}
