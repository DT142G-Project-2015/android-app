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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class KitchenActivity extends SwipeListViewActivity {
    private List<String> orders;
    private String order;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);


        // Sampleorder
        order = "";

        if(Databas.getInstance().text != null)
        {
            order = Databas.getInstance().text;
        }

        // An array for orders
        orders = new ArrayList<>();
        orders.add(order);

        // Find the ListView in activity_kitchen
        mListView = (ListView) findViewById(R.id.ordersListView);

        // ListAdapter acts as a bridge between the data and each ListItem
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, orders);

        // Tells the ListView to use adapter to display its content
        mListView.setAdapter(mAdapter);



    }

    @Override
    public ListView getListView() {
        return mListView;
    }

    // See SwipeListViewActivity
    @Override
    public void deleteSwipedItem(boolean isLeft, int position) {

        if(isLeft)
        {
            String item = orders.get(position);
            orders.remove(item);
            mAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, orders);
            mListView.setAdapter(mAdapter);
            Databas.getInstance().text = null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kitchen, menu);
        return true;
    }

    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
        Toast.makeText(this, "Single tap on item position " + position,
                Toast.LENGTH_SHORT).show();
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
