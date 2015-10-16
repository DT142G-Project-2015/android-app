package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.ItemAdapter;
import com.example.proxymeister.antonsskafferi.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OrderSubItemActivity extends AppCompatActivity /*implements AdapterView.OnItemClickListener*/ {
    private List<String> strings;
    private ListView list;
    private Button addOrderButton;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_meal);
        list = (ListView)findViewById(R.id.list);


        int id = getIntent().getIntExtra("menu-id", 1);
        final int orderId = getIntent().getIntExtra("order-id", 1);
        final int groupId = getIntent().getIntExtra("group-id", 1);
        final int itemId = getIntent().getIntExtra("item-id", 1);
        final int pos = getIntent().getIntExtra("pos", 1);

        Call<List<Item>> call = Utils.getApi(OrderSubItemActivity.this).getMenuItems(id);

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Response<List<Item>> response, Retrofit retrofit) {

                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                List<Item> items = response.body();

                if (items != null) {
                    itemAdapter = new ItemAdapter(OrderSubItemActivity.this, items, orderId, groupId, itemId, pos, new ItemAdapter.Callback() {
                        @Override
                        public void itemAdded() {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", pos);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    });
                    list.setAdapter(itemAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
            }
        });

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