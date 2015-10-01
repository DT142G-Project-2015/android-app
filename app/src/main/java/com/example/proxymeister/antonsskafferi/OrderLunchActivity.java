package com.example.proxymeister.antonsskafferi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OrderLunchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<String> strings;
    private ListView list;
    private Button addOrderButton;
    List<Item> temporder = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        list = (ListView)findViewById(R.id.list);
        addOrderButton = (Button) findViewById(R.id.AddItem);


        addOrderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onAddOrderClick();
            }
        });




        int id = getIntent().getIntExtra("menu-id", 1);

        Call<List<Item>> call = Utils.getApi().getMenuItems(id);

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Response<List<Item>> response, Retrofit retrofit) {

                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                List<Item> items = response.body();

                if (items != null) {
                    ItemAdapter itemAdapter = new ItemAdapter(OrderLunchActivity.this, items);
                    list.setAdapter(itemAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
            }
        });

        // Item Click Listener for the list
        list.setOnItemClickListener(this);
    }

    public static class ItemHolder {
        public ItemHolder(Item item) {
            counter = 0;
            this.item = item;
        }

        public Item item;
        public int counter;
    }

    public static class ItemAdapter extends ArrayAdapter<ItemHolder>{


        public ItemAdapter(Context context, List<Item> items) {
            super(context, 0);

            for (Item i : items) {
                add(new ItemHolder(i));
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.activity_order_lunch_list_view, null);
            }

            TextView name = (TextView) v.findViewById(R.id.name);
            TextView price = (TextView) v.findViewById(R.id.price);
            TextView counter = (TextView) v.findViewById(R.id.counter);
            TextView description = (TextView) v.findViewById(R.id.description);

            ItemHolder holder = getItem(position);

            v.setTag(holder);

            name.setText(holder.item.name);
            price.setText(holder.item.price.toString());
            counter.setText(Integer.valueOf(holder.counter).toString());
            description.setText(holder.item.description);


            return v;
        }
    }

    public void onItemClick(AdapterView<?> parent, View container, int position, long id) {

        ItemHolder holder = (ItemHolder) container.getTag();
        TextView counter = (TextView) container.findViewById(R.id.counter);
        counter.setText(Integer.valueOf(++holder.counter).toString());

        temporder.add(holder.item);


        Toast.makeText(OrderLunchActivity.this,
                "Item in position " + position + " clicked",
                Toast.LENGTH_LONG).show();
    }

    public void onAddOrderClick(){
        Order o = new Order();
        Group g = new Group();

        g.items = temporder;

        o.groups = new ArrayList<>();
        g.status = "readyForKitchen";
        o.groups.add(g);
        Call<Void> call = Utils.getApi().createOrder(o);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                Log.i("idg", "Response succesfull");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("idg", "MEGA FAIL");
            }
        });

        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
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