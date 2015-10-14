package com.example.proxymeister.antonsskafferi;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.Note;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NoteDialogHandler extends AppCompatActivity {

    Item item; 
    int orderId, groupId;
    Context context;
    
    public NoteDialogHandler(Item it, int gid, int orderId, Context con)
    {
        context = con;
        item = it;
        this.orderId = orderId;
        this.groupId = gid;
        showNoteDialog();


    }



    // Show dialogs for creating notes to an item
    public void showNoteDialog() {


        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_order_add_new_note);
        dialog.setTitle("Ny notering");


        if (item.type == 2) {
            dialog.findViewById(R.id.buttonsmeat).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.textmeat).setVisibility(View.VISIBLE);
        } else {
            dialog.findViewById(R.id.buttonsmeat).setVisibility(View.GONE);
            dialog.findViewById(R.id.textmeat).setVisibility(View.GONE);
        }


        // This button closes the inner dialog
        Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCANCEL);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final List<String> addednotes = new ArrayList<>();
        final ListAdapter theAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
                addednotes);

        //listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);

/*        if (!item.notes.isEmpty()) {
            for (Note note : item.notes) {
                addednotes.add(note.text);
            }

            //listviewaddednotes.setAdapter(theAdapter);
        }
*/

        // Edit text for own note
        final EditText newnote = (EditText) dialog.findViewById(R.id.newnotetext);


        Button doneButton = (Button) dialog.findViewById(R.id.dialogButtonDONE);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = newnote.getText().toString();

                Note n = new Note();
                n.text = thenewnote;

                addNote(orderId, groupId, item.id, n);
                addednotes.add(n.text);
                //listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);
                // listviewaddednotes.setAdapter(theAdapter);

                Toast.makeText(context, thenewnote + " tillagd", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });


        final Button welldoneButton = (Button) dialog.findViewById(R.id.welldonemeatbtn);
        welldoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = welldoneButton.getText().toString();

                Note n = new Note();
                n.text = thenewnote;

                addNote(orderId, groupId, item.id, n);
                addednotes.add(n.text);
                // listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);
                // listviewaddednotes.setAdapter(theAdapter);

                Toast.makeText(context, thenewnote + " tillagd", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        final Button mediumButton = (Button) dialog.findViewById(R.id.mediummeatbtn);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = mediumButton.getText().toString();

                Note n = new Note();
                n.text = thenewnote;

                addNote(orderId, groupId, item.id, n);
                addednotes.add(n.text);
                //listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);
                // listviewaddednotes.setAdapter(theAdapter);

                Toast.makeText(context, thenewnote + " tillagd", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        final Button rareButton = (Button) dialog.findViewById(R.id.raremeatbtn);
        rareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = rareButton.getText().toString();

                Note n = new Note();
                n.text = thenewnote;

                addNote(orderId, groupId, item.id, n);
                addednotes.add(n.text);
                //listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);
                //listviewaddednotes.setAdapter(theAdapter);

                Toast.makeText(context, thenewnote + " tillagd", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();

    }


    void addNote(int orderId, int groupid, int itemid, Note n) {
        Call<Void> call = Utils.getApi(context).addNote(orderId, groupid, itemid, n);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                Log.i("idg", "Response succesfull: " + response.code());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("idg", "MEGA FAIL");
            }
        });
    }

}
