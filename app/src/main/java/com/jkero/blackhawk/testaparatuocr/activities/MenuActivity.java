package com.jkero.blackhawk.testaparatuocr.activities;


import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jkero.blackhawk.testaparatuocr.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends AppCompatActivity {

    public static String myLanguage;
    private Dialog myDialog;
    private Button bClose;
    private InterstitialAd mInterstitialAd;
    //private AdView mAdView;
    @BindView(R.id.adView) AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        myLanguage = "apk/"+getString(R.string.set_language);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //9B658CACCC5483A78F8EBFD7CE7F3C73

        MobileAds.initialize(this, "ca-app-pub-8457083331420228~3984558764");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8457083331420228/8858778787");
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // test
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });


    }

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestGPSPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                }
            }
        }
    }
    */


    public void goAlergens(View view) {
        //mInterstitialAd.show();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MenuActivity.this, ListActivity.class);
                startActivity(intent);
            }
        }, 200);


    }

    public void exit(View view) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 200);
        //System.out.println("MY LANGUAGE:"+getString(R.string.set_language)+"x");
        Log.i("MenuActivity", getString(R.string.set_language));

    }

    public void goBarcode(View view) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MenuActivity.this, BarcodeActivity.class);
                //Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 200);

    }

    public void aboutApp(View view) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showCustomDialog();
            }
        }, 200);
    }

    private void showCustomDialog() {
        myDialog = new Dialog(MenuActivity.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.custom_dialog);
        myDialog.setTitle(getString(R.string.menu_about));
        bClose = (Button) myDialog.findViewById(R.id.bClose);
        bClose.setEnabled(true);

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.cancel();
            }
        });

        myDialog.show();
    }
}
