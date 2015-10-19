package com.example.proxymeister.antonsskafferi;


import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Menu;

import java.util.ArrayList;
import java.util.List;

import retrofit.Response;
import retrofit.Retrofit;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private Context context;
    private Menu menu;
    private boolean editMode;
    private Callback callback;

    private void failedToRemoveRow(int pos) {
        notifyItemChanged(pos);
        Toast.makeText(context, "Kunde inte ta bort", Toast.LENGTH_SHORT).show();
    }

    private interface Row {
        int getLayout();
        void bindViewHolder(ViewHolder vh);
        void delete();
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

            if (editMode) {
                vh.addItem.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        callback.onAddItemClick(group);
                    }
                });
            } else {
                vh.addItem.setVisibility(View.GONE);
            }
        }

        public void delete() {
            if (expanded)
                expand();


            final int pos = rows.indexOf(this);

            Utils.getApi(context).deleteMenuGroup(menu.id, group.id).enqueue(new retrofit.Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        rows.remove(pos);
                        notifyItemRemoved(pos);
                    } else
                        failedToRemoveRow(pos);
                }

                @Override
                public void onFailure(Throwable t) {
                    failedToRemoveRow(pos);
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
                    itemRow.parentGroup = group;
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
        Menu.Group parentGroup;
        Menu.Item item;

        public int getLayout() { return R.layout.activity_menu_rv_item; }

        public void bindViewHolder(ViewHolder vh) {
            vh.text1.setText(item.name);
            vh.text2.setText(item.description);
            vh.text3.setText(String.valueOf(item.price) + " kr");

            if (editMode) {
                vh.addItem.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vh.text3.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                vh.addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onPickItem(item);
                    }
                });
            }
        }

        public void delete() {

            final int pos = rows.indexOf(this);

            Utils.getApi(context).deleteMenuItem(menu.id, parentGroup.id, item.id).enqueue(new retrofit.Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        rows.remove(pos);
                        parentGroup.items.remove(item);
                        notifyItemRemoved(pos);
                    } else
                        failedToRemoveRow(pos);
                }

                @Override
                public void onFailure(Throwable t) {
                    failedToRemoveRow(pos);
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
        void onAddItemClick(Menu.Group group);
    }

    public MenuAdapter(Context context, Menu menu, boolean editMode, Callback callback) {
        this.context = context;
        this.menu = menu;
        this.editMode = editMode;
        this.callback = callback;

        for (Menu.Group g : menu.groups) {
            Group groupRow = new Group();
            groupRow.group = g;
            rows.add(groupRow);
        }

        // auto expand all groups
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

    public void addItem(int groupId, final Menu.Item item) {
        for (final Row r : rows) {
            if (r instanceof Group) {

                final Group groupRow = (Group) r;

                if (groupRow.group.id == groupId) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (groupRow.expanded)
                                groupRow.expand();

                            groupRow.group.items.add(item);

                            groupRow.expand();
                        }
                    });
                }
            }
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

    public void onDeleteRow(int position) {
        rows.get(position).delete();
    }
}