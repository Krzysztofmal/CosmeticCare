package com.jkero.blackhawk.testaparatuocr.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.jkero.blackhawk.testaparatuocr.api.Ingredient;
import com.jkero.blackhawk.testaparatuocr.R;
import com.jkero.blackhawk.testaparatuocr.api.ApiClient;
import com.jkero.blackhawk.testaparatuocr.api.ApiInterface;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    //SurfaceView cameraView;
    @BindView(R.id.surface_view) SurfaceView cameraView;
    //TextView textView;
    @BindView(R.id.text_view) TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    private List<Ingredient> ingredientsList;
    public static String idIngredients = "";
    public static String deviceId = "";
    public static String mess;
    ApiInterface apiInterface;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

//**

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

//**

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


    AlertDialog alert;

    boolean scanDone;
    boolean startScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idIngredients = "";
        deviceId = "";

        scanDone = false;
        startScanning = true;
        deviceId = id(getApplicationContext()).toString();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<String> callType = apiInterface.checkIfScanned(BarcodeActivity.barcode,deviceId);
        callType.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    String temp = jsonObject.getString("scanned");
                    if (temp.contains("true")) {
                        finish();
                        mess = "scanned by this device";
                    } else {
                        mess = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        ingredientsList = new ArrayList<Ingredient>();


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        //cameraView = (SurfaceView) findViewById(R.id.surface_view);
        //textView = (TextView) findViewById(R.id.text_view);

//***

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        //getting datas from db, list of ingredients with ID's

        Call<List<Ingredient>> callType2 = apiInterface.getAllIngredients();
        callType2.enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, retrofit2.Response<List<Ingredient>> response) {
                ingredientsList.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {

            }
        });


        //finish

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {

            //preview size
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1920, 1080)
                    .setRequestedFps(60.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });
            StringBuilder sb = new StringBuilder();
            final StringBuilder stringBuilder = new StringBuilder();
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {

                                String[] stringList;

                                for (int i = 0; i < items.size(); ++i) {

                                    TextBlock item = items.valueAt(i);

                                    stringList = item.getValue()
                                            .replace(" ", "")
                                            .replace(":", ",")
                                            //.replace("•",",")
                                            .replace(".", "")
                                            .toLowerCase()
                                            .split(",");

                                    //solution 1

                                    for (Ingredient ing : ingredientsList) {

                                        if (item.getValue().toLowerCase().contains(ing.getNameIngredient().toLowerCase())) {
                                            if (startScanning) {
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finish();
                                                        scanDone = true;
                                                    }
                                                }, 15000);
                                                startScanning = false;
                                            }


                                            //rozpoznanie zawiera nazwę składnika z bazy to
                                            if (!stringBuilder.toString().contains(ing.getNameIngredient())) {
                                                stringBuilder.append(ing.getNameIngredient());
                                                stringBuilder.append("\n");
                                                //dodanie do stringów id produktu
                                                if (idIngredients.isEmpty()) {
                                                    idIngredients += ing.getIdIngredient();
                                                } else {
                                                    idIngredients += "," + ing.getIdIngredient();
                                                }
                                            }
                                        }
                                    }
                                }
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    public void exit(View view) {
        finish();
    }

    public void goMenu(View view) {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (idIngredients.length() < 1 && scanDone == true) {
            Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
            startActivity(intent);
            mess = "1";
        } else {
            Call<String> callType3 = apiInterface.doScan(BarcodeActivity.barcode,MainActivity.deviceId,MainActivity.idIngredients);
            callType3.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    try {
                        if (mess != null) {
                            Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
                            startActivity(intent);
                        } else {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String temp = jsonObject.getString("message");
                            mess = temp;
                            Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

        }
        alert.dismiss();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.scan_dialog_title));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setMessage(getString(R.string.scan_dialog_info));
        alert = builder.create();

        alert.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
