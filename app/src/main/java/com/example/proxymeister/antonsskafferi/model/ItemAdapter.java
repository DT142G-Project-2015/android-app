package com.example.proxymeister.antonsskafferi.model;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.R;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<ItemHolder> {


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