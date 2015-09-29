package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class KitchenActivity extends SwipeListViewActivity {
    private List<String> orders = new ArrayList<>();
    private List<String> deletedorders = new ArrayList<>();
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    int oldposition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);


        for (Databas.Order o : Databas.getInstance().orders) {
            orders.add(o.text);
        }


        // Find the ListView in activity_kitchen
        mListView = (ListView) findViewById(R.id.ordersListView);

        // ListAdapter acts as a bridge between the data and each ListItem
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, orders);

        // Tells the ListView to use adapter to display its content
        mListView.setAdapter(mAdapter);


        // Undo delete button
        Button deletebtn = (Button) findViewById(R.id.undodeletebutton);

        View.OnClickListener oclbtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!deletedorders.isEmpty()) {
                    String item = deletedorders.get(deletedorders.size() - 1);
                    orders.add(item);
                    /*
                    if(orders.get(oldposition) == null)
                        orders.add(item);
                    else
                    {
                        orders.add("");
                        for(int i = orders.size()-1; i > oldposition; i++ )
                        {
                            String temp = orders.get(i);

                            orders.add(oldposition, item);
                        }
                    }

                    */

                    deletedorders.remove(item);
                    mAdapter = new ArrayAdapter<>(KitchenActivity.this,
                            android.R.layout.simple_list_item_1, orders);

                    mListView.setAdapter(mAdapter);
                    Databas.Order o = new Databas.Order();
                    o.text = item;
                    Databas.getInstance().orders.add(o);
                }
            }
        };

        deletebtn.setOnClickListener(oclbtn);


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
            deletedorders.add(item);
            orders.remove(item);
            mAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, orders);
            mListView.setAdapter(mAdapter);
            Databas.getInstance().orders.remove(position);
           // oldposition = position;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kitchen, menu);
        return true;
    }

    @Override
    // Single tap on each item.
    public void onItemClickListener(ListAdapter adapter, int position) {
        //Toast.makeText(this, "Single tap on item position " + position,
         //       Toast.LENGTH_SHORT).show();
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
