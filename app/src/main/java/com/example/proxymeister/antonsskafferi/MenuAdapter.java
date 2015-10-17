package com.example.proxymeister.antonsskafferi;


import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.model.Menu;

import java.util.ArrayList;
import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private boolean editMenu;
    private Callback callback;

    private interface Row {
        int getLayout();
        void bindViewHolder(ViewHolder vh);
    }

    private class Group implements Row {
        Menu.Group group;
        boolean expanded;

        public int getLayout() { return R.layout.activity_menu_rv_group; }

        public void bindViewHolder(ViewHolder vh) {
            vh.text1.setText(group.name);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expand();
                }
            });
        }

        public void expand() {
            expanded = !expanded;

            int afterThis = rows.indexOf(this) + 1;

            if (expanded) {

                int pos = afterThis;

                for (Menu.Item i : group.items) {
                    Item itemRow = new Item();
                    itemRow.item = i;
                    rows.add(pos++, itemRow);
                }
                notifyItemRangeInserted(afterThis, group.items.size());

            } else {

                for (int i = afterThis; i < afterThis + group.items.size(); i++) {
                    rows.remove(afterThis);
                }
                notifyItemRangeRemoved(afterThis, group.items.size());
            }
        }
    }

    private class Item implements Row {
        Menu.Item item;

        public int getLayout() { return R.layout.activity_menu_rv_item; }

        public void bindViewHolder(ViewHolder vh) {
            vh.text1.setText(item.name);
            vh.text2.setText(item.description);
            vh.text3.setText(String.valueOf(item.price) + " kr");
            vh.addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null)
                        callback.onPickItem(item);
                }
            });
        }
    }


    private final List<Row> rows = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

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

    public interface Callback {
        void onPickItem(Menu.Item item);
    }

    public MenuAdapter(Menu menu, boolean editMenu, Callback callback) {
        this.editMenu = editMenu;
        this.callback = callback;

        for (Menu.Group g : menu.groups) {
            Group groupRow = new Group();
            groupRow.group = g;
            rows.add(groupRow);
        }

        if (!editMenu) {
            new Handler().post(new Runnable() {
                @Override public void run() {
                    for (final Row r : rows) {
                        if (r instanceof Group)
                            new Handler().post(new Runnable() {
                                @Override public void run() {
                                    ((Group) r).expand();
                                }
                            });
                    }
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).getLayout();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(viewType, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        rows.get(position).bindViewHolder(vh);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }
}