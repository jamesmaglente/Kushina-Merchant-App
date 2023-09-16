package com.kushina.merchant.android.navigations;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.kushina.merchant.android.LoginActivity;
import com.kushina.merchant.android.R;
import com.kushina.merchant.android.globals.API;
import com.kushina.merchant.android.globals.Globals;
import com.kushina.merchant.android.globals.MainReceiver;
import com.kushina.merchant.android.globals.MainService;
import com.kushina.merchant.android.globals.Preferences;
import com.kushina.merchant.android.globals.ui.CountDrawable;
import com.kushina.merchant.android.navigations.notifications.NotificationsActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    private AppBarConfiguration mAppBarConfiguration;

    private int UPDATE_DIALOG_VISIBLE;
    MainReceiver receiver = new MainReceiver(new ResponseResult());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        mGlobals = new Globals(this);
        mAPI = new API(this);
        mPreferences = new Preferences(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_leaderboard,
                R.id.nav_users,
                R.id.nav_categories,
                R.id.nav_items,
                R.id.nav_deposits,
                R.id.nav_vouchers,
                R.id.nav_orders,
                R.id.nav_withdrawals
        )
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch(item.getItemId()){

            case R.id.action_notifications:
                intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setCount(this, String.valueOf(mPreferences.getNotificationCount()), menu);
        return true;
    }

    public void setCount(Context context, String count, Menu defaultMenu) {
        MenuItem menuItem = defaultMenu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_notification_count);
        if (reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_notification_count, badge);
    }

    public class ResponseResult {
        public void displayMessage(int resultCode, Bundle resultData){

            if(UPDATE_DIALOG_VISIBLE == 0){

                String title = resultData.getString("title");
                String message = resultData.getString("message");
                String task = resultData.getString("task");

//                mGlobals.toast(task);
                switch (task){
                    case "show_dialog":
                        UPDATE_DIALOG_VISIBLE = 1;
                        mGlobals.showDialog(title, message, true, new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                        break;
                    case "update_badge":
                        invalidateOptionsMenu();
                        break;
                }

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UPDATE_DIALOG_VISIBLE = 0;

        // start service
        Intent serviceIntent = new Intent(this, MainService.class);
        serviceIntent.putExtra("receiver", receiver);
        startService(serviceIntent);

    }

    public void logout(){
        mGlobals.alert("Notice",
                "You are about to logout. Continue?",
                "Logout",
                "No",
                null,
                MainActivity.this,
                true,
                new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            stopService(new Intent(MainActivity.this, MainService.class));
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        FragmentManager ft = getSupportFragmentManager();
        if(ft.getBackStackEntryCount() == 1){
            logout();
        } else {
            super.onBackPressed();
        }
    }
}
