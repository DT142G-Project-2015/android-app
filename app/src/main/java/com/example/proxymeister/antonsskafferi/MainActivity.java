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
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: thread global, pro: you cant miss it, con: disruptive
        // TODO: get account info. if account is waiter, create thread.
        client = new Thread(new CheckForOrders());

        if (threadNotRunning) {
            client.start();
        }

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

    public void showDialog(final String id, final String booth) {
        // necessary context change, UI CANNOT be handled in the new thread
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Food is ready!");
                builder.setMessage("Order ID: " + id + " /  Booth: " + booth);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        dflag = 1;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    //TODO: use the change status API to clear the "readyToServe", so that it does not repeat notifications
    // always listen for a message. if a dish is done, display dialog.
    class CheckForOrders implements Runnable {

        private List<Order> readyOrder;

        @Override
        public void run() {

            threadNotRunning = false;

            while (true) {
                try {
                    Call<List<Order>> call = Utils.getApi().getOrdersByStatus("readyToServe");
                    call.enqueue(new Callback<List<Order>>() {
                        @Override
                        public void onResponse(Response<List<Order>> response, Retrofit retrofit) {

                            int statusCode = response.code();
                            Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                            readyOrder = response.body();

                            if (!(readyOrder.isEmpty() || readyOrder == null)) {
                                dflag = 2;
                                showDialog(Integer.toString(readyOrder.get(0).getId()), Integer.toString(readyOrder.get(0).getBooth()));

                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                                // get wrecket
                                v.vibrate(2500);
                                sound();
                            } else {
                                dflag = 3;
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                        }
                    });

                    // waiting for uithread to catch up
                    while (dflag == 0)
                    {
                        Thread.sleep(100);
                    }

                    // nothing found, continue listening
                    if (dflag == 3)
                    {
                        Thread.sleep(2000);
                    }
                    else if (dflag == 2)
                    {
                        while (dflag != 1)
                        {
                            Thread.sleep(3000);
                        }
                    }

                    dflag = 0;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    threadNotRunning = true;
                    return;
                }
            }
        }
    }

    void sound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
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
            client.interrupt();
        }

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
    public void startLagerActivity(View view) {
        client.interrupt();
        Intent intent = new Intent(MainActivity.this, LagerActivity.class);
        startActivity(intent);
    }

    //-------------------------------------------------------------------------------------//
    // When you click on any of the buttons "beställningar/kök/meny/lager", the button is sent in as
    // an argument of type "view" to these functions. When the corresponding function runs, it sets
    // up a new activity called "OrderActivity". Navigate to that file if you want to implement
    // something related.

    public void openOrderActivity(View view) {
        client.interrupt();
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    public void openKitchenActivity(View view) {
        client.interrupt();
        Intent intent = new Intent(this, KitchenActivity.class);
        startActivity(intent);
    }

    public void openMenuActivity(View view) {
        client.interrupt();
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
        client.interrupt();
    }
}
