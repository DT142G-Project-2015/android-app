package com.example.proxymeister.antonsskafferi.model;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.MainActivity;
import com.example.proxymeister.antonsskafferi.NoteDialogHandler;
import com.example.proxymeister.antonsskafferi.OrderActivity;
import com.example.proxymeister.antonsskafferi.OrderMealActivity;
import com.example.proxymeister.antonsskafferi.R;
import com.example.proxymeister.antonsskafferi.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ItemAdapter extends ArrayAdapter<ItemHolder> {
    private final Callback callback;
    private Item itemclicked;
    public boolean isClicked = false;
    public List<Item> temporder = new ArrayList<>();
    public int orderID;
    public int groupID;
    public int itemID;
    private boolean isItem;
    public ItemAdapter(Context context, List<Item> items, int orderId, int groupId, int pos, Callback callback) {
        super(context, 0);
        this.callback = callback;
        orderID = orderId;
        groupID = groupId;
        for (Item i : items) {
            add(new ItemHolder(i));
        }
        isItem = true;
    }

    public ItemAdapter(Context context, List<Item> items, int orderId, int groupId, int itemId, int pos, Callback callback) {
        super(context, 0);
        this.callback = callback;
        orderID = orderId;
        groupID = groupId;
        itemID = itemId;
        for (Item i : items) {
            add(new ItemHolder(i));
        }
        isItem = false;
    }

    public interface Callback {
        void itemAdded();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;


        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_order_lunch_list_view, null);
        }

        TextView name = (TextView) v.findViewById(R.id.name);
        TextView price = (TextView) v.findViewById(R.id.price);
        TextView description = (TextView) v.findViewById(R.id.description);

        ItemHolder holder = getItem(position);
        v.setTag(holder);


        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemHolder holder = getItem(position);

                if(isItem)
                {

                    final Call<Void> call = Utils.getApi(getContext()).addItem(holder.item, orderID, groupID);
                    call.enqueue(new retrofit.Callback<Void>() {
                        @Override
                        public void onResponse(Response<Void> response, Retrofit retrofit) {
                            if (callback != null)
                                callback.itemAdded();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                        }
                    });

                }
                else
                {
                    final Call<Void> call = Utils.getApi(getContext()).addSubItem(holder.item, orderID, groupID, itemID);
                    call.enqueue(new retrofit.Callback<Void>() {
                        @Override
                        public void onResponse(Response<Void> response, Retrofit retrofit) {
                            if (callback != null)
                                callback.itemAdded();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                        }
                    });
                }


            }
        });


        name.setText(holder.item.name);
        price.setText(Double.toString(holder.item.price) + ":-");
        description.setText(holder.item.description);

/*
        if(itemclicked != null)
            if(itemclicked.type == 2)
            {
                NoteDialogHandler handler = new NoteDialogHandler(holder.item, groupID, orderID, ItemAdapter.this, new NoteDialogHandler.Callback() {
                    @Override
                    public void onDone() {
                        getAllOrders(i);
                    }
                });
            }
*/


        return v;
    }


    boolean getIsClicked()
    {
        return isClicked;
    }
}