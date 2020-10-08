package com.jkero.blackhawk.testaparatuocr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.jkero.blackhawk.testaparatuocr.adapter.CustomAdapter;
import com.jkero.blackhawk.testaparatuocr.api.Ingredient;
import com.jkero.blackhawk.testaparatuocr.R;
import com.jkero.blackhawk.testaparatuocr.adapter.RowIngredient;
import com.jkero.blackhawk.testaparatuocr.api.ApiClient;
import com.jkero.blackhawk.testaparatuocr.api.ApiInterface;
import com.jkero.blackhawk.testaparatuocr.database.DbHandler;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class ScanResultActivity extends AppCompatActivity {


    @BindView(R.id.listviewIngredients)RecyclerView listviewIngredients;
    @BindView(R.id.textviewTitle)TextView textviewTitle;
    String temp;
    ApiInterface apiInterface;
    private InterstitialAd mInterstitialAd;
    @BindArray(R.array.emoticons)TypedArray emoticons;
    @BindArray(R.array.stars)TypedArray stars;
    List<Ingredient> ingredientsListH;
    List<RowIngredient> rowItems;
    //sqlite
    DbHandler db;
    ArrayList<Integer> datasList;
    IRecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

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
        ButterKnife.bind(this);
        ingredientsListH = new ArrayList<Ingredient>();

        //emoticons = getResources().obtainTypedArray(R.array.emoticons);
        //stars = getResources().obtainTypedArray(R.array.stars);

        rowItems = new ArrayList<RowIngredient>();
        rowItems.clear();

        //listviewIngredients = (RecyclerView) findViewById(R.id.listviewIngredients);
        listviewIngredients.addItemDecoration(new DividerItemDecoration(ScanResultActivity.this, LinearLayoutManager.VERTICAL));
        //textviewTitle = (TextView) findViewById(R.id.textviewTitle);

        textviewTitle.append(getString(R.string.scan_result_title));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.scan_result_finish));
        AlertDialog alert;

        RecyclerView.LayoutManager recyce = new
                LinearLayoutManager(ScanResultActivity.this, LinearLayoutManager.VERTICAL, false);
        listviewIngredients.setLayoutManager(recyce);

        listener = new IRecyclerViewClickListener() {
            @Override
            public void onLongClicked(int pos) {

            }
        };

        switch (MainActivity.mess) {
            case "scanned by this device":
                //AlertDialog alert2;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getScannedIngredientsOfTheDevice(MainActivity.deviceId, BarcodeActivity.barcode);
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
                        saveScanToDb(MainActivity.deviceId,BarcodeActivity.barcode);
                    }
                });
                builder.setMessage(getString(R.string.scan_done));
                alert = builder.create();
                alert.show();


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

    private void saveScanToDb(String deviceId, String barcode) {
        Call<List<Ingredient>> call = apiInterface.getIngredientsScannedByDevice(deviceId,barcode);
        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, retrofit2.Response<List<Ingredient>> response) {

                for (Ingredient i : response.body()) {
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
                CustomAdapter adapter = new CustomAdapter(ScanResultActivity.this, rowItems,listener);
                listviewIngredients.setAdapter(adapter);
                listviewIngredients.setLayoutManager(new LinearLayoutManager(ScanResultActivity.this));
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {

            }
        });


    }

    private void getScannedIngredientsOfTheDevice(String deviceId, String barcode) {
        Call<List<Ingredient>> call = apiInterface.getIngredientsScannedByDevice(deviceId,barcode);
        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, retrofit2.Response<List<Ingredient>> response) {
                for (Ingredient i : response.body()) {
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
                CustomAdapter adapter = new CustomAdapter(ScanResultActivity.this, rowItems,listener);
                listviewIngredients.setAdapter(adapter);
                listviewIngredients.setLayoutManager(new LinearLayoutManager(ScanResultActivity.this));
            }
            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {

            }
        });

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
