package com.example.proxymeister.antonsskafferi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.LinearLayout.LayoutParams;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.example.proxymeister.antonsskafferi.model.Article;
import com.example.proxymeister.antonsskafferi.model.LagerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LagerActivity extends AppCompatActivity  {

    private LagerRecycler lr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lager);

        // create and set view
        lr = new LagerRecycler();
        lr.setView();
    }

    public class LagerRecycler
    {
        // stores clicked position
        private int pos;

        // stores entries from database
        private List<Article> articles;

        // stores entries in string
        private ArrayList<String> strings;

        // recyclerview
        private RecyclerView rv;

        // adapter
        private LagerAdapter lAdapter;

        public LagerRecycler()
        {
            pos = -1;

            // get reference to recyclerView
            rv = (RecyclerView) findViewById(R.id.lager_recycler_view);

            // sets layout for recyclerviewer
            rv.setLayoutManager(new LinearLayoutManager(LagerActivity.this));

            // used to store data for recyclerview
            strings = new ArrayList<>();
        }

        // sets recyclerview
        public void setView()
        {
            Call<List<Article>> call = Utils.getApi(LagerActivity.this).getArticles();
            call.enqueue(new Callback<List<Article>>()
            {
                @Override
                public void onResponse(Response<List<Article>> response, Retrofit retrofit)
                {
                    int statusCode = response.code();
                    Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                    articles = response.body();

                    if (!articles.equals(null))
                    {
                        for (Article p : articles)
                        {
                            strings.add(p.toString());
                        }
                    }
                    else
                    {
                        strings.add("Inga varor hittades ...");
                    }

                    lAdapter = new LagerAdapter(strings);

                    lAdapter.SetOnItemClickListener(new LagerAdapter.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(View view, int position)
                        {
                            pos = position;
                        }
                    });

                    rv.setItemAnimator(new DefaultItemAnimator());
                    rv.setAdapter(lAdapter);
                }

                @Override
                public void onFailure(Throwable t)
                {
                    Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());

                    strings.add("Kunde inte nå databasen... ");
                    lAdapter = new LagerAdapter(strings);
                    rv.setAdapter(lAdapter);
                }
            });
        }

        // refresh content of recylcerview
        public void refreshView()
        {
            Call<List<Article>> call = Utils.getApi(LagerActivity.this).getArticles();
            call.enqueue(new Callback<List<Article>>()
            {
                @Override
                public void onResponse(Response<List<Article>> response, Retrofit retrofit)
                {
                    int statusCode = response.code();
                    Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                    articles = response.body();

                    strings.clear();

                    if (!articles.equals(null))
                    {
                        for (Article p : articles)
                        {
                            strings.add(p.toString());
                        }
                    }
                    else
                    {
                        strings.add("Inga varor hittades ...");
                    }

                    lAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Throwable t)
                {
                    Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                }
            });
        }
    }

    public class HandleLager
    {
        private int deleteID;
        private Article body = new Article();
        private Article retrievedArticle = new Article();
        private List<Article> articles = lr.articles;

        public void deleteArticleDialog()
        {
            // if nothing is selected
            if(lr.pos == -1)
            {
                createToaster("Välj en vara");
                return;
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(LagerActivity.this);
            builder.setTitle("");
            builder.setMessage("Är du säker på att du vill ta bort denna vara?");

            builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        deleteID = articles.get(lr.pos).id;

                        deleteArticle();
                        dialog.dismiss();
                        createToaster("Vara borttagen");
                        lr.refreshView();
                    } catch (Throwable t) {
                        dialog.dismiss();
                        String s = t.getMessage();
                        createToaster(s);
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void createArticleDialog()
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LagerActivity.this);

            builder.setTitle("   Fyll i värden för varan");

            // defines layout
            LinearLayout ll = new LinearLayout(LagerActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation

            // spinner for category
            String[] s = { "Dryck", "Grönsak", "Råvara", "Färskvara", "Tillbehör" };

            final ArrayAdapter<String> adp = new ArrayAdapter<>(LagerActivity.this,
                    R.layout.spinner_item, s);

            final Spinner sp = new Spinner(LagerActivity.this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            sp.setAdapter(adp);

            // input objects
            final EditText name = new EditText(LagerActivity.this);
            final EditText amount = new EditText(LagerActivity.this);
            final EditText unit = new EditText(LagerActivity.this);
            final EditText category = new EditText(LagerActivity.this);
            final EditText date = new EditText(LagerActivity.this);

            // constraints for input objects
            name.setInputType(InputType.TYPE_CLASS_TEXT);
            amount.setInputType(InputType.TYPE_CLASS_NUMBER);
            unit.setInputType(InputType.TYPE_CLASS_TEXT);
            category.setInputType(InputType.TYPE_CLASS_TEXT);
            date.setInputType(InputType.TYPE_CLASS_DATETIME);

            // title per input object
            final TextView name_title = new TextView(LagerActivity.this);
            name_title.setText("  Namn  (*)");
            final TextView unit_title = new TextView(LagerActivity.this);
            unit_title.setText("  Enhet  (*)");
            final TextView amount_title = new TextView(LagerActivity.this);
            amount_title.setText("  Mängd  (*)");
            final TextView date_title = new TextView(LagerActivity.this);
            date_title.setText("  Utgångsdatum (YYYY/MM/DD, YYYY-MM-DD)");
            final TextView category_title = new TextView(LagerActivity.this);
            category_title.setText("  Kategori  (*)");

            // insert to layout
            ll.addView(new TextView(LagerActivity.this));
            ll.addView(name_title);
            ll.addView(name);
            ll.addView(unit_title);
            ll.addView(unit);
            ll.addView(amount_title);
            ll.addView(amount);
            ll.addView(date_title);
            ll.addView(date);
            ll.addView(category_title);
            ll.addView(sp);

            // set view for dialog
            builder.setView(ll);

            builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("Genmför", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    try {
                        body.name = name.getText().toString();
                        if (body.name.equals("")) throw new Throwable("Ange namn");

                        body.unit = unit.getText().toString();
                        if (body.unit.equals("")) throw new Throwable("Ange enhet");

                        if (amount.getText().toString().equals(""))
                            throw new Throwable("Ange mängd");
                        body.amount = Integer.parseInt(amount.getText().toString());

                        body.exp_date = date.getText().toString();
                        if (!(isValidDate(body.exp_date)) && !(body.exp_date.equals("")))
                            throw new Throwable("Fel format på utgångsdatum");

                        body.category = sp.getSelectedItem().toString();

                        createArticle();
                        dialog.dismiss();
                        createToaster("Vara skapad");
                        lr.refreshView();

                    } catch (Throwable t) {
                        dialog.dismiss();
                        String s = t.getMessage();
                        createToaster(s);
                        createArticleDialog();
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void changeArticleDialog()
        {
            // if nothing is selected
            if(lr.pos == -1)
            {
                createToaster("Välj en vara");
                return;
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(LagerActivity.this);

            builder.setTitle("Fyll i värden som ska ändras");

            // defines layout
            LinearLayout ll = new LinearLayout(LagerActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation


            // spinner for category
            String[] s = { "Dryck", "Grönsak", "Råvara", "Färskvara", "Tillbehör" };

            final ArrayAdapter<String> adp = new ArrayAdapter<>(LagerActivity.this,
                    R.layout.spinner_item, s);

            final Spinner sp = new Spinner(LagerActivity.this);
            sp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            sp.setAdapter(adp);


            // input objects
            final EditText name = new EditText(LagerActivity.this);
            final EditText amount = new EditText(LagerActivity.this);
            final EditText unit = new EditText(LagerActivity.this);
            final EditText category = new EditText(LagerActivity.this);
            final EditText date = new EditText(LagerActivity.this);

            // constraints for input objects
            name.setInputType(InputType.TYPE_CLASS_TEXT);
            amount.setInputType(InputType.TYPE_CLASS_NUMBER);
            unit.setInputType(InputType.TYPE_CLASS_TEXT);
            category.setInputType(InputType.TYPE_CLASS_TEXT);
            date.setInputType(InputType.TYPE_CLASS_DATETIME);

            // title per input object
            final TextView name_title = new TextView(LagerActivity.this);
            name_title.setText("  Namn");
            final TextView unit_title = new TextView(LagerActivity.this);
            unit_title.setText("  Enhet");
            final TextView amount_title = new TextView(LagerActivity.this);
            amount_title.setText("  Mängd");
            final TextView date_title = new TextView(LagerActivity.this);
            date_title.setText("  Utgångsdatum (YYYY/MM/DD, YYYY-MM-DD)");
            final TextView category_title = new TextView(LagerActivity.this);
            category_title.setText("  Kategori");

            // insert to layout
            ll.addView(new TextView(LagerActivity.this));
            ll.addView(name_title);
            ll.addView(name);
            ll.addView(unit_title);
            ll.addView(unit);
            ll.addView(amount_title);
            ll.addView(amount);
            ll.addView(date_title);
            ll.addView(date);
            ll.addView(category_title);
            ll.addView(sp);

            // set view for dialog
            builder.setView(ll);

            builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Genmför", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        retrievedArticle = articles.get(lr.pos);
                        body.id = retrievedArticle.id;

                        body.name = name.getText().toString();
                        if (body.name.equals("")) body.name = retrievedArticle.name;

                        body.unit = unit.getText().toString();
                        if (body.unit.equals("")) body.unit = retrievedArticle.unit;

                        if (amount.getText().toString().equals("")) {
                            body.amount = retrievedArticle.amount;
                        } else {
                            body.amount = Integer.parseInt(amount.getText().toString());
                        }

                        if (date.getText().toString().equals("")) {
                            body.exp_date = retrievedArticle.exp_date;
                        } else {
                            body.exp_date = date.getText().toString();
                            if (!(isValidDate(body.exp_date)) && !(body.exp_date.equals("")))
                                throw new Throwable("Fel format på utgångsdatum");
                        }

                        body.category = sp.getSelectedItem().toString();

                        System.out.println(body.category);

                        changeArticle();
                        dialog.dismiss();
                        createToaster("Ändring genomförd");
                        lr.refreshView();

                    } catch (Throwable t) {
                        dialog.dismiss();
                        String s = t.getMessage();
                        createToaster(s);
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void deleteArticle() {

            Call<Void> call = Utils.getApi(LagerActivity.this).deleteArticle(deleteID);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    Log.i("DELETE", "Success");
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i(LagerActivity.class.getName(), "Failed to delete data " + t.getMessage());
                }
            });
        }

        public void createArticle() {

            Article newArticle = new Article(body.name, body.category, body.amount, body.unit, body.exp_date);
            Call<Void> call = Utils.getApi(LagerActivity.this).createArticle(newArticle);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    Log.i("CREATE", response.message());
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i("CREATE, FAIL", t.getMessage());
                }
            });
        }

        public void changeArticle()
        {
            Article newArticle = new Article(body.name, body.category, body.amount, body.unit, body.exp_date);
            Call<Void> call = Utils.getApi(LagerActivity.this).changeArticle(body.id, newArticle);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    Log.i("CHANGE", "Success");
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i("CHANGE", t.getMessage());
                }
            });
        }

        // checks if string date format is valid
        public boolean isValidDate(String inDate) {
            boolean dashValid = true;
            boolean slashValid = true;
            SimpleDateFormat dateFormat_dash = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat_slash = new SimpleDateFormat("yyyy/MM/dd");
            dateFormat_dash.setLenient(false);
            dateFormat_slash.setLenient(false);
            try {
                dateFormat_dash.parse(inDate.trim());
            } catch (ParseException pe) {
                dashValid = false;
            }
            try {
                dateFormat_slash.parse(inDate.trim());
            } catch (ParseException pe) {
                slashValid = false;
            }

            if(slashValid || dashValid)
            {
                return true;
            }
            else
            {
                return false;
            }

        }

        // check if id exists
        public boolean idDoesNotExist() {
            for (int i = 0; i < articles.size(); i++) {
                if (articles.get(i).id == deleteID) {
                    return false;
                }
            }
            return true;
        }

        // create toaster with input string
        public void createToaster(String s)
        {
            Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
            toast.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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

        HandleLager hl = new HandleLager();

        switch(id)
        {
            case R.id.lagethantering_refresh: lr.refreshView();
                break;
            case R.id.lagethantering_change: hl.changeArticleDialog();
                break;
            case R.id.lagethantering_remove: hl.deleteArticleDialog();
                break;
            case R.id.lagethantering_add: hl.createArticleDialog();
        }
        return super.onOptionsItemSelected(item);
    }
}