package com.example.proxymeister.antonsskafferi;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.model.DividerItemDecoration;
import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.ItemHolder;
import com.example.proxymeister.antonsskafferi.model.Order;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OrderActivity extends AppCompatActivity {

    private List<Order> orders;
    private int activePosition;
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
                startActivity(intent);

                Intent intent = new Intent(OrderActivity.this, OrderMealActivity.class);
                intent.putExtra("menu-id", 1);
                startActivity(intent);*/
                showOrderDialog();
            }
        };

        btn.setOnClickListener(oclbtn);
        getAllOrders(-1);

    }

    public void getAllOrders(final int pos){
    Call<List<Order>> call = Utils.getApi().getOrders();
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
                setOrderAdapter(pos);
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
        }
    });
}

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public List<Item> items = new ArrayList<>();
        private TextView mOrderTextView;
        private TextView mTotPriceTextView;
        public LinearLayout groupHolder;
        public Button mAddGroupButton;
        public boolean expanded = false;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mOrderTextView = (TextView) itemView.findViewById(R.id.order);
            mTotPriceTextView = (TextView) itemView.findViewById(R.id.totalPrice);
            mAddGroupButton = (Button) itemView.findViewById(R.id.addGroup);
            groupHolder = (LinearLayout) itemView.findViewById(R.id.group_holder);
        }
    }

    public void onScroll(int i) {
        mRecyclerView.scrollToPosition(i);
    }

    void setOrderAdapter(final int pos){
        activePosition = pos;
        mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_order_view
                        , viewGroup, false);
                view.setBackgroundResource(android.R.drawable.list_selector_background);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final CustomViewHolder viewHolder, final int i) {
                viewHolder.mOrderTextView.setText("Bord:" + orders.get(i).booth);
                double totPrice = orders.get(i).getTotalPrice();
                final int orderId = orders.get(i).getId();

                LayoutInflater inflater = (LayoutInflater) getSystemService(OrderActivity.LAYOUT_INFLATER_SERVICE);
                final LinearLayout groupHolder = (LinearLayout) viewHolder.groupHolder;
                groupHolder.removeAllViews();
                if(i == activePosition){
                    groupHolder.setVisibility(View.VISIBLE);
                    viewHolder.mTotPriceTextView.setVisibility(View.VISIBLE);
                    viewHolder.mOrderTextView.setPadding(20, 20, 20, 5);
                    viewHolder.mAddGroupButton.setVisibility(View.VISIBLE);
                    viewHolder.expanded = true;
                }

                for (final Group g : orders.get(i).groups) {
                    View groupView = inflater.inflate(R.layout.recyclerview_group_view, null);
                    final int groupID = g.id;

                    //SEND TO KITCHEN
                    Button mSendToKitchenButton = (Button) groupView.findViewById(R.id.sendToKitchen);
                    OnClickListener sendGroup = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            g.status="readyForKitchen";
                            Call<Void> call = Utils.getApi().changeStatus(g, orderId, groupID);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Response<Void> response, Retrofit retrofit) {
                                    System.out.println("working");
                                    g.status = "readyForKitchen";
                                    getAllOrders(i);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    System.out.println("not working");

                                }
                            });
                        }
                    };
                    mSendToKitchenButton.setOnClickListener(sendGroup);
                    //END SENDTOKITCHEN

                    // ADD ITEM TO ORDER
                    Button mAddItemButton = (Button) groupView.findViewById(R.id.addItemToGroup);
                    OnClickListener additem = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OrderActivity.this, OrderMealActivity.class);
                            intent.putExtra("order-id", orderId);
                            intent.putExtra("group-id", groupID);
                            startActivity(intent);
                        }
                    };
                    mAddItemButton.setOnClickListener(additem);
                    //END

                    if(g.getStatus().equals("readyForKitchen"))
                        groupView.setBackgroundColor(Color.parseColor("#FFC726"));
                    if(g.getStatus().equals("done")) // Denna ska inte synas senare.
                        groupView.setBackgroundColor(Color.parseColor("#609040"));
                    if(g.getStatus().equals("readyToServe")) {
                        groupView.setBackgroundColor(Color.parseColor("#609040"));
                        mAddItemButton.setVisibility(View.GONE);
                        Button mDoneButton = (Button) groupView.findViewById(R.id.done);
                        mDoneButton.setVisibility(View.VISIBLE);
                    }
                    if(g.getStatus().equals("initial")) {
                        groupView.setBackgroundColor(Color.WHITE);
                        mSendToKitchenButton.setVisibility(View.VISIBLE);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(0, 4, 0, 4);
                    groupView.setLayoutParams(lp);

                    final LinearLayout itemHolder = (LinearLayout) groupView.findViewById(R.id.item_holder);

                    for(final Item it : g.items) {
                        View itemView = inflater.inflate(R.layout.recyclerview_item_view, null);
                        TextView tv = (TextView) itemView.findViewById(R.id.item);
                        Button deletebtn = (Button) itemView.findViewById(R.id.itemRemoveId);
                        if(g.getStatus().equals("readyForKitchen")) {
                            itemView.setBackgroundColor(Color.parseColor("#FFC726"));
                            tv.setBackgroundColor(Color.parseColor("#FFC726"));
                        }
                        if(g.getStatus().equals("done")) {
                            itemView.setBackgroundColor(Color.parseColor("#609040"));
                            tv.setBackgroundColor(Color.parseColor("#609040"));
                        }
                        if(g.getStatus().equals("readyToServe")) {
                            itemView.setBackgroundColor(Color.parseColor("#609040"));
                            tv.setBackgroundColor(Color.parseColor("#609040"));
                        }
                        if(g.getStatus().equals("initial")){
                            itemView.setBackgroundColor(Color.WHITE);
                            tv.setBackgroundColor(Color.WHITE);
                            tv.setTextColor(Color.BLACK);
                        }
                        tv.setText(it.name + ", " + it.price + ":-");
                        itemHolder.addView(itemView);


                        OnClickListener deletebuttonListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                g.items.remove(it);

                                deleteItem(orderId, g.id, it.id);

                                mAdapter.notifyItemRemoved(i);
                                getAllOrders(i);




                            }
                        };
                        deletebtn.setOnClickListener(deletebuttonListener);

                    }
                    groupHolder.addView(groupView);

                }

                //ADD GROUP
                viewHolder.mAddGroupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Group gr = new Group();
                        gr.status = "initial";
                        final Call<Group> call = Utils.getApi().createOrderGroup(gr, orderId);
                        call.enqueue(new retrofit.Callback<Group>() {
                            @Override
                            public void onResponse(Response<Group> response, Retrofit retrofit) {
                                Log.i(MainActivity.class.getName(), "NICE");
                                getAllOrders(i);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                            }
                        });
                    }
                });
                //END ADDGROUP

                if( totPrice != 0 )
                    viewHolder.mTotPriceTextView.setText("Totalt pris: " + Double.toString(totPrice) + ":-");
                /*for (Item it : groups.get(i).items) {
                    viewHolder.mItemTextView.append("\n" + "   " + it.name);
                }*/

                //EXPANDING CODE
                OnClickListener oclbtn = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!viewHolder.expanded) {
                            groupHolder.setVisibility(View.VISIBLE);
                            viewHolder.mTotPriceTextView.setVisibility(View.VISIBLE);
                            viewHolder.mOrderTextView.setPadding(20, 20, 20, 5);
                            viewHolder.mAddGroupButton.setVisibility(View.VISIBLE);
                            viewHolder.expanded = true;

                        }else{
                            groupHolder.setVisibility(View.GONE);
                            viewHolder.mTotPriceTextView.setVisibility(View.GONE);
                            viewHolder.mOrderTextView.setPadding(20, 20, 20, 20);
                            viewHolder.mAddGroupButton.setVisibility(View.GONE);
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
        if(activePosition > 0)
            onScroll(activePosition);
        activePosition = -1;
    }

    public void showOrderDialog(){
        CharSequence tabels[] = new CharSequence[] {"Bord 1", "Bord 2", "Bord 3", "Bord 4", "Bord 5"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Välj ett bord");
        builder.setItems(tabels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int table) {
                Order o = new Order();
                Group g = new Group();
                o.booth = table + 1;
                o.groups = new ArrayList<>();
                g.items = new ArrayList<Item>();
                g.status = "initial";
                o.groups.add(g);
                Call<Void> call = Utils.getApi().createOrder(o);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        Log.i("idg", "Response succesfull");
                        getAllOrders(-1);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("idg", "MEGA FAIL");
                    }
                });
            }
        });
        builder.setPositiveButton("Avbryt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
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

    public void deleteItem(int orderId, int groupId, int itemId) {


        Call<Void> call = Utils.getApi().deleteItem(orderId, groupId, itemId);


        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                Log.i("DELETE", "Success");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(LagerActivity.class.getName(), "Failed to delete data " + t.getMessage());
            }
        });
    }

    }
