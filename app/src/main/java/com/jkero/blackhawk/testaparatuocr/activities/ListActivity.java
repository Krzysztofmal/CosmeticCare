package com.jkero.blackhawk.testaparatuocr.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.jkero.blackhawk.testaparatuocr.adapter.CustomAdapter;
import com.jkero.blackhawk.testaparatuocr.api.Ingredient;
import com.jkero.blackhawk.testaparatuocr.R;
import com.jkero.blackhawk.testaparatuocr.adapter.RowIngredient;
import com.jkero.blackhawk.testaparatuocr.api.ApiClient;
import com.jkero.blackhawk.testaparatuocr.api.ApiInterface;
import com.jkero.blackhawk.testaparatuocr.database.DbHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemLongClick;
import butterknife.OnLongClick;
import butterknife.Optional;
import retrofit2.Call;
import retrofit2.Callback;

public class ListActivity extends AppCompatActivity  {

    ApiInterface apiInterface;
    //TypedArray emoticons;
    @BindArray(R.array.emoticons) TypedArray emoticons;
    //TypedArray stars;
    @BindArray(R.array.stars) TypedArray stars;
    List<Ingredient> ingredientsListH;
    //private RecyclerView ingredientsList;
    @BindView(R.id.lvIngredients) RecyclerView ingredientsList;
    List<RowIngredient> rowItems;

    //sqlite
    DbHandler db;
    ArrayList<Integer> datasList;

    IRecyclerViewClickListener listener;

    public void getAllIngredients() {
        Call<List<Ingredient>> call = apiInterface.getAllIngredients();
        call.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, retrofit2.Response<List<Ingredient>> response) {

                Log.i(ListActivity.class.getSimpleName(), response.body().toString());

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
                //IRecyclerViewClickListener listener = new IRecyclerViewClickListener();

                CustomAdapter adapter = new CustomAdapter(ListActivity.this, rowItems,listener);
                ingredientsList.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                Toast.makeText(ListActivity.this, "rp :" +
                                t.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getLocalDb();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        ingredientsListH = new ArrayList<Ingredient>();

        //emoticons = getResources().obtainTypedArray(R.array.emoticons);
        //stars = getResources().obtainTypedArray(R.array.stars);

        rowItems = new ArrayList<RowIngredient>();
        rowItems.clear();

        //ingredientsList = (RecyclerView) findViewById(R.id.lvIngredients);

        ingredientsList.addItemDecoration(new DividerItemDecoration(ListActivity.this, LinearLayoutManager.VERTICAL));

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        RecyclerView.LayoutManager recyce = new
                LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false);
        ingredientsList.setLayoutManager(recyce);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);



        getAllIngredients();


        listener = new IRecyclerViewClickListener() {
            @Override
            public void onLongClicked(int pos) {
                boolean add = true;

                if (datasList.contains(rowItems.get(pos).getId())) {
                    db.deleteIngredient(rowItems.get(pos).getId());
                    add = false;
                    rowItems.clear();
                    getLocalDb();
                    getAllIngredients();

                    builder.setTitle(R.string.dialog_title_database);

                    builder.setMessage(R.string.dialog_removed_database);
                    AlertDialog alert = builder.create();

                    alert.show();
                }

                if (add) {
                    db.addIngredient(rowItems.get(pos).getId());
                    rowItems.clear();
                    getLocalDb();
                    getAllIngredients();

                    builder.setTitle(R.string.dialog_title_database);

                    builder.setMessage(R.string.dialog_added_database);
                    AlertDialog alert = builder.create();

                    alert.show();
                }

            }
        };

    }

    private void getLocalDb(){
        db = new DbHandler(this);
        final Cursor datas = db.getData();
        datasList = new ArrayList<Integer>();
        while (datas.moveToNext()) {
            datasList.add(datas.getInt(0));
        }
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
