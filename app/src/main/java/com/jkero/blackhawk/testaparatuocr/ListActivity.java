package com.jkero.blackhawk.testaparatuocr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListActivity extends AppCompatActivity {


    TypedArray emoticons;
    TypedArray stars;
    List<Ingredient> ingredientsListH;

    //private ListView ingredientsList;
    private RecyclerView ingredientsList;
    List<RowIngredient> rowItems;


    //sqlite
    DbHandler db;
    ArrayList<Integer> datasList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        db = new DbHandler(this);
        final Cursor datas = db.getData();
        datasList = new ArrayList<Integer>();
        while (datas.moveToNext()) {
            datasList.add(datas.getInt(0));
        }


        //mInterstitialAd.show();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ingredientsListH = new ArrayList<Ingredient>();

        emoticons = getResources().obtainTypedArray(R.array.emoticons);
        stars = getResources().obtainTypedArray(R.array.stars);

        rowItems = new ArrayList<RowIngredient>();
        rowItems.clear();

        //ingredientsList = (ListView) findViewById(R.id.lvIngredients);
        ingredientsList = (RecyclerView) findViewById(R.id.lvIngredients);

        ingredientsList.addItemDecoration(new DividerItemDecoration(ListActivity.this, LinearLayoutManager.VERTICAL));


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_GETALLINGREDIENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (Ingredient i : Ingredient.fromJsonList(jsonArray)) {


                                if (!datasList.isEmpty()) {

                                    if (datasList.contains(i.getIdIngredient())) {
                                        rowItems.add(0, new RowIngredient(i.getNameIngredient(), i.getDescription(), emoticons.getResourceId(i.getDanger(), 0), stars.getResourceId(1, 0), i.getIdIngredient()));
                                    } else {
                                        if (rowItems.size() == 0) {
                                            rowItems.add(0, new RowIngredient(i.getNameIngredient(), i.getDescription(), emoticons.getResourceId(i.getDanger(), 0), stars.getResourceId(0, 0), i.getIdIngredient()));
                                        } else if (rowItems.size() == 1) {
                                            rowItems.add(1, new RowIngredient(i.getNameIngredient(), i.getDescription(), emoticons.getResourceId(i.getDanger(), 0), stars.getResourceId(0, 0), i.getIdIngredient()));

                                        } else if (rowItems.size() > 1) {
                                            rowItems.add(rowItems.size() - 1, new RowIngredient(i.getNameIngredient(), i.getDescription(), emoticons.getResourceId(i.getDanger(), 0), stars.getResourceId(0, 0), i.getIdIngredient()));
                                        }
                                    }
                                } else {
                                    rowItems.add(new RowIngredient(i.getNameIngredient(), i.getDescription(), emoticons.getResourceId(i.getDanger(), 0), stars.getResourceId(0, 0), i.getIdIngredient()));

                                }


                            }
                            CustomAdapter adapter = new CustomAdapter(ListActivity.this, rowItems);
                            adapter.notifyDataSetChanged();
                            ingredientsList.setAdapter(adapter);
                            //ingredientsList.setAdapter(new CustomAdapter(getApplicationContext(), rowItems));
                            ingredientsList.setLayoutManager(new LinearLayoutManager(ListActivity.this));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);



/*
        ingredientsList.setOnItemClickListener(this);


        ingredientsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                boolean add = true;
                if (datasList.contains(rowItems.get(pos).getId())) {

                    db.deleteIngredient(rowItems.get(pos).getId());

                    add = false;

                    Intent intent = new Intent(ListActivity.this, ListActivity.class);
                    finish();
                    startActivity(intent);
                }

                if (add) {
                    db.addIngredient(rowItems.get(pos).getId());

                    Intent intent = new Intent(ListActivity.this, ListActivity.class);
                    finish();
                    startActivity(intent);
                }

                return true;
            }
        });*/


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
