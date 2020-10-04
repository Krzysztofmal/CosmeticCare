package com.jkero.blackhawk.testaparatuocr;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ScanResultActivity extends AppCompatActivity {


    ListView listviewIngredients;
    TextView textviewTitle;
    String temp;


    private InterstitialAd mInterstitialAd;

    TypedArray emoticons;
    TypedArray stars;
    List<Ingredient> ingredientsListH;
    List<RowIngredient> rowItems;

    //sqlite
    DbHandler db;
    ArrayList<Integer> datasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        MobileAds.initialize(this, "ca-app-pub-8457083331420228~3984558764");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8457083331420228/7283609716");
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

        db = new DbHandler(this);
        final Cursor datas = db.getData();
        datasList = new ArrayList<Integer>();
        while (datas.moveToNext()) {
            datasList.add(datas.getInt(0));
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ingredientsListH = new ArrayList<Ingredient>();

        emoticons = getResources().obtainTypedArray(R.array.emoticons);
        stars = getResources().obtainTypedArray(R.array.stars);

        rowItems = new ArrayList<RowIngredient>();
        rowItems.clear();


        listviewIngredients = (ListView) findViewById(R.id.listviewIngredients);
        textviewTitle = (TextView) findViewById(R.id.textviewTitle);

        textviewTitle.append(getString(R.string.scan_result_title));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.scan_result_finish));
        AlertDialog alert;

        switch (MainActivity.mess) {
            case "scanned by this device":
                //AlertDialog alert2;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                Constants.URL_GETINGREDIENTSOFDEVICE,
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
                                            CustomAdapter adapter = new CustomAdapter(getApplicationContext(), rowItems);
                                            listviewIngredients.setAdapter(adapter);

                                            listviewIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ScanResultActivity.this);
                                                    AlertDialog alert2;
                                                    builder2.setTitle(rowItems.get(position).getIngredientName());

                                                    builder2.setMessage(rowItems.get(position).getIngredientDesc());
                                                    alert2 = builder2.create();

                                                    alert2.show();
                                                }
                                            });


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
                                params.put("device_id", MainActivity.deviceId);
                                return params;
                            }
                        };

                        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


                    }
                });
                builder.setMessage(getString(R.string.scan_already_scanned));

                alert = builder.create();
                alert.show();
                break;
            case "zapisywanie do bazy skanu":

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                Constants.URL_GETINGREDIENTSOFDEVICE,
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
                                            CustomAdapter adapter = new CustomAdapter(getApplicationContext(), rowItems);
                                            listviewIngredients.setAdapter(adapter);

                                            listviewIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ScanResultActivity.this);
                                                    AlertDialog alert2;
                                                    builder2.setTitle(rowItems.get(position).getIngredientName());

                                                    builder2.setMessage(rowItems.get(position).getIngredientDesc());
                                                    alert2 = builder2.create();

                                                    alert2.show();
                                                }
                                            });


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
                                params.put("device_id", MainActivity.deviceId);
                                return params;
                            }
                        };

                        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


                    }
                });
                builder.setMessage(getString(R.string.scan_done));
                alert = builder.create();
                alert.show();

                //lista zeskanowana


                //koniec listy xd

                break;
            case "wiecej jak 4":

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setMessage(getString(R.string.scan_done));
                alert = builder.create();
                alert.show();
                break;
            default:

                //tez uzupelnic liste?
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                });
                builder.setMessage(getString(R.string.scan_quality));
                alert = builder.create();
                alert.show();
                break;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
