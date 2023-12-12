package com.example.cocktailapp;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApp extends Application {
    NetworkingManager networkingManager = new NetworkingManager();

    JsonManager jsonManager = new JsonManager();

    static ExecutorService executorService = Executors.newFixedThreadPool(4);
    static Handler mainhandler = new Handler(Looper.getMainLooper());

    DatabaseManager databaseManager = new DatabaseManager();

    //ArrayList<Cocktail> cocktailArrayList = new ArrayList<>(0);


}
