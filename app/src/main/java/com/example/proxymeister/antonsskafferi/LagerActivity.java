package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.proxymeister.antonsskafferi.model.Article;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LagerActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lager);


        //int id = getIntent().getIntExtra("article-id", 1); //PNU

        Call<List<Article>> call = Utils.getApi().getArticles();

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Response<List<Article>> response, Retrofit retrofit) {
                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                List<Article> articles = response.body();

                if (articles != null) {
                    // strings = items.map(_.toString())
                    List<String> strings = new ArrayList<String>();
                    for (Article p : articles) {
                        strings.add(p.toString());
                    }

                    // create simple ArrayAdapter to hold the strings for the ListView
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<String>(LagerActivity.this, android.R.layout.simple_list_item_1, strings);

                    // pass the adapter to the ListView
                   ListView list = (ListView) findViewById(R.id.lagerList);
                    list.setAdapter(itemsAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
            }
        });
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