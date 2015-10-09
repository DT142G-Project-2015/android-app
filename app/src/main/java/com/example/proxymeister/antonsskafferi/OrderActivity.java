package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private List<Order> orders;
    private List<Group> groups = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter<CustomViewHolder> mAdapter;

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

        Call<List<Order>> call = Utils.getApi().getOrdersByStatus("readyForKitchen");

        call.enqueue(new Callback<List<Order>>() {
                         @Override
                         public void onResponse(Response<List<Order>> response, Retrofit retrofit) {

                             int statusCode = response.code();
                             Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                             orders = response.body();

                             if (orders != null) {
                                 for (int i = 0; i < orders.size(); i++) {
                                     List<Group> temp = orders.get(i).groups;
                                     for (int j = 0; j < temp.size(); j++) {
                                         groups.add(temp.get(j));

                                     }
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

    }
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public List<Item> items = new ArrayList<>();
        private TextView mOrderTextView;
        private TextView mTotPriceTextView;
        public LinearLayout ll;
        public Button mAddItemButton;
        public boolean expanded = false;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mOrderTextView = (TextView) itemView.findViewById(R.id.order);
            mTotPriceTextView = (TextView) itemView.findViewById(R.id.totalPrice);
            mAddItemButton = (Button) itemView.findViewById(R.id.addItemToOrder);
            ll = (LinearLayout) itemView.findViewById(R.id.item_holder);
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
            public void onBindViewHolder(final CustomViewHolder viewHolder, int i) {
                viewHolder.mOrderTextView.setText("Bord:" + orders.get(i).id);
                orders.get(i).getTotalPrice();

                LayoutInflater inflater = (LayoutInflater) getSystemService(OrderActivity.LAYOUT_INFLATER_SERVICE);
                final LinearLayout parent = (LinearLayout) viewHolder.ll;

                for (Item it : groups.get(i).items) {
                    View custom = inflater.inflate(R.layout.activity_item_view, null);
                    TextView tv = (TextView) custom.findViewById(R.id.item);
                    tv.setText(it.name + ", " + it.price + ":-");
                    parent.addView(custom);
                }

                viewHolder.mTotPriceTextView.setText("Totalt pris: " + Double.toString(orders.get(i).totPrice) + ":-");
                /*for (Item it : groups.get(i).items) {
                    viewHolder.mItemTextView.append("\n" + "   " + it.name);
                }*/

                //EXPANDING CODE
                OnClickListener oclbtn = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!viewHolder.expanded) {
                            parent.setVisibility(View.VISIBLE);
                            viewHolder.mTotPriceTextView.setVisibility(View.VISIBLE);
                            viewHolder.mOrderTextView.setPadding(20, 20, 20, 5);
                            viewHolder.mAddItemButton.setVisibility(View.VISIBLE);
                            viewHolder.expanded = true;

                        }else{
                            parent.setVisibility(View.GONE);
                            viewHolder.mTotPriceTextView.setVisibility(View.GONE);
                            viewHolder.mOrderTextView.setPadding(20, 20, 20, 20);
                            viewHolder.mAddItemButton.setVisibility(View.GONE);
                            viewHolder.expanded = false;

                        }
                    }
                };

                viewHolder.mOrderTextView.setOnClickListener(oclbtn);
                //END EXPAND


                viewHolder.mOrderTextView.setPressed(true);
            }

            @Override
            public int getItemCount() {
                return orders.size();
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

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
