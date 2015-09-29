package com.example.proxymeister.antonsskafferi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OrderLunchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<String> strings;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        list = (ListView)findViewById(R.id.list);

        int id = getIntent().getIntExtra("menu-id", 0);

        Utils.FetchURL.Callback callback = new Utils.FetchURL.Callback() {

            @Override
            public void onComplete(Object result) {

                Gson gson = new Gson();

                // convert the JSON string to a List of Items
                List<Item> persons = gson.fromJson((String)result,
                        new TypeToken<List<Item>>() {}.getType());

                // strings = persons.map(_.toString())

                strings = new ArrayList<>();
                for (Item p : persons) {
                    strings.add(p.toString());
                }

                // create simple ArrayAdapter to hold the strings for the ListView
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<>(OrderLunchActivity.this, android.R.layout.simple_list_item_1, strings);

                // pass the adapter to the ListView
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


        // Item Click Listener for the list
        list.setOnItemClickListener(this);



    }

    public void onItemClick(AdapterView<?> parent, View container, int position, long id) {

        Toast.makeText(OrderLunchActivity.this,
                "Item in position " + position + " clicked",
                Toast.LENGTH_LONG).show();
        Databas.Order o = new Databas.Order();
        o.text = strings.get(position);
        Databas.getInstance().orders.add(o);
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