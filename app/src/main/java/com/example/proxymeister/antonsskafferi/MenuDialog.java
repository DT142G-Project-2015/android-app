package com.example.proxymeister.antonsskafferi;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Menu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MenuDialog implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private final Context context;
    private final Menu menuToEdit;
    private Callback callback;
    private TextView startDateText;
    private TextView stopDateText;
    private ImageView startDateButton;
    private ImageView stopDateButton;
    private RadioButton button0;
    private RadioButton button1;
    private RadioButton button2;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private int getMenuType() {
        return button0.isChecked() ? 0 : button1.isChecked() ? 1 : 2;
    }

    private void setMenuType(int type) {
        switch (type) {
            case 2: button2.setChecked(true); break;
            case 1: button1.setChecked(true); break;
            case 0:
            default:
                button0.setChecked(true);

        }
    }

    public interface Callback {
        void onResult(Menu menu, DialogInterface dialog);
    }

    public MenuDialog(Context context, Menu menuToEdit, Callback callback) {
        this.context = context;
        this.menuToEdit = menuToEdit;
        this.callback = callback;
    }

    public void show() {
        final View view = LayoutInflater.from(context).inflate(R.layout.activity_menu_list_dialog, null);

        startDateText = (TextView)view.findViewById(R.id.start_date);
        stopDateText = (TextView)view.findViewById(R.id.stop_date);
        startDateButton = (ImageView)view.findViewById(R.id.start_date_btn);
        stopDateButton = (ImageView) view.findViewById(R.id.stop_date_btn);
        button0 = (RadioButton)view.findViewById(R.id.radioButton0);
        button1 = (RadioButton)view.findViewById(R.id.radioButton1);
        button2 = (RadioButton)view.findViewById(R.id.radioButton2);

        startDateButton.setOnClickListener(this);
        stopDateButton.setOnClickListener(this);

        String title;
        if (menuToEdit == null) {
            // Set default dates to today
            Date today = new Date();
            startDateText.setText(sdf.format(today));
            stopDateText.setText(sdf.format(today));
            title = "Skapa meny";
        } else {

            startDateText.setText(sdf.format(menuToEdit.start_date));
            stopDateText.setText(sdf.format(menuToEdit.stop_date));
            setMenuType(menuToEdit.type);
            title = "Redigera meny";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
        .setTitle(title)
        .setView(view)
        .setPositiveButton(title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onDone(dialog);
            }
        })
        .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void onDone(DialogInterface dialog) {

        Menu m = menuToEdit != null ? menuToEdit : new Menu();

        try {
            m.start_date = sdf.parse(startDateText.getText().toString());
            m.stop_date = sdf.parse(stopDateText.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(context, "Felaktigt datumformat", Toast.LENGTH_SHORT).show();
            return;
        }

        m.type = getMenuType();

        callback.onResult(m, dialog);
    }

    // open DatePickerDialog
    public void onClick(View v) {

        TextView dateText = v == startDateButton ? startDateText :
                            v == stopDateButton ? stopDateText : null;

        if (dateText != null) {
            try {
                Date date = sdf.parse(dateText.getText().toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                DatePickerDialog dateDialog = new DatePickerDialog(context, this,
                        cal.get(Calendar.YEAR) , cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                dateDialog.getDatePicker().setTag(dateText);
                dateDialog.show();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDateSet(DatePicker picker, int year, int monthOfYear, int dayOfMonth) {
        TextView dateText = (TextView)picker.getTag();
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        dateText.setText(sdf.format(cal.getTime()));
    }

}
