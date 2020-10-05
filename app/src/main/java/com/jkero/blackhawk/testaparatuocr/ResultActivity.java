package com.jkero.blackhawk.testaparatuocr;

import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.database.Cursor;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResultActivity extends AppCompatActivity {

    TextView textView;
    RecyclerView lvIngredients;

    List<RowIngredient> rowItems;

    TypedArray emoticons;
    TypedArray stars;

    private InterstitialAd mInterstitialAd;

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


        MobileAds.initialize(this, "ca-app-pub-8457083331420228~3984558764");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8457083331420228/3422641357");
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // test
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mInterstitialAd.show();
                    }
                }, 500);
            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        rowItems = new ArrayList<RowIngredient>();

        lvIngredients = (RecyclerView) findViewById(R.id.lvIngredients);
        lvIngredients.addItemDecoration(new DividerItemDecoration(ResultActivity.this, LinearLayoutManager.VERTICAL));
        textView = (TextView) findViewById(R.id.tvBarcode);
        textView.append(" " + BarcodeActivity.barcode);

        emoticons = getResources().obtainTypedArray(R.array.emoticons);
        stars = getResources().obtainTypedArray(R.array.stars);


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_GETINGREDIENTSBYBARCODE,
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
                            //CustomAdapter adapter = new CustomAdapter(getApplicationContext(), rowItems);
                            //lvIngredients.setAdapter(adapter);
                            lvIngredients.setAdapter(new CustomAdapter(ResultActivity.this, rowItems));
                            lvIngredients.setLayoutManager(new LinearLayoutManager(ResultActivity.this));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("barcode", BarcodeActivity.barcode);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


        //lvIngredients.setOnItemClickListener(this);


    }

/*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(rowItems.get(position).getIngredientName());

        builder.setMessage(rowItems.get(position).getIngredientDesc());
        AlertDialog alert = builder.create();

        alert.show();


    }
*/

}
