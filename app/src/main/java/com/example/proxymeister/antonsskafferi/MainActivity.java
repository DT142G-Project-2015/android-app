package com.example.proxymeister.antonsskafferi;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private static boolean threadNotRunning = true;
        private int dflag = 0;
        private Thread client;
        boolean logout;
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

  /*  @Override
    public void onBackPressed() {

// dont call **super**, if u want disable back button in current screen.
    }*/

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


        if (id == R.id.actionbar_logout) {


                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor =sp.edit();
                editor.clear();
                editor.putString("username", "");
                editor.putString("password", "");
                editor.apply();
                finish();
        }

        return super.onOptionsItemSelected(item);

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

    public void openLagerActivity(View view) {

        Intent intent = new Intent(MainActivity.this, LagerActivity.class);
        startActivity(intent);
    }

}
