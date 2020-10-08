package com.jkero.blackhawk.testaparatuocr.activities;


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

import org.json.JSONArray;
import org.json.JSONException;
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
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {

    ApiInterface apiInterface;
    @BindView(R.id.tvBarcode)TextView textView;
    @BindView(R.id.lvIngredients)RecyclerView lvIngredients;
    List<RowIngredient> rowItems;
    @BindArray(R.array.emoticons)TypedArray emoticons;
    @BindArray(R.array.stars)TypedArray stars;
    private InterstitialAd mInterstitialAd;
    //sqlite
    DbHandler db;
    ArrayList<Integer> datasList;
    IRecyclerViewClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DbHandler(this);
        final Cursor datas = db.getData();
        datasList = new ArrayList<Integer>();
        while (datas.moveToNext()) {
            datasList.add(datas.getInt(0));
        }

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

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

        ButterKnife.bind(this);

        rowItems = new ArrayList<RowIngredient>();

        //lvIngredients = (RecyclerView) findViewById(R.id.lvIngredients);
        lvIngredients.addItemDecoration(new DividerItemDecoration(ResultActivity.this, LinearLayoutManager.VERTICAL));

        RecyclerView.LayoutManager recyce = new
                LinearLayoutManager(ResultActivity.this, LinearLayoutManager.VERTICAL, false);
        lvIngredients.setLayoutManager(recyce);

        //textView = (TextView) findViewById(R.id.tvBarcode);
        textView.append(" " + BarcodeActivity.barcode);

        listener = new IRecyclerViewClickListener() {
            @Override
            public void onLongClicked(int pos) {

            }
        };

        //emoticons = getResources().obtainTypedArray(R.array.emoticons);
        //stars = getResources().obtainTypedArray(R.array.stars);

        getResult(BarcodeActivity.barcode);

        //lvIngredients.setOnItemClickListener(this);

    }




    private void getResult(String barcode){
        Call <String> callTypes = apiInterface.getIngredientsByBarcode2(barcode);
        callTypes.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONArray jsonObject = new JSONArray(response.body());

                    for (Ingredient i : Ingredient.fromJsonList(jsonObject)) {
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
                    CustomAdapter adapter = new CustomAdapter(ResultActivity.this, rowItems,listener);
                    lvIngredients.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });



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
