package com.example.proxymeister.antonsskafferi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;

import com.example.proxymeister.antonsskafferi.model.Menu;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MenuActivity extends AppCompatActivity implements MenuAdapter.Callback {

    private MenuAdapter adapter;
    private RecyclerView rv;
    private boolean editMenu;
    private int menuId;

    private static String PICK_ITEM = "antonsskafferi.PICK_ITEM";
    private static String EDIT_MENU = "antonsskafferi.EDIT_MENU";

    public static Intent getPickItemIntent(Context context) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.setAction(PICK_ITEM);
        return intent;
    }

    public static Intent getEditMenuIntent(Context context, int menuId) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.setAction(EDIT_MENU);
        intent.putExtra("menu-id", menuId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        if (getIntent().getAction().equals(EDIT_MENU)) {
            editMenu = true;
            menuId = getIntent().getIntExtra("menu-id", 1);
        }

        if (editMenu) {
            ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition()); // restore
                }
            };
            ItemTouchHelper touchHelper = new ItemTouchHelper(touchCallback);
            touchHelper.attachToRecyclerView(rv);
        }

        setTitle("...");
        refreshData();
    }


    private void refreshData() {

        if (editMenu) {
            Call<Menu> call = Utils.getApi(this).getMenu(menuId);

            call.enqueue(new Callback<Menu>() {
                @Override
                public void onResponse(Response<Menu> response, Retrofit retrofit) {

                    Menu menu = response.body();

                    if (menu != null) {
                        setTitle("Redigera " + (menu.type == 0 ? "lunchmeny" : "middagsmeny"));
                        adapter = new MenuAdapter(menu, editMenu, MenuActivity.this);;
                        rv.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i(MenuActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                }
            });

        } else {

            Call<List<Menu>> call = Utils.getApi(this).getMenus();

            call.enqueue(new Callback<List<Menu>>() {
                @Override
                public void onResponse(Response<List<Menu>> response, Retrofit retrofit) {

                    List<Menu> menus = response.body();

                    if (menus != null) {
                        Menu menu = Menu.mergedMenuAtCurrentTime(menus);
                        setTitle(menu.type == 0 ? "Lunchmeny" : "Middagsmeny");
                        adapter = new MenuAdapter(menu, editMenu, MenuActivity.this);
                        rv.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i(MenuActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                }
            });


        }




    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return editMenu;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_menu_group) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPickItem(Menu.Item item) {
        Intent intent = getIntent();
        intent.putExtra("picked-item", item);
        setResult(RESULT_OK, intent);
        finish();
    }
}
