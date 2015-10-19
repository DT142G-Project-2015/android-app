
        package com.example.proxymeister.antonsskafferi;


        import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Staff;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity {

    EditText user;
    EditText pass;
    Button  Login;
    TextView tx1;
    int counter = 5;


    @Override
    protected void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        user.setText(sp.getString("username", ""));
        pass.setText(sp.getString("password", ""));
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText)findViewById(R.id.editTextUserName);
        pass = (EditText)findViewById(R.id.editTextPassword);
        Login=(Button)findViewById(R.id.button);
        tx1=(TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);






        //tx1=(TextView)findViewById(R.id.textView3);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("username", user.getText().toString());
                edit.putString("password", pass.getText().toString());
                edit.apply();

                Call<List<Staff>> call = Utils.getApi(LoginActivity.this).getStaffMembers();
                call.enqueue(new Callback<List<Staff>>() {
                    @Override
                    public void onResponse(Response<List<Staff>> response, Retrofit retrofit) {

                        if (response.body() != null) {


                            // LOGIN SUCCESSSFUL
                            // GÅ VIDARE!
                            Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);


                            List<Staff> staffs = response.body();

                            for (Staff staff : staffs) {
                                if (user.getText().toString().equals(staff.username)) {
                                    Toast.makeText(getApplicationContext(), "Welcome " +
                                            staff.first_name + " " + staff.last_name ,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                            tx1.setVisibility(View.VISIBLE);
                            tx1.setBackgroundColor(Color.RED);
                            counter--;
                            tx1.setText(Integer.toString(counter));

                            if (counter == 0) {
                                Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                Login.setEnabled(false);
                        }
                    }
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "Connection Problem", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        });


    }

}