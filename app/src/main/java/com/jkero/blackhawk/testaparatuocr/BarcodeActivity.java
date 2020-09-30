package com.jkero.blackhawk.testaparatuocr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    public static String barcode = "";
    private List<Product> productList;
    private boolean inDb;


    final int RequestCameraPermissionID = 1001;




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults){
        switch (requestCode)
        {
            case RequestCameraPermissionID:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcode = "";




        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        //scannerView.startCamera();

        //productList = new ArrayList<Product>();
        inDb = false;


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BarcodeActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    RequestCameraPermissionID);
            return;
        }






    }



    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }



    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }

        return uniqueID;
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        scannerView.stopCamera();




    }


    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause



    }

    @Override
    public void handleResult(Result result) {

        final String scanResult = result.getText();
        barcode = result.getText();
        inDb = false;

        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println(scanResult);
        // **************


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_CHECKIFUSEDBARCODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    System.out.println("***************************");
                        System.out.println("***************************");
                        System.out.println(response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String temp = jsonObject.getString("exist");
                            if (temp.contains("true")) inDb=true;

                            if (inDb){
                                Intent intent = new Intent(BarcodeActivity.this,ResultActivity.class);

                                finish();
                                startActivity(intent);
                                //finish();
                                System.out.println(barcode);
                            } else if (!inDb) {
                                Intent intent = new Intent(BarcodeActivity.this,MainActivity.class);

                                finish();
                                startActivity(intent);

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            protected Map<String,String> getParams () throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("barcode", barcode);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);





    }


    @Override
    protected void onStop() {
        super.onStop();



    }
}
