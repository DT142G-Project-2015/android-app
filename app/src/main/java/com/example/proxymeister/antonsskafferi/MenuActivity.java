package com.example.proxymeister.antonsskafferi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.Item;
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

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        if (getIntent().getAction().equals(EDIT_MENU)) {
            editMenu = true;
            menuId = getIntent().getIntExtra("menu-id", 0);
        }

        if (editMenu) {
            ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.onDeleteRow(viewHolder.getAdapterPosition());
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
                        setTitle("Redigera " + menu.getMenuTypeString() + "meny");
                        adapter = new MenuAdapter(MenuActivity.this, menu, editMenu, MenuActivity.this);;
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
                        setTitle(menu.getMenuTypeString() + "meny");
                        adapter = new MenuAdapter(MenuActivity.this, menu, editMenu, MenuActivity.this);
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

        if (id == R.id.action_add_menu_group) {


            final EditText edit = new EditText(this);
            edit.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES);
            edit.setHint("Namn");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Skapa kategori");
            builder.setView(edit);
            builder.setPositiveButton("Skapa kategori", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Menu.Group g = new Menu.Group(edit.getText().toString());

                    Utils.getApi(MenuActivity.this).createMenuGroup(g, menuId).enqueue(new Callback<Group>() {
                        public void onResponse(Response<Group> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                refreshData();
                            }
                        }

                        public void onFailure(Throwable t) {

                        }
                    });
                }
            });
            builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
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

    @Override
    public void onAddItemClick(Menu.Group group) {
        Intent i = ItemActivity.getPickItemIntent(this);
        i.putExtra("group-id", group.id);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {

        if (resultCode == RESULT_OK) {
            final Menu.Item item = (Menu.Item)i.getSerializableExtra("picked-item");
            final int groupId = i.getIntExtra("group-id", -1);

            Utils.getApi(this).createMenuGroupItem(item, menuId, groupId).enqueue(new Callback<Group>() {
                public void onResponse(Response<Group> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        adapter.addItem(groupId, item);
                    } else
                        Toast.makeText(MenuActivity.this, "Kunde inte lägga till maträtt", Toast.LENGTH_SHORT).show();

                }
                public void onFailure(Throwable t) {
                    Toast.makeText(MenuActivity.this, "Ingen anslutning. Försök igen.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
