package com.jkero.blackhawk.testaparatuocr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Product {

    private Integer id_product;
    private String barcode;

    public Product(Integer id_product, String barcode) {
        this.id_product = id_product;
        this.barcode = barcode;
    }

    public Product(){}

    public Integer getId_product() {
        return id_product;
    }

    public void setId_product(Integer id_product) {
        this.id_product = id_product;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


    //methods JSON

    public static Product fromJson(JSONObject jsonObject) {
        Product b = new Product();
        // Deserialize json into object fields
        try {
            b.setId_product(jsonObject.getInt("id_product"));
            b.setBarcode(jsonObject.getString("barcode"));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return b;
    }


    // lists of products

    public static ArrayList<Product> fromJsonList(JSONArray jsonArray) {
        JSONObject productsJson;
        ArrayList<Product> products = new ArrayList<Product>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                productsJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Product prod = Product.fromJson(productsJson);
            if (prod != null) {
                products.add(prod);
            }
        }

        return products;
    }




}
