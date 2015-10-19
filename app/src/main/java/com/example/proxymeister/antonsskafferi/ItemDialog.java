package com.example.proxymeister.antonsskafferi;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.Menu;

import java.text.SimpleDateFormat;


public class ItemDialog {

    private final Context context;
    private Callback callback;
    private TextView nameText;
    private TextView descriptionText;
    private TextView priceText;
    private RadioButton button0;
    private RadioButton button1;
    private RadioButton button2;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private int getFoodType() {
        return button0.isChecked() ? 0 : button1.isChecked() ? 1 : 2;
    }

    private void setFoodType(int type) {
        switch (type) {
            case 2: button2.setChecked(true); break;
            case 1: button1.setChecked(true); break;
            case 0:
            default:
                button0.setChecked(true);

        }
    }

    public interface Callback {
        void onResult(Menu.Item item, DialogInterface dialog);
    }

    public ItemDialog(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void show() {
        final View view = LayoutInflater.from(context).inflate(R.layout.activity_item_dialog, null);

        nameText = (TextView)view.findViewById(R.id.name);
        descriptionText = (TextView)view.findViewById(R.id.description);
        priceText = (TextView)view.findViewById(R.id.price);
        button0 = (RadioButton)view.findViewById(R.id.radioButton0);
        button1 = (RadioButton)view.findViewById(R.id.radioButton1);
        button2 = (RadioButton)view.findViewById(R.id.radioButton2);

        String title = "Skapa maträtt";

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(title, null)
                .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Weird hack to prevent dialog from autoclosing
        final Button okButton =
                dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDone(dialog);
            }
        });
    }

    private void onDone(DialogInterface dialog) {

        Menu.Item i = new Menu.Item();
        i.name = nameText.getText().toString();
        i.description = descriptionText.getText().toString();
        try {
            i.price = Double.parseDouble(priceText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Felaktigt pris", Toast.LENGTH_SHORT).show();
            return;
        }

        i.type = getFoodType();

        callback.onResult(i, dialog);
    }
}
