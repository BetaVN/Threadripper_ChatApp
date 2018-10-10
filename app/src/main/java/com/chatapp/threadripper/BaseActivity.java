package com.chatapp.threadripper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.threadripper.utils.ShowToast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    TextView title;
    ImageView btnImgBack;

    boolean doubleBackToExitPressedOnce = false;

    // WifiP2pManager mManager;
    // Channel mChannel;
    // BroadcastReceiver mReceiver;

    // IntentFilter mIntentFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        // mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        // mChannel = mManager.initialize(this, getMainLooper(), null);
        // mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        //
        // mIntentFilter = new IntentFilter();
        // mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }


    // /* register the broadcast receiver with the intent values to be matched */
    // @Override
    // protected void onResume() {
    //     super.onResume();
    //     registerReceiver(mReceiver, mIntentFilter);
    // }
    //
    // /* unregister the broadcast receiver */
    // @Override
    // protected void onPause() {
    //     super.onPause();
    //     unregisterReceiver(mReceiver);
    // }


    public final void changeTitle(int toolbarId, String titlePage) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setTitle("");
    }

    public final void setupToolbar(int toolbarId, String titlePage) {
        changeTitle(toolbarId, titlePage);
    }

    public void setupToolbarWithBackButton(int toolbarId, String titlePage) {
        setupToolbar(toolbarId, titlePage);

        btnImgBack = (ImageView) findViewById(R.id.btnImgBack);
        btnImgBack.setVisibility(View.VISIBLE);
        btnImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setupDoubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        ShowToast.lengthShort(this, "Please click BACK again to exit");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
