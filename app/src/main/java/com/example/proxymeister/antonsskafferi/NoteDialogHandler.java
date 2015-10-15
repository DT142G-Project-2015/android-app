package com.example.proxymeister.antonsskafferi;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.Note;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NoteDialogHandler implements View.OnClickListener {


    private Item item;
    private int orderId, groupId;
    private Context context;
    private Callback callback;
    private ListView listviewaddednotes;
    private List<String> addednotes;
    private ListAdapter theAdapter;
    private Dialog dialog;

    public interface Callback {
        void onDone();
    }
    
    public NoteDialogHandler(Item it, int gid, int orderId, Context con, Callback callback)
    {
        context = con;
        item = it;
        this.orderId = orderId;
        this.groupId = gid;
        this.callback = callback;
        showNoteDialog();
    }


    private void done() {
        if (callback != null)
            callback.onDone();
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {

        String thenewnote = ((Button)v).getText().toString();

        Button clickedButton = (Button)v;
        if (clickedButton.getId() == R.id.dialogButtonDONE) {
            EditText noteTextView = (EditText)dialog.findViewById(R.id.newnotetext);
            thenewnote = noteTextView.getText().toString();
        }

        Note n = new Note();
        n.text = thenewnote;

        addednotes.add(n.text);
        listviewaddednotes.setAdapter(theAdapter);
        //listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);
        // listviewaddednotes.setAdapter(theAdapter);

        addNote(orderId, groupId, item.id, n, thenewnote);
    }


    // Show dialogs for creating notes to an item
    public void showNoteDialog() {


        dialog = new Dialog(context);
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

        addednotes = new ArrayList<>();
        theAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
                addednotes);

        listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);

        if (!item.notes.isEmpty()) {
            for (Note note : item.notes) {
                addednotes.add(note.text);
            }

            listviewaddednotes.setAdapter(theAdapter);
        }

        // Edit text for own note
        final EditText newnote = (EditText) dialog.findViewById(R.id.newnotetext);

        Button doneButton = (Button) dialog.findViewById(R.id.dialogButtonDONE);

        final Button mediumButton = (Button) dialog.findViewById(R.id.mediummeatbtn);

        final Button rareButton = (Button) dialog.findViewById(R.id.raremeatbtn);
        final Button welldoneButton = (Button) dialog.findViewById(R.id.welldonemeatbtn);

        rareButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        welldoneButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);


        dialog.show();

    }


    void addNote(int orderId, int groupid, int itemid, Note n, final String thenewnote) {
        Call<Void> call = Utils.getApi(context).addNote(orderId, groupid, itemid, n);
        call.enqueue(new retrofit.Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                Log.i("idg", "Response succesfull: " + response.code());
                Toast.makeText(context, thenewnote + " tillagd", Toast.LENGTH_SHORT).show();
                done();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
