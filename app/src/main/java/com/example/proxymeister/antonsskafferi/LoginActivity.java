package com.example.proxymeister.antonsskafferi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void openMainActivity(View view)
    {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
