package com.example.proxymeister.antonsskafferi;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import retrofit.Response;
import retrofit.Retrofit;

public class NoteDialogHandler implements View.OnClickListener {

    private Note n = new Note();
    private Item item;
    private Item subitem;
    private int orderId;
    private int groupId;
    private Context context;
    private Callback callback;
    private ListView listviewaddednotes;
    private List<String> addednotes;
    private List<Integer> addednotesid;
    private ListAdapter theAdapter;
    private Dialog dialog;
    private boolean isItem;

    public void showNoteDialogCooking() {
        dialog.setContentView(R.layout.activity_order_add_new_note);


        dialog.findViewById(R.id.buttonsmeat).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.textmeat).setVisibility(View.GONE);
        dialog.findViewById(R.id.newnotetext).setVisibility(View.GONE);
        dialog.findViewById(R.id.addednotestext).setVisibility(View.GONE);
        dialog.findViewById(R.id.dialogButtonDONE).setVisibility(View.GONE);
        dialog.findViewById(R.id.dialogButtonCANCEL).setVisibility(View.GONE);





        final Button mediumButton = (Button) dialog.findViewById(R.id.mediummeatbtn);
        final Button rareButton = (Button) dialog.findViewById(R.id.raremeatbtn);
        final Button welldoneButton = (Button) dialog.findViewById(R.id.welldonemeatbtn);
        rareButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        welldoneButton.setOnClickListener(this);

        dialog.show();
    }

    public interface Callback {
        void onDone();
    }

    // Add note to item
    public NoteDialogHandler(Item it, int gid, int orderId, Context con, Callback callback)
    {
        context = con;
        item = it;
        this.orderId = orderId;
        this.groupId = gid;
        this.callback = callback;
        isItem = true;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_order_add_new_note);
    }

    // Add no to subitem
    public NoteDialogHandler(Item subit, Item it, int gid, int orderId, Context con, Callback callback)
    {
        context = con;
        item = it;
        subitem = subit;
        this.orderId = orderId;
        this.groupId = gid;
        this.callback = callback;
        isItem = false;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_order_add_new_note);
    }

    public void setTitle(String title)
    {
        dialog.setTitle(title);
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

        this.n.text = thenewnote;
        if(this.n.text.isEmpty())
        {
            Toast.makeText(context, "Fel: Ange notering eller tryck avbryt", Toast.LENGTH_SHORT).show();
        }
        else
        {
            addNote();
        }
    }


    // Show dialogs for creating notes to an item
    public void showNoteDialog() {

        if(isItem) {
            if (item.type == 2) {
                dialog.findViewById(R.id.buttonsmeat).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.textmeat).setVisibility(View.VISIBLE);
            } else {
                dialog.findViewById(R.id.buttonsmeat).setVisibility(View.GONE);
                dialog.findViewById(R.id.textmeat).setVisibility(View.GONE);
            }
        }
        else{
            if (subitem.type == 2) {
                dialog.findViewById(R.id.buttonsmeat).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.textmeat).setVisibility(View.VISIBLE);
            } else {
                dialog.findViewById(R.id.buttonsmeat).setVisibility(View.GONE);
                dialog.findViewById(R.id.textmeat).setVisibility(View.GONE);
            }
        }


        // This button closes the inner dialog
        Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCANCEL);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        addednotes = new ArrayList<>();
        addednotesid = new ArrayList<>();
        theAdapter = new ArrayAdapter<>(context, R.layout.custom_note_layout,
                addednotes);

        listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);
        listviewaddednotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                deleteNote(pos);
                return true;
            }
        });

        listviewaddednotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(context, "Håll in för att ta bort noteringen " + String.valueOf(adapterView.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();

            }
        });

        if(isItem) {
            if(item.notes != null)
            {
                if(!item.notes.isEmpty()) {
                    for (Note note : item.notes) {
                        addednotes.add(" " + note.text);
                        addednotesid.add(note.id);
                    }

                    listviewaddednotes.setAdapter(theAdapter);
                }
                else
                    dialog.findViewById(R.id.addednotestext).setVisibility(View.GONE);
            }
        }
        else {
            if (subitem.notes != null)
            {
                if(!subitem.notes.isEmpty())
                {
                    for (Note note : subitem.notes) {
                            addednotes.add(" " + note.text);
                            addednotesid.add(note.id);
                        }

                        listviewaddednotes.setAdapter(theAdapter);
                }
                else
                    dialog.findViewById(R.id.addednotestext).setVisibility(View.GONE);
            }
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


    void addNote() {
        if(isItem) {
            Call<Void> call = Utils.getApi(context).addNote(orderId, groupId, item.id, n);
            call.enqueue(new retrofit.Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    Log.i("idg", "Response succesfull: " + response.code());
                    Toast.makeText(context, n.text + " tillagd till " + item.name, Toast.LENGTH_SHORT).show();
                    done();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Call<Void> call = Utils.getApi(context).addSubItemNote(orderId, groupId, item.id, subitem.id, n);
            call.enqueue(new retrofit.Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    Log.i("idg", "Response succesfull: " + response.code());
                    Toast.makeText(context, n.text + " tillagd till tillbehöret " + subitem.name, Toast.LENGTH_SHORT).show();
                    done();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }



    void deleteNote(final int position) {
        n.id = addednotesid.get(position);
        n.text = addednotes.get(position);
        if(isItem) {
            Call<Void> call = Utils.getApi(context).deleteNote(orderId, groupId, item.id, n.id);
            call.enqueue(new retrofit.Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    Log.i("idg", "Response succesfull: " + response.code());
                    Toast.makeText(context, n.text + " borttagen från " + item.name, Toast.LENGTH_SHORT).show();
                    addednotes.remove(position);
                    addednotesid.remove(position);
                    listviewaddednotes.setAdapter(theAdapter);
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Call<Void> call = Utils.getApi(context).deleteSubItemNote(orderId, groupId, item.id, subitem.id, n.id);
            call.enqueue(new retrofit.Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    Log.i("idg", "Response succesfull: " + response.code());
                    Toast.makeText(context, n.text + " borttagen från tillbehöret " + subitem.name, Toast.LENGTH_SHORT).show();
                    addednotes.remove(position);
                    addednotesid.remove(position);
                    listviewaddednotes.setAdapter(theAdapter);
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
