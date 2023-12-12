package com.example.cocktailapp;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Cocktail implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    int id;
    String name;
    String category;
    String instruction;
    String image;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getImage() {
        return image;
    }

    public String getIngredient() {
        return ingredient;
    }

    String ingredient;

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Cocktail() {
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public Cocktail(String name, String category, String instruction, String image, String ingredient) {
        this.name = name;
        this.category = category;
        this.instruction = instruction;
        this.image = image;
        this.ingredient = ingredient;
    }

    protected Cocktail(Parcel in) {
        name = in.readString();
        category = in.readString();
        instruction = in.readString();
        image = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<Cocktail> CREATOR = new Creator<Cocktail>() {
        @Override
        public Cocktail createFromParcel(Parcel in) {
            return new Cocktail(in);
        }

        @Override
        public Cocktail[] newArray(int size) {
            return new Cocktail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(instruction);
        dest.writeString(image);
        dest.writeString(ingredient);
    }
}
