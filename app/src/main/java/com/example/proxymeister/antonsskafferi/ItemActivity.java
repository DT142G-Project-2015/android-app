package com.example.proxymeister.antonsskafferi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.model.IdHolder;
import com.example.proxymeister.antonsskafferi.model.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ItemActivity extends AppCompatActivity {

    private ItemAdapter adapter;
    private RecyclerView rv;
    private int groupId;

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        public List<Item> items = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView text1;
            TextView text2;
            TextView text3;
            Button addItem;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = (TextView)itemView.findViewById(R.id.name);
                text2 = (TextView)itemView.findViewById(R.id.description);
                text3 = (TextView)itemView.findViewById(R.id.price);
                addItem = (Button)itemView.findViewById(R.id.add_item);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.activity_menu_rv_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            final Item item = items.get(position);
            vh.text1.setText(item.name);
            vh.text2.setText(item.description);
            vh.text3.setText("" + item.price);
            vh.addItem.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sendResult(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setData(List<Item> data) {
            items = data;
            notifyDataSetChanged();
        }

        public void addItem(Item item) {
            items.add(item);
            notifyItemInserted(items.indexOf(item));
            rv.smoothScrollToPosition(items.indexOf(item));
        }
    }

    void sendResult(Item item) {
        Intent i = getIntent();
        i.putExtra("picked-item", item);
        setResult(RESULT_OK, i);
        finish();
    }

    private static String PICK_ITEM = "antonsskafferi.PICK_ITEM";

    public static Intent getPickItemIntent(Context context) {
        Intent intent = new Intent(context, ItemActivity.class);
        intent.setAction(PICK_ITEM);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        groupId =  getIntent().getIntExtra("group-id", -1);

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter = new ItemAdapter();
        rv.setAdapter(adapter);
        setTitle("Lägg till maträtt på menyn");
        refreshData();
    }

    private void refreshData() {

        Utils.getApi(this).getItems(groupId).enqueue(new Callback<List<Item>>() {
            public void onResponse(Response<List<Item>> response, Retrofit retrofit) {

                List<Item> items = response.body();

                if (items != null) {
                    adapter.setData(items);
                }
            }

            public void onFailure(Throwable t) {
                Log.i(MenuActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_add_item) {


            new ItemDialog(this, new ItemDialog.Callback() {
                @Override
                public void onResult(final Item item, DialogInterface dialog) {
                    Utils.getApi(ItemActivity.this).createItem(item).enqueue(new Callback<IdHolder>() {
                        public void onResponse(Response<IdHolder> response, Retrofit retrofit) {
                            if (response.body() != null) {
                                Item i = (Item)item.clone();
                                i.id = response.body().id;
                                adapter.addItem(i);
                            }
                        }
                        public void onFailure(Throwable t) {}
                    });
                }
            }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
