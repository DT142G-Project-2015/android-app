package com.example.proxymeister.antonsskafferi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.proxymeister.antonsskafferi.model.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        int id = getIntent().getIntExtra("menu-id", 0);

        Utils.FetchURL.Callback callback = new Utils.FetchURL.Callback() {

            @Override
            public void onComplete(Object result) {

                Gson gson = new Gson();

                // convert the JSON string to a List of Items
                List<Item> persons = gson.fromJson((String)result,
                        new TypeToken<List<Item>>() {}.getType());

                // strings = persons.map(_.toString())
                List<String> strings = new ArrayList<String>();
                for (Item p : persons) {
                    strings.add(p.toString());
                }

                // create simple ArrayAdapter to hold the strings for the ListView
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(MenuActivity.this, android.R.layout.simple_list_item_1, strings);

                // pass the adapter to the ListView
                ListView list = (ListView)findViewById(R.id.list);
                list.setAdapter(itemsAdapter);

                Log.i(MainActivity.class.getName(), "complete");
            }

            @Override
            public void onError() {
                Log.i(MainActivity.class.getName(), "error");
            }
        };



        Utils.FetchURL fetchMenuItems = new Utils.FetchURL(callback);

        try {
            fetchMenuItems.execute(new URL("http://46.254.14.163/web-app/api/menu/"+ id +"/item"));
        } catch (MalformedURLException e) {} // ignore

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
