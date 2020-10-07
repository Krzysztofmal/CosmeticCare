package com.jkero.blackhawk.testaparatuocr.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.zxing.Result;
import com.jkero.blackhawk.testaparatuocr.R;
import com.jkero.blackhawk.testaparatuocr.api.ApiClient;
import com.jkero.blackhawk.testaparatuocr.api.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;

public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    public static String barcode = "";
    private boolean inDb;

    final int RequestCameraPermissionID = 1001;

    ApiInterface apiInterface;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.alert_permission_title);
                    builder.setMessage(R.string.alert_permission_description);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
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

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

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

    @Override
    public void onDestroy() {
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
        checkBarcode(barcode);

    }

    private void checkBarcode(String bcode){

        Call<String> callType = apiInterface.checkIfBarcodeUsed(barcode);

        callType.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call <String>call, retrofit2.Response <String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    String temp = jsonObject.getString("exist");
                    if (temp.contains("true")) inDb = true;
                    if (inDb) {
                        Intent intent = new Intent(BarcodeActivity.this, ResultActivity.class);
                        finish();
                        startActivity(intent);
                    } else if (!inDb) {
                        Intent intent = new Intent(BarcodeActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call <String> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
