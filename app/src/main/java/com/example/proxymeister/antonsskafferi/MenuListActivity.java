package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.model.Menu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MenuListActivity extends AppCompatActivity {

    class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {

        List<Menu> menus = new ArrayList<>();

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
            vh.text1.setText(menu.type == 0 ? "Lunchmeny" : "Middagsmeny");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            vh.text2.setText("Aktiv f.o.m " + sdf.format(menu.start_date) + " t.o.m " + sdf.format(menu.stop_date));
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = MenuActivity.getEditMenuIntent(MenuListActivity.this, menu.id);
                    startActivity(i);
                }
            });
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
        refreshData();
    }

    private void refreshData() {

        Utils.getApi(this).getMenus().enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Response<List<Menu>> response, Retrofit retrofit) {

                List<Menu> menus = response.body();

                if (menus != null) {
                    adapter.setData(menus);
                }
            }

            @Override
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_menu) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}