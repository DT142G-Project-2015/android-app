package com.example.proxymeister.antonsskafferi.model;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<ItemHolder> {
    public List<Item> temporder = new ArrayList<>();

    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0);

        for (Item i : items) {
            add(new ItemHolder(i));
        }
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
        final TextView counter = (TextView) v.findViewById(R.id.counter);
        TextView description = (TextView) v.findViewById(R.id.description);
        Button plusItem = (Button) v.findViewById(R.id.plusButton);
        Button minusItem = (Button) v.findViewById(R.id.minusButton);

        ItemHolder holder = getItem(position);
        v.setTag(holder);

        plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemHolder holder = getItem(position);
                counter.setText(Integer.valueOf(++holder.counter).toString());

                temporder.add(holder.item);
            }
        });


        minusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemHolder holder = getItem(position);
                if(holder.counter > 0) {
                    counter.setText(Integer.valueOf(--holder.counter).toString());
                    temporder.remove(holder.item);
                }
            }
        });



        name.setText(holder.item.name);
        price.setText(holder.item.price.toString());
        counter.setText(Integer.valueOf(holder.counter).toString());
        description.setText(holder.item.description);


        return v;
    }
}