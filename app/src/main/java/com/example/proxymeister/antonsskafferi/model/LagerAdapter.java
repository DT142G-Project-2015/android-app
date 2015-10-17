package com.example.proxymeister.antonsskafferi.model;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.LagerActivity;
import com.example.proxymeister.antonsskafferi.R;

import java.util.ArrayList;

public class LagerAdapter extends RecyclerView.Adapter<LagerAdapter.LagerViewHolder> {
    private ArrayList<String> myLagerDataset;
    private OnItemClickListener mItemClickListener;
    private int selectedPos = -1;

    public class LagerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView lagerTextView;
        public LagerViewHolder(View lagerView) {
            super(lagerView);
            lagerView.setOnClickListener(this);
            lagerTextView = (TextView) lagerView.findViewById(R.id.recycler_lager_title);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
            {
                mItemClickListener.onItemClick(v, getLayoutPosition());
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
            }
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemCLickListener)
    {
        this.mItemClickListener = mItemCLickListener;
    }

    @Override
    public LagerViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        View lagerView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_lager_rv, parent, false);
        LagerViewHolder lViewHolder = new LagerViewHolder(lagerView);
        return lViewHolder;
    }

    @Override
    public void onBindViewHolder(LagerViewHolder lagerholder, int position) {
        lagerholder.lagerTextView.setText(myLagerDataset.get(position));
        if (selectedPos == position) {
            lagerholder.lagerTextView.setBackgroundColor(Color.BLACK);
        } else {
            lagerholder.lagerTextView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public LagerAdapter(ArrayList<String> lagerDataset)
    {
        myLagerDataset = lagerDataset;
    }

    @Override
    public int getItemCount()
    {
        return myLagerDataset.size();
    }
}