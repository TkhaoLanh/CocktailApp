package com.example.cocktailapp;

import android.content.Context;

import androidx.room.Room;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseManager {

    interface DatabaseManagerInterfaceListener{
        void databaseGetListOfDrinks(List<Cocktail> l);
        void drinkInserted(boolean done);

    }
    CocktailDatabase database;
    DatabaseManagerInterfaceListener listener;

    CocktailDatabase getDatabase(Context context){
        if (database == null)
            database = Room.databaseBuilder(context,
                    CocktailDatabase.class, "database-Drinks").build();

        return  database;

    }

    void addDrinkInBGThread(Cocktail cocktail){
        MyApp.executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Cocktail> listDrinks = database.getDao().checkForDrink(cocktail.name);
                if(listDrinks.size() == 0){
                    database.getDao().addNewDrink(cocktail);
                    MyApp.mainhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.drinkInserted(true);
                        }
                    });
                }else {
                    MyApp.mainhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.drinkInserted(false);
                        }
                    });

                }
            }
        });

    }

    void deleteDrinkInBGThread(Cocktail cocktail){
        MyApp.executorService.execute(new Runnable() {
            @Override
            public void run() {
                database.getDao().deleteDrink(cocktail);
            }
        });

    }


    void getAllDrinksInBGThread(){
        MyApp.executorService.execute(new Runnable() {
            @Override
            public void run() {
               List<Cocktail> list = database.getDao().getAllDrinks();
               MyApp.mainhandler.post(new Runnable() {
                   @Override
                   public void run() {
                       //main activity
                        listener.databaseGetListOfDrinks(list);
                   }
               });
            }
        });

    }
}
