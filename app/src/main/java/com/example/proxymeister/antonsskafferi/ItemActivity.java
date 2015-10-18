package com.example.proxymeister.antonsskafferi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ItemActivity extends AppCompatActivity {


    private static String PICK_ITEM = "antonsskafferi.PICK_ITEM";

    public static Intent getPickItemIntent(Context context) {
        Intent intent = new Intent(context, ItemActivity.class);
        intent.setAction(PICK_ITEM);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);


    }

}
