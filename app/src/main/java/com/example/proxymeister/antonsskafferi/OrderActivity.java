package com.example.proxymeister.antonsskafferi;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.DividerItemDecoration;
import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.ItemHolder;
import com.example.proxymeister.antonsskafferi.model.Note;
import com.example.proxymeister.antonsskafferi.model.Order;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OrderActivity extends AppCompatActivity {

    private List<Order> orders;
    private int activePosition;
    private List<Group> groups = new ArrayList<>();
    private ListView listviewaddednotes;
    private List<Note> notes = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter<CustomViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);


        Button btn = (Button) findViewById(R.id.place_new_order_button);
        OnClickListener oclbtn = new OnClickListener() {
            @Override
            public void onClick(View v) {

                showOrderDialog();
            }
        };

        btn.setOnClickListener(oclbtn);
        getAllOrders(-1);

    }

    public void getAllOrders(final int pos) {
        Call<List<Order>> call = Utils.getApi().getOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Response<List<Order>> response, Retrofit retrofit) {

                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);
                orders = response.body();

                if (orders != null) {
                    for (int i = 0; i < orders.size(); i++) {
                        boolean done = orders.get(i).allDone();
                        if(done)
                            orders.remove(i--);

                        else {
                            List<Group> temp = orders.get(i).groups;
                            for (int j = 0; j < temp.size(); j++) {
                                groups.add(temp.get(j));
                            }
                        }
                    }
                    mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
                    mLayoutManager = new LinearLayoutManager(OrderActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(OrderActivity.this, DividerItemDecoration.VERTICAL_LIST));

                    setOrderAdapter(pos);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getAllOrders(data.getIntExtra("result", 1));
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public List<Item> items = new ArrayList<>();
        private TextView mOrderTextView;
        private TextView mTotPriceTextView;
        public LinearLayout groupHolder;
        public Button mAddGroupButton;
        public boolean expanded = false;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mOrderTextView = (TextView) itemView.findViewById(R.id.order);
            mTotPriceTextView = (TextView) itemView.findViewById(R.id.totalPrice);
            mAddGroupButton = (Button) itemView.findViewById(R.id.addGroup);
            groupHolder = (LinearLayout) itemView.findViewById(R.id.group_holder);
        }
    }

    public void onScroll(int i) {
        mRecyclerView.scrollToPosition(i);
    }

    void setOrderAdapter(final int pos) {
        activePosition = pos;
        mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_order_view
                        , viewGroup, false);
                view.setBackgroundResource(android.R.drawable.list_selector_background);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final CustomViewHolder viewHolder, final int i) {
                viewHolder.mOrderTextView.setText("Bord:" + orders.get(i).booth);
                double totPrice = orders.get(i).getTotalPrice();
                final int orderId = orders.get(i).getId();

                LayoutInflater inflater = (LayoutInflater) getSystemService(OrderActivity.LAYOUT_INFLATER_SERVICE);
                final LinearLayout groupHolder = (LinearLayout) viewHolder.groupHolder;
                groupHolder.removeAllViews();
                if (i == activePosition) {
                    groupHolder.setVisibility(View.VISIBLE);
                    viewHolder.mTotPriceTextView.setVisibility(View.VISIBLE);
                    viewHolder.mOrderTextView.setPadding(20, 20, 20, 5);
                    viewHolder.mAddGroupButton.setVisibility(View.VISIBLE);
                    viewHolder.expanded = true;
                }

                for (final Group g : orders.get(i).groups) {
                    View groupView = inflater.inflate(R.layout.recyclerview_group_view, null);
                    final int groupID = g.id;

                    //SEND TO KITCHEN
                    Button mSendToKitchenButton = (Button) groupView.findViewById(R.id.sendToKitchen);
                    OnClickListener sendGroup = new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            g.status = "readyForKitchen";
                            Call<Void> call = Utils.getApi().changeStatus(g, orderId, groupID);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Response<Void> response, Retrofit retrofit) {
                                    System.out.println("working");
                                    g.status = "readyForKitchen";
                                    getAllOrders(i);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    System.out.println("not working");

                                }
                            });
                        }
                    };
                    mSendToKitchenButton.setOnClickListener(sendGroup);
                    //END SENDTOKITCHEN

                    // ADD ITEM TO ORDER
                    Button mAddItemButton = (Button) groupView.findViewById(R.id.addItemToGroup);
                    OnClickListener additem = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OrderActivity.this, OrderMealActivity.class);
                            intent.putExtra("order-id", orderId);
                            intent.putExtra("group-id", groupID);
                            intent.putExtra("pos", i);
                            startActivityForResult(intent, 1);

                        }
                    };
                    mAddItemButton.setOnClickListener(additem);
                    //END

                    //MARK DONE
                    Button mDoneButton = (Button) groupView.findViewById(R.id.done);
                    OnClickListener markDone = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            g.status = "done";
                            Call<Void> call = Utils.getApi().changeStatus(g, orderId, groupID);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Response<Void> response, Retrofit retrofit) {
                                    System.out.println("working");
                                    g.status = "done";
                                    if(orders.get(i).allDone())
                                        getAllOrders(-1);
                                    else
                                        getAllOrders(i);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    System.out.println("not working");

                                }
                            });
                        }
                    };
                    mDoneButton.setOnClickListener(markDone);
                    //END

                    if (g.getStatus().equals("readyForKitchen"))
                        groupView.setBackgroundColor(Color.parseColor("#FFC726"));
                    if (g.getStatus().equals("done")) { // Denna ska inte synas senare.
                        groupView.setBackgroundColor(Color.parseColor("#CDCDCD"));
                        mAddItemButton.setVisibility(View.GONE);
                    }
                    if (g.getStatus().equals("readyToServe")) {
                        groupView.setBackgroundColor(Color.parseColor("#609040"));
                        mAddItemButton.setVisibility(View.GONE);
                        mDoneButton.setVisibility(View.VISIBLE);
                    }
                    if (g.getStatus().equals("initial")) {
                        groupView.setBackgroundColor(Color.WHITE);
                        if(!g.items.isEmpty())
                            mSendToKitchenButton.setVisibility(View.VISIBLE);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(0, 4, 0, 4);
                    groupView.setLayoutParams(lp);

                    final LinearLayout itemHolder = (LinearLayout) groupView.findViewById(R.id.item_holder);

                    for (final Item it : g.items) {
                        View itemView = inflater.inflate(R.layout.recyclerview_item_view, null);
                        TextView tv = (TextView) itemView.findViewById(R.id.item);
                        Button deletebtn = (Button) itemView.findViewById(R.id.itemRemoveId);
                        Button addnotebtn = (Button) itemView.findViewById(R.id.itemNoteId);

                        if (g.getStatus().equals("readyForKitchen")) {
                            itemView.setBackgroundColor(Color.parseColor("#FFC726"));
                            tv.setBackgroundColor(Color.parseColor("#FFC726"));
                        }
                        if (g.getStatus().equals("done")) {
                            itemView.setBackgroundColor(Color.parseColor("#CDCDCD"));
                            tv.setBackgroundColor(Color.parseColor("#CDCDCD"));
                        }
                        if (g.getStatus().equals("readyToServe")) {
                            itemView.setBackgroundColor(Color.parseColor("#609040"));
                            tv.setBackgroundColor(Color.parseColor("#609040"));
                        }
                        if (g.getStatus().equals("initial")) {
                            itemView.setBackgroundColor(Color.WHITE);
                            tv.setBackgroundColor(Color.WHITE);
                            tv.setTextColor(Color.BLACK);
                        }
                        tv.setText(it.name + ", " + it.price + ":-");
                        itemHolder.addView(itemView);


                        OnClickListener deletebuttonListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSecureDialog(it, g, orderId, i);
                            }
                        };
                        deletebtn.setOnClickListener(deletebuttonListener);


                        OnClickListener addnotebuttonListener = new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                showNoteDialog(it, g, orderId);
                            }
                        };
                        addnotebtn.setOnClickListener(addnotebuttonListener);

                    }
                    groupHolder.addView(groupView);

                }

                //ADD GROUP
                viewHolder.mAddGroupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Group gr = new Group();
                        gr.status = "initial";
                        final Call<Group> call = Utils.getApi().createOrderGroup(gr, orderId);
                        call.enqueue(new retrofit.Callback<Group>() {
                            @Override
                            public void onResponse(Response<Group> response, Retrofit retrofit) {
                                Log.i(MainActivity.class.getName(), "NICE");
                                getAllOrders(i);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                            }
                        });
                    }
                });
                //END ADDGROUP

                if (totPrice != 0)
                    viewHolder.mTotPriceTextView.setText("Totalt pris: " + Double.toString(totPrice) + ":-");
                /*for (Item it : groups.get(i).items) {
                    viewHolder.mItemTextView.append("\n" + "   " + it.name);
                }*/

                //EXPANDING CODE
                OnClickListener oclbtn = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!viewHolder.expanded) {
                            groupHolder.setVisibility(View.VISIBLE);
                            viewHolder.mTotPriceTextView.setVisibility(View.VISIBLE);
                            viewHolder.mOrderTextView.setPadding(20, 20, 20, 5);
                            viewHolder.mAddGroupButton.setVisibility(View.VISIBLE);
                            viewHolder.expanded = true;

                        } else {
                            groupHolder.setVisibility(View.GONE);
                            viewHolder.mTotPriceTextView.setVisibility(View.GONE);
                            viewHolder.mOrderTextView.setPadding(20, 20, 20, 20);
                            viewHolder.mAddGroupButton.setVisibility(View.GONE);
                            viewHolder.expanded = false;

                        }
                    }
                };
                viewHolder.mOrderTextView.setOnClickListener(oclbtn);
                //END EXPAND


                viewHolder.mOrderTextView.setPressed(true);
            }

            @Override
            public int getItemCount() {
                return orders.size();
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        if (activePosition > 0)
            onScroll(activePosition);



    }


    public void showOrderDialog() {
        CharSequence tabels[] = new CharSequence[]{"Bord 1", "Bord 2", "Bord 3", "Bord 4", "Bord 5"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Välj ett bord");
        builder.setItems(tabels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int table) {
                Order o = new Order();
                Group g = new Group();
                o.booth = table + 1;
                o.groups = new ArrayList<>();
                g.items = new ArrayList<>();
                g.status = "initial";
                o.groups.add(g);
                Call<Void> call = Utils.getApi().createOrder(o);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        Log.i("idg", "Response succesfull");
                        getAllOrders(-1);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("idg", "MEGA FAIL");
                    }
                });
            }
        });
        builder.setPositiveButton("Avbryt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void showSecureDialog(final Item item, final Group group, final int orderId, final int position) {
        final Dialog dialog = new Dialog(OrderActivity.this);
        dialog.setContentView(R.layout.activity_order_secure_remove);
        dialog.setTitle("Ta bort");

        // Set size of dialog to "max"
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        final TextView itemtoremove = (TextView) dialog.findViewById(R.id.textSuretoremove);
        itemtoremove.append(item.name + "?");

        Button yesButton = (Button) dialog.findViewById(R.id.dialogButtonYES);
        yesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                group.items.remove(item);
                deleteItem(orderId, group.id, item.id);
                mAdapter.notifyItemRemoved(position);
                getAllOrders(position);
                dialog.dismiss();
            }
        });

        Button noButton = (Button) dialog.findViewById(R.id.dialogButtonNO);

        noButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    // Show dialogs for creating notes to an item
    public void showNoteDialog(final Item item, final Group group, final int orderId) {

        final Dialog dialog = new Dialog(OrderActivity.this);
        dialog.setContentView(R.layout.activity_order_add_new_note);
        dialog.setTitle("Ny notering");

        // This button closes the inner dialog
        Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCANCEL);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final List<String> addednotes = new ArrayList<>();
        final ListAdapter theAdapter = new ArrayAdapter<String>(OrderActivity.this, android.R.layout.simple_list_item_1,
                addednotes);

        listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);

        if(!item.notes.isEmpty()) {
            for (Note note : item.notes) {
                addednotes.add(note.text);
            }

                listviewaddednotes.setAdapter(theAdapter);
        }





        // Edit text for own note
        final EditText newnote = (EditText) dialog.findViewById(R.id.newnotetext);

        Button doneButton = (Button) dialog.findViewById(R.id.dialogButtonDONE);
        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = newnote.getText().toString();

                Note n = new Note();
                n.text = thenewnote;

                Call<Void> call = Utils.getApi().addNote(orderId, group.id, item.id, n);
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



                addednotes.add(n.text);
                listviewaddednotes = (ListView) dialog.findViewById(R.id.addednotes);
                listviewaddednotes.setAdapter(theAdapter);


                Toast.makeText(OrderActivity.this, thenewnote + " tillagd", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });



        final Button welldoneButton = (Button) dialog.findViewById(R.id.welldonemeatbtn);
        welldoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = welldoneButton.getText().toString();
                Toast.makeText(OrderActivity.this, thenewnote, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        final Button mediumButton = (Button) dialog.findViewById(R.id.mediummeatbtn);
        mediumButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = mediumButton.getText().toString();
                Toast.makeText(OrderActivity.this, thenewnote, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        final Button rareButton = (Button) dialog.findViewById(R.id.raremeatbtn);
        rareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String thenewnote = rareButton.getText().toString();
                Toast.makeText(OrderActivity.this, thenewnote, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();

    }



@Override
public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order,menu);
        return true;
        }

@Override
public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id=item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.action_settings){
        return true;
        }

        return super.onOptionsItemSelected(item);
        }

public void deleteItem(int orderId,int groupId,int itemId){


        Call<Void>call=Utils.getApi().deleteItem(orderId,groupId,itemId);

        call.enqueue(new Callback<Void>(){
@Override
public void onResponse(Response<Void>response,Retrofit retrofit){
        Log.i("DELETE","Success");
        }

@Override
public void onFailure(Throwable t){
        Log.i(LagerActivity.class.getName(),"Failed to delete data "+t.getMessage());
        }
        });

        }

        }
