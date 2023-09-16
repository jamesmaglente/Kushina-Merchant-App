package com.kushina.merchant.android;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    private static int SPLASH_TIME_OUT = 8000;
    @BindView(R.id.tv_vesion)
    TextView tvVersion;
    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        mGlobals = new Globals(this);
        mAPI = new API(this);
        mPreferences = new Preferences(this);

        getSupportActionBar().hide();
        View mDecorView = SplashScreenActivity.this.getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startApp();

                finish();
            }
        }, SPLASH_TIME_OUT);

        String current_version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersion.setText("v" + current_version);
    }

    private void startApp() {
//        if (mDatabaseHelper.isAlreadyLogged()) {
//            Cursor data = mDatabaseHelper.getFullname();
//            data.moveToNext();
//            String full_name = data.getString(0);
//
//            // show welcome back message
//            mGlobals.toast("Welcome back " + full_name + "!");
//
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//        } else {
//        lottieCooking.pauseAnimation();
//        lottieCooking.setVisibility(View.INVISIBLE);
        ivSplash.setVisibility(View.INVISIBLE);
     //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
//        }
    }
}
