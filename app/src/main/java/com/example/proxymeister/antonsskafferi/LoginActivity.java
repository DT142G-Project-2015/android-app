package com.example.proxymeister.antonsskafferi;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText user;
    EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText)findViewById(R.id.editTextUserName);
        pass = (EditText)findViewById(R.id.editTextPassword);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        user.setText(sp.getString("username", ""));
        pass.setText(sp.getString("password", ""));
    }

    public void openMainActivity(View view)
    {


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor edit = sp.edit();

        edit.putString("username", user.getText().toString());
        edit.putString("password", pass.getText().toString());
        edit.commit();

        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
