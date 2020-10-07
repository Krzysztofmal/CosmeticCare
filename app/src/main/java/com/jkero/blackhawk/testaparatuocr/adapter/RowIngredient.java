package com.jkero.blackhawk.testaparatuocr.adapter;

import androidx.recyclerview.widget.RecyclerView;

public class RowIngredient {


    private int id;
    private String ingredientName;
    private String ingredientDesc;
    private int imageStar;
    private int imageEmoticon;

    public RowIngredient(String ingredientName, String ingredientDesc, int imageStar, int imageEmoticon, int id) {
        this.ingredientName = ingredientName;
        this.ingredientDesc = ingredientDesc;
        this.imageStar = imageStar;
        this.imageEmoticon = imageEmoticon;
        this.id = id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientDesc() {
        return ingredientDesc;
    }

    public void setIngredientDesc(String ingredientDesc) {
        this.ingredientDesc = ingredientDesc;
    }

    public int getImageStar() {
        return imageStar;
    }

    public void setImageStar(int imageStar) {
        this.imageStar = imageStar;
    }

    public int getImageEmoticon() {
        return imageEmoticon;
    }

    public void setImageEmoticon(int imageEmoticon) {
        this.imageEmoticon = imageEmoticon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
