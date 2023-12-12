package com.example.cocktailapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NetworkingManager {

    interface NetworkingInterfaceListener{
        void networkingFinishWithJsonString(String json);
        void networkingFinishWithBitMapImage(Bitmap bitmap);
        void networkingFinishWithCategoryJsonString(String category);
    }
    NetworkingInterfaceListener listener;

    void getDrinkCategories(){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list";
        connect(url);
    }

    void getCocktails(String query){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + query;
        connect(url);

    }

    void getDrink(Cocktail c){
        String url = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s="+c.name;
        connect(url);
    }

    private void connect(String url){
        HttpURLConnection httpURLConnection = null;
        MyApp.executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;

                try {

                    // this code will work with any http function (get, post, put , delete)
                    URL urlObj = new URL(url);
                    httpURLConnection  = (HttpURLConnection) urlObj.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    int v;
                    while ((v = inputStream.read()) != -1) {
                        buffer.append((char)v);
                    }
                    String jsonResponse = buffer.toString();
                    //handle Json parsing and call it back
                    if(url.contains("list.php?c=list")){
                        MyApp.mainhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.networkingFinishWithCategoryJsonString(jsonResponse);
                            }
                        });
                    }else{
                        MyApp.mainhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // run in main thread
                                listener.networkingFinishWithJsonString(jsonResponse);
                            }
                        });
                    }

                    Log.d("json",jsonResponse);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("There is an error");
                    e.printStackTrace();
                }
                finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        });

    }

    void downloadImage(String image){
        MyApp.executorService.execute(new Runnable() {
            @Override
            public void run() {
                InputStream is;
                try {
                    is = (InputStream) new URL(image).getContent();
                    Bitmap d = BitmapFactory.decodeStream(is);
                    MyApp.mainhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.networkingFinishWithBitMapImage(d);
                        }
                    });
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

}
