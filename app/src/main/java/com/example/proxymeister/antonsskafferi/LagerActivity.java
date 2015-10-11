package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.proxymeister.antonsskafferi.model.Article;
import com.example.proxymeister.antonsskafferi.model.LagerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LagerActivity extends AppCompatActivity  {

    private RecyclerView rv;
    private RecyclerView.Adapter lAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private  ArrayList<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lager);

        // 1. get a reference to recyclerView
        rv = (RecyclerView) findViewById(R.id.lager_recycler_view);
        rv.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        strings = new ArrayList<>();

        Call<List<Article>> call = Utils.getApi().getArticles();
        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Response<List<Article>> response, Retrofit retrofit) {
                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                List<Article> articles = response.body();

                if (articles != null) {
                    for (Article p : articles) {
                        strings.add(p.toString());
                    }
                }
                else
                {
                    strings.add("Inga varor hittades ...");
                }
            }


            @Override
            public void onFailure(Throwable t) {
                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
            }
        });

        lAdapter = new LagerAdapter(strings);

        rv.setAdapter(lAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // lagerhanterings alternativ
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, HandleLagerActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}