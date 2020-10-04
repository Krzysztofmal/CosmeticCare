package com.jkero.blackhawk.testaparatuocr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Ingredient {

    private Integer idIngredient;
    private String nameIngredient;
    private Integer danger;
    private String description;

    public Ingredient() {
    }

    public Ingredient(Integer idIngredient, String nameIngredient, Integer danger, String description) {
        this.idIngredient = idIngredient;
        this.nameIngredient = nameIngredient;
        this.danger = danger;
        this.description = description;
    }

    public Integer getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(Integer idIngredient) {
        this.idIngredient = idIngredient;
    }

    public String getNameIngredient() {
        return nameIngredient;
    }

    public void setNameIngredient(String nameIngredient) {
        this.nameIngredient = nameIngredient;
    }

    public Integer getDanger() {
        return danger;
    }

    public void setDanger(Integer danger) {
        this.danger = danger;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // methods JSON


    public static Ingredient fromJson(JSONObject jsonObject) {
        Ingredient b = new Ingredient();
        // Deserialize json into object fields
        try {
            b.setIdIngredient(jsonObject.getInt("id_ingredient"));
            b.setNameIngredient(jsonObject.getString("name_ingredient"));
            b.setDanger(jsonObject.getInt("danger"));
            b.setDescription(jsonObject.getString("description"));
            //System.out.println(b.getName_ingredient());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return b;
    }


    // lists of ingredients

    public static ArrayList<Ingredient> fromJsonList(JSONArray jsonArray) {
        JSONObject ingredientsJson;
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                ingredientsJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Ingredient ing = Ingredient.fromJson(ingredientsJson);
            if (ing != null) {
                ingredients.add(ing);
            }
        }

        return ingredients;
    }


}
