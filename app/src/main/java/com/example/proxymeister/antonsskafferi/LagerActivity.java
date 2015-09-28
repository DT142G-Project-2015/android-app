package com.example.proxymeister.antonsskafferi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LagerActivity extends AppCompatActivity  {
    private List<String> lager_entry;
    private ListView lager_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lager);
        lager_list = (ListView)findViewById(R.id.lagerList);

        lager_entry = new ArrayList<>();
        {
            lager_entry.add("Renkött    |   6 (kg)    |    15 dagar ");
            lager_entry.add("Drakfilé    | 10 (gram) |  31708 dagar ");
            lager_entry.add("Revbenspjäll    |   17 (kg)    |   5 dagar  ");
        }


        /* used with URL-fetch
        for (Item p : entries) {
            strings.add(p.toString());
        }
        */

        // stores entries
        ArrayAdapter<String> entry_keeper =
                new ArrayAdapter<>(LagerActivity.this, android.R.layout.simple_list_item_1, lager_entry);

        // pass entries to the ListView
        lager_list.setAdapter(entry_keeper);
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