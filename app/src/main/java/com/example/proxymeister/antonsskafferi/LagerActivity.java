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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private RecyclerView rv;
    private LagerAdapter lAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> strings;
    private List<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lager);

        // get a reference to recyclerView
        rv = (RecyclerView) findViewById(R.id.lager_recycler_view);

        // sets layout for recyclerviewer
        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        // used to store data for recyclerview
        strings = new ArrayList<>();

        // create recyclerviewer
        setRecyclerview(0);

        setRvListener();
    }

    public void setRecyclerview(final int cmd)
    {
        Call<List<Article>> call = Utils.getApi(this).getArticles();
        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Response<List<Article>> response, Retrofit retrofit) {
                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                articles = response.body();

                if( cmd == 1 ) strings.clear();

                if (articles != null)
                {
                    for (Article p : articles) {
                        strings.add(p.toString());
                    }
                } else {
                    strings.add("Inga varor hittades ...");
                }
                if(cmd == 1)
                {
                    lAdapter.notifyDataSetChanged();
                }
                else
                {
                    lAdapter = new LagerAdapter(strings);
                    lAdapter.SetOnItemClickListener(new LagerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            System.out.println(position);
                        }
                    });
                    rv.setItemAnimator(new DefaultItemAnimator());
                    rv.setAdapter(lAdapter);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                strings.add("Kunde inte nå databasen... ");
                lAdapter = new LagerAdapter(strings);
                rv.setAdapter(lAdapter);
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

        HandleLager hl = new HandleLager();

        switch(id) {
            case (R.id.lagethantering_add): { hl.createArticleDialog();}
            break;
            case (R.id.lagethantering_remove): { hl.deleteArticleDialog();}
            break;
            case (R.id.lagethantering_change): { hl.changeArticleDialog();}
            break;
            case (R.id.lagethantering_refresh): { setRecyclerview(1); }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class HandleLager
    {
        private int deleteID;
        private Article body = new Article();
        private Article retrievedArticle = new Article();

        public void deleteArticleDialog()
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LagerActivity.this);
            builder.setTitle("");
            builder.setMessage("Mata in ID för varan du vill ta bort");


            //input object
            final EditText input = new EditText(LagerActivity.this);

            // constraint for input
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            // put input into dialog
            builder.setView(input);


            builder.setPositiveButton("Genmför", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    try
                    {
                        if(input.getText().toString().equals("")) { throw new Throwable("Ange ID"); }
                        deleteID = Integer.parseInt(input.getText().toString());

                        if(idDoesNotExist()) { throw new Throwable("Varan med ID: " + deleteID + " existerar inte"); }


                        deleteArticle();
                        dialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Genomförd", Toast.LENGTH_LONG);
                        toast.show();
                        setRecyclerview(1);
                    }
                    catch (Throwable t)
                    {
                        dialog.dismiss();
                        String s = t.getMessage();
                        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
                        toast.show();
                        deleteArticleDialog();
                    }
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

        public void createArticleDialog()
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LagerActivity.this);

            builder.setTitle("   Mata in värden för varan");

            // defines layout
            LinearLayout ll = new LinearLayout(LagerActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation

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
            date_title.setText("  Utgångsdatum (YYYY/MM/DD)");
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
            ll.addView(category);

            // set view for dialog
            builder.setView(ll);

            builder.setPositiveButton("Genmför", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id)
                {

                    try
                    {
                        body.name = name.getText().toString();
                        if (body.name.equals("")) throw new Throwable("Ange namn");

                        body.unit = unit.getText().toString();
                        if (body.unit.equals("")) throw new Throwable("Ange enhet");

                        if(amount.getText().toString().equals("")) throw new Throwable("Ange mängd");
                        body.amount = Integer.parseInt(amount.getText().toString());
                        if (body.amount == 0) throw new Throwable("Mängd får inte vara 0");

                        body.exp_date = date.getText().toString();
                        if (!(isValidDate(body.exp_date)) && !(body.exp_date.equals(""))) throw new Throwable("Dåligt format av utgångsdatum");

                        body.category = category.getText().toString();
                        if (body.category.equals("")) throw new Throwable("Ange kategori");

                        createArticle();
                        dialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Genomförd", Toast.LENGTH_LONG);
                        toast.show();
                        setRecyclerview(1);
                    }
                    catch (Throwable t)
                    {
                        dialog.dismiss();
                        String s = t.getMessage();
                        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
                        toast.show();
                        createArticleDialog();
                    }
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

        public void changeArticleDialog()
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LagerActivity.this);

            builder.setTitle(" Mata in ID och värden som skall  ändras");

            // defines layout
            LinearLayout ll = new LinearLayout(LagerActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation

            // input objects
            final EditText id_art = new EditText(LagerActivity.this);
            final EditText name = new EditText(LagerActivity.this);
            final EditText amount = new EditText(LagerActivity.this);
            final EditText unit = new EditText(LagerActivity.this);
            final EditText category = new EditText(LagerActivity.this);
            final EditText date = new EditText(LagerActivity.this);

            // constraints for input objects
            id_art.setInputType(InputType.TYPE_CLASS_NUMBER);
            name.setInputType(InputType.TYPE_CLASS_TEXT);
            amount.setInputType(InputType.TYPE_CLASS_NUMBER);
            unit.setInputType(InputType.TYPE_CLASS_TEXT);
            category.setInputType(InputType.TYPE_CLASS_TEXT);
            date.setInputType(InputType.TYPE_CLASS_DATETIME);

            // title per input object
            final TextView id_title = new TextView(LagerActivity.this);
            id_title.setText("  ID  (*)");
            final TextView name_title = new TextView(LagerActivity.this);
            name_title.setText("  Namn");
            final TextView unit_title = new TextView(LagerActivity.this);
            unit_title.setText("  Enhet");
            final TextView amount_title = new TextView(LagerActivity.this);
            amount_title.setText("  Mängd");
            final TextView date_title = new TextView(LagerActivity.this);
            date_title.setText("  Utgångsdatum (YYYY/MM/DD)");
            final TextView category_title = new TextView(LagerActivity.this);
            category_title.setText("  Kategori");

            // insert to layout
            ll.addView(new TextView(LagerActivity.this));
            ll.addView(id_title);
            ll.addView(id_art);
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
                public void onClick(DialogInterface dialog, int id) {
                    try {

                        if(id_art.getText().toString().equals("")) throw new Throwable("Ange ID");
                        body.id = Integer.parseInt(id_art.getText().toString());

                        for(int i = 0; i < articles.size(); i++)
                        {
                            if(articles.get(i).id == body.id)
                            {
                                retrievedArticle = articles.get(i);
                                break;
                            }
                            if(i == (articles.size()-1) ) { throw new Throwable("Varan med ID: " + deleteID + " existerar inte"); }
                        }

                        // if article is completely empty or corrupt, throw "Varan med id" + body.id "finns inte")

                        body.name = name.getText().toString();
                        if (body.name.equals("")) body.name=retrievedArticle.name;

                        body.unit = unit.getText().toString();
                        if (body.unit.equals("")) body.unit=retrievedArticle.unit;

                        if(amount.getText().toString().equals(""))
                        {
                            body.amount=retrievedArticle.amount;
                        }
                        else
                        {
                            body.amount = Integer.parseInt(amount.getText().toString());
                        }
                        if (body.amount == 0) throw new Throwable("Mängd får inte vara 0");

                        if(date.getText().toString().equals(""))
                        {
                            body.exp_date=retrievedArticle.exp_date;
                        }
                        else
                        {
                            body.exp_date = date.getText().toString();
                            if (!(isValidDate(body.exp_date)) && !(body.exp_date.equals(""))) throw new Throwable("Fel format på utgångsdatum");
                        }


                        body.category = category.getText().toString();
                        if (body.category.equals("")) body.category=retrievedArticle.category;

                        changeArticle();
                        dialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Genomförd", Toast.LENGTH_LONG);
                        toast.show();
                        setRecyclerview(1);
                    }
                    catch(Throwable t)
                    {
                        dialog.dismiss();
                        String s = t.getMessage();
                        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
                        toast.show();
                        changeArticleDialog();
                    }
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

        public boolean isValidDate(String inDate) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(inDate.trim());
            } catch (ParseException pe) {
                return false;
            }
            return true;
        }

        public boolean idDoesNotExist() {
            for (int i = 0; i < articles.size(); i++) {
                if (articles.get(i).id == deleteID) {
                    return false;
                }
            }
            return true;
        }
    }

    public void setRvListener() {

    }
}