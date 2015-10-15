package com.example.proxymeister.antonsskafferi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.proxymeister.antonsskafferi.model.Order;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends AppCompatActivity {

    private static boolean threadNotRunning = true;
        private int dflag = 0;
        private Thread client;
        Button logout;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // TODO: thread global, pro: you cant miss it, con: disruptive
            // TODO: get account info. if account is waiter, create thread.


            //Gets current screen orientation value
        int screen = getResources().getConfiguration().orientation;
        //Switches layout depending on the screen orientation
        switch (screen) {
            case 0:
                setContentView(R.layout.activity_main_land); //SQUARE (value 0)
                break;
            case 1:
                setContentView(R.layout.activity_main); //PORTRAIT (value 1)
                break;
            case 2:
                setContentView(R.layout.activity_main_land); //LANDSCAPE (value 2)
                break;
            default:
                setContentView(R.layout.activity_main); //DEFAULT (value ?)
                break;
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_land);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            if (client != null)
                client.interrupt();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionbar_settings) {
            return true;

        }

        if (id == R.id.actionbarlog_out) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    // Called on by the Lager button in activity_main.xml
    // Starts the lager activity
    public void startLagerActivity(View view) {

        Intent intent = new Intent(MainActivity.this, LagerActivity.class);
        startActivity(intent);
    }

    //-------------------------------------------------------------------------------------//
    // When you click on any of the buttons "beställningar/kök/meny/lager", the button is sent in as
    // an argument of type "view" to these functions. When the corresponding function runs, it sets
    // up a new activity called "OrderActivity". Navigate to that file if you want to implement
    // something related.

    public void openOrderActivity(View view) {

        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    public void openKitchenActivity(View view) {

        Intent intent = new Intent(this, KitchenActivity.class);
        startActivity(intent);
    }

    public void openMenuActivity(View view) {

        Intent intent = new Intent(this, MenuListActivity.class);
        startActivity(intent);
    }

    //public void MenuListActivity(View view) {
    //  Intent intent = new Intent(this, MenuActivity.class);
    //startActivity(intent);
    //}
    //-------------------------------------------------------------------------------------//

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("okk");

    }
}
