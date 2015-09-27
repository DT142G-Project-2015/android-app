package com.example.proxymeister.antonsskafferi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class KitchenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        // Sampleorder
        String order = Databas.getInstance().text;

        // An array for orders
        String[] orders = {order};


        if(order != null) {

            // ListAdapter acts as a bridge between the data and each ListItem
            ListAdapter theAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                    orders);

            // Find the ListView in activity_kitchen
            ListView theListView = (ListView) findViewById(R.id.ordersListView);

            // Tells the ListView to use adapter to display its content
            theListView.setAdapter(theAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kitchen, menu);
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
