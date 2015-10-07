package com.example.proxymeister.antonsskafferi;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Gets current screen orientation value
        int screen = getResources().getConfiguration().orientation;
        //Switches layout depending on the screen orientation
        switch (screen){
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
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            setContentView(R.layout.activity_main_land);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionbar_settings) {
            return true;
        }

        if (id == R.id.actionbar_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    // Called on by the Lager button in activity_main.xml
    // Starts the lager activity
    public void startLagerActivity(View view)
    {
        Intent intent;
        intent = new Intent(MainActivity.this, LagerActivity.class);
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


}
