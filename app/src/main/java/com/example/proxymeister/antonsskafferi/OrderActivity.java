package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.model.DividerItemDecoration;
import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OrderActivity extends AppCompatActivity{
    //______________TILLFÄLLIGT______________________
    private List<Order> orders;
    private List<Group> groups = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter<CustomViewHolder> mAdapter;
    //________________END_________________________________

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);


        Button btn = (Button) findViewById(R.id.place_new_order_button);
        OnClickListener oclbtn = new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(OrderActivity.this, OrderListActivity.class);
                startActivity(intent);*/

                Intent intent = new Intent(OrderActivity.this, OrderMealActivity.class);
                intent.putExtra("menu-id", 1);
                startActivity(intent);
            }
        };

        btn.setOnClickListener(oclbtn);

        // Get all orders ready to serve
        Call<List<Order>> call = Utils.getApi().getOrdersByStatus(getString(R.string.StatusReadyToServe));

        call.enqueue(new Callback<List<Order>>() {
                         @Override
                         public void onResponse(Response<List<Order>> response, Retrofit retrofit) {

                             int statusCode = response.code();
                             Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                             orders = response.body();

                             if (orders != null) {
                                 for(Order o : orders){
                                     for(Group g : o.groups)
                                         groups.add(g);
                                 }
                                 mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
                                 mLayoutManager = new LinearLayoutManager(OrderActivity.this);
                                 mRecyclerView.setLayoutManager(mLayoutManager);
                                 mRecyclerView.addItemDecoration(new DividerItemDecoration(OrderActivity.this, DividerItemDecoration.VERTICAL_LIST));
                                 setOrderAdapter();
                             }
                         }

                         @Override
                         public void onFailure(Throwable t) {
                             Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                         }
                     }

        );
        //______________________________END____________________________________________

    }
    //________________________Tillfälligt___________________________________
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public List<Item> items = new ArrayList<>();
        private TextView mOrderTextView;
        private TextView mGroupTextView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mOrderTextView = (TextView) itemView.findViewById(R.id.order);
            mGroupTextView = (TextView) itemView.findViewById(R.id.group);
        }
    }

    void setOrderAdapter(){
        mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_order_item
                        , viewGroup, false);
                view.setBackgroundResource(android.R.drawable.list_selector_background);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CustomViewHolder viewHolder, int i) {
                viewHolder.mOrderTextView.setText("Bord:" + orders.get(i).id);
                viewHolder.mOrderTextView.setPressed(true);
            }

            @Override
            public int getItemCount() {
                return orders.size();
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    //____________________________END________________________________

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
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
