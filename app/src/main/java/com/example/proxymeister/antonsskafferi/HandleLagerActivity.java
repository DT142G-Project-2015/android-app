package com.example.proxymeister.antonsskafferi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.proxymeister.antonsskafferi.model.Article;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import android.util.Log;

import com.example.proxymeister.antonsskafferi.R;

public class HandleLagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_lager);

        alterDialog();

        Article testArticle = new Article(5, "Kycklingklubbor", "Kött", 700, "kg", "2020-01-01");
        Call<Void> call = Utils.getApi().createArticle(testArticle);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                Log.i("idg", "Response succesfull");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("idg", "Fail");
            }
        });
    }





    //~~~~~~~~~~~~~~Self-made functions~~~~~~~~~~~~~~~~~~~~/

    private String smt = "";

    public void deleteDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HandleLagerActivity.this);
        builder.setTitle("");
        builder.setMessage("Mata in ID för varan du vill ta bort.");


        //input object
        final EditText input = new EditText(this);

        // constraint for input
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        // put input into dialog
        builder.setView(input);


        builder.setPositiveButton("Genmför", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                smt = input.getText().toString();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void alterDialog()
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HandleLagerActivity.this);
        builder.setTitle("   Mata in värden för varan");

        // defines layout
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation

        // input objects
        final EditText name = new EditText(this);
        final EditText amount = new EditText(this);
        final EditText unit = new EditText(this);
        final EditText category = new EditText(this);
        final EditText date = new EditText(this);

        // constraints for input objects
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        unit.setInputType(InputType.TYPE_CLASS_TEXT);
        category.setInputType(InputType.TYPE_CLASS_TEXT);
        date.setInputType(InputType.TYPE_CLASS_DATETIME);

        // title per input object
        final TextView name_title = new TextView(this);
        name_title.setText("  Namn");
        final TextView unit_title = new TextView(this);
        unit_title.setText("  Enhet");
        final TextView amount_title = new TextView(this);
        amount_title.setText("  Mängd");
        final TextView date_title = new TextView(this);
        date_title.setText("  Utgångsdatum (YYYY/MM/DD)");
        final TextView category_title = new TextView(this);
        category_title.setText("  Kategori");

        // insert to layout
        ll.addView(new TextView(this));
        ll.addView(name_title);
        ll.addView(name);
        ll.addView(unit_title);
        ll.addView(unit);
        ll.addView(amount_title);
        ll.addView(amount);
        ll.addView(date_title);
        ll.addView(date);
        ll.addView(category_title);
        ll.addView(category);

        // set view for dialog
        builder.setView(ll);

        builder.setPositiveButton("Genmför", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handle_lager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
