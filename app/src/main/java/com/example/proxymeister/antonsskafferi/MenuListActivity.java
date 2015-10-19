package com.example.proxymeister.antonsskafferi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Menu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MenuListActivity extends AppCompatActivity {


    class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {

        public List<Menu> menus = new ArrayList<>();

        private Integer selectedRow;


        public Integer getSelectedRow() {
            return selectedRow;
        }

        public void setSelectedRow(Integer selectedRow) {

            if (this.selectedRow != null)
                notifyItemChanged(this.selectedRow);
            if (selectedRow != null)
                notifyItemChanged(selectedRow);

            this.selectedRow = selectedRow;
        }




        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView text1;
            TextView text2;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = (TextView)itemView.findViewById(R.id.name);
                text2 = (TextView)itemView.findViewById(R.id.description);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.activity_menu_list_rv_menu, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            final Menu menu = menus.get(position);
            vh.text1.setText(menu.getMenuTypeString());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            vh.text2.setText("Aktiv f.o.m " + sdf.format(menu.start_date) + " t.o.m " + sdf.format(menu.stop_date));
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = MenuActivity.getEditMenuIntent(MenuListActivity.this, menu.id);
                    startActivity(i);
                }
            });

            if (Integer.valueOf(position).equals(selectedRow))
                vh.itemView.setBackgroundColor(Color.BLACK);
            else
                vh.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        @Override
        public int getItemCount() {
            return menus.size();
        }

        public void setData(List<Menu> data) {
            menus = data;
            notifyDataSetChanged();
        }
    }


    private MenuListAdapter adapter;
    private RecyclerView rv;
    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter = new MenuListAdapter();
        rv.setAdapter(adapter);
        setTitle("Menyredigeraren");


        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                if (actionMode != null) {
                    actionMode.finish();
                }
                View view = rv.findChildViewUnder(e.getX(), e.getY());
                adapter.setSelectedRow(rv.getChildAdapterPosition(view));

                actionMode = startActionMode(new ActionMode.Callback() {
                    public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.menu_menu_list_selected, menu);
                        return true;
                    }
                    public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
                        return false;
                    }
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        if (item.getItemId() == R.id.action_remove_menu) {
                            deleteMenu();
                            return true;
                        } else if (item.getItemId() == R.id.action_edit_menu) {
                            openUpdateMenuDialog();
                            return true;
                        }
                        return false;
                    }
                    public void onDestroyActionMode(ActionMode mode) {
                        actionMode = null;
                        adapter.setSelectedRow(null);
                    }
                });
                super.onLongPress(e);
            }
        });

        rv.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
        });



        refreshData();
    }

    private void deleteMenu() {
        final Integer row = adapter.getSelectedRow();
        if (row != null) {
            int id = adapter.menus.get(row).id;

            Utils.getApi(this).deleteMenu(id).enqueue(new Callback<Void>() {
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        adapter.menus.remove((int)row);
                        adapter.notifyItemRemoved(row);
                        adapter.setSelectedRow(null);
                        actionMode.finish();
                    }
                }
                public void onFailure(Throwable t) {}
            });
        }
    }

    private void refreshData() {

        Utils.getApi(this).getMenus().enqueue(new Callback<List<Menu>>() {
            public void onResponse(Response<List<Menu>> response, Retrofit retrofit) {

                List<Menu> menus = response.body();

                if (menus != null) {
                    adapter.setData(menus);
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
        getMenuInflater().inflate(R.menu.menu_menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_menu) {
            new MenuDialog(this, null, new MenuDialog.Callback() {
                public void onResult(Menu menu, DialogInterface dialog) {
                    addMenu(menu, dialog);
                }
            }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMenu(Menu menu, final DialogInterface dialog) {

        Utils.getApi(this).createMenu(menu).enqueue(new Callback<Void>() {
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    refreshData();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MenuListActivity.this, "Kunde inte skapa menyn", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Throwable t) {
                Toast.makeText(MenuListActivity.this, "Ingen anslutning", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openUpdateMenuDialog() {

        final Integer row = adapter.getSelectedRow();
        if (row != null) {
            Menu selectedMenu = adapter.menus.get(row);

            new MenuDialog(MenuListActivity.this, selectedMenu, new MenuDialog.Callback() {
                public void onResult(Menu menu, final DialogInterface dialog) {
                    updateMenu(menu, dialog);
                }
            }).show();
        }
    }

    private void updateMenu(Menu menu, final DialogInterface dialog) {

        Utils.getApi(MenuListActivity.this).updateMenu(menu.id, menu).enqueue(new Callback<Void>() {
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    refreshData();
                    dialog.dismiss();
                    adapter.setSelectedRow(null);
                    actionMode.finish();
                } else {
                    Toast.makeText(MenuListActivity.this, "Kunde inte uppdatera menyn", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Throwable t) {
                Toast.makeText(MenuListActivity.this, "Ingen anslutning", Toast.LENGTH_SHORT).show();
            }
        });
    }

}