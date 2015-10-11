package com.example.proxymeister.antonsskafferi.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import com.example.proxymeister.antonsskafferi.R;

import java.util.ArrayList;
import java.util.List;

public class LagerAdapter extends RecyclerView.Adapter<LagerAdapter.LagerViewHolder> {
    private ArrayList<String> myLagerDataset;

    public static class LagerViewHolder extends RecyclerView.ViewHolder{
        public TextView lagerTextView;
        public LagerViewHolder(View lagerView) {
            super(lagerView);
            lagerTextView = (TextView) lagerView.findViewById(R.id.recycler_lager_title);
        }
    }

    // ok

    @Override
    public LagerViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        View lagerView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lager_recycler_view, parent, false);

        LagerViewHolder lViewHolder = new LagerViewHolder(lagerView);
        return lViewHolder;
    }

    @Override
    public void onBindViewHolder(LagerViewHolder lagerholder, int position) {
        lagerholder.lagerTextView.setText(myLagerDataset.get(position));
    }

    public LagerAdapter(ArrayList<String> lagerDataset) {
        myLagerDataset = lagerDataset;
    }

    public void addItem(String data, int index) {
        myLagerDataset.add(data);
        notifyItemInserted(index);
    }

    @Override
    public int getItemCount() {
        return myLagerDataset.size();
    }
}