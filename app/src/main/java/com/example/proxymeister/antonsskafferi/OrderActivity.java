package com.example.proxymeister.antonsskafferi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.DividerItemDecoration;
import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.IdHolder;
import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.Menu;
import com.example.proxymeister.antonsskafferi.model.Note;
import com.example.proxymeister.antonsskafferi.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.proxymeister.antonsskafferi.model.Group.Status.Done;
import static com.example.proxymeister.antonsskafferi.model.Group.Status.Initial;
import static com.example.proxymeister.antonsskafferi.model.Group.Status.ReadyForKitchen;
import static com.example.proxymeister.antonsskafferi.model.Group.Status.ReadyToServe;

public class OrderActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_ITEM = 1;
    private static final int REQUEST_CODE_PICK_SUB_ITEM = 2;

    private Timer timer = new Timer();
    private List<Order> orders;
    private int expandedPosition = -1;
    private ListView listviewaddednotes;
    private List<Note> notes = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
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
        getAllOrders(-1, null);

        mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
        mLayoutManager = new LinearLayoutManager(OrderActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(OrderActivity.this, DividerItemDecoration.VERTICAL_LIST));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ready();
            }
        }, 0, 5000);
    }

    void notice() {
        try {
            //Sound
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            //Vibration
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void ready() {
        actuallyGetAllOrders(new OrdersCallback() {
            public void ordersReady(List<Order> orders) {

                List<Order> existingOrders = OrderActivity.this.orders;


                if (existingOrders != null) {
                    Order changedOrder = null;

                    for (Order order : orders)
                        for (Order existingOrder : existingOrders)
                            if (order.id == existingOrder.id)
                                for (Group group : order.groups)
                                    for (Group existingGroup : existingOrder.groups)
                                        if (group.id == existingGroup.id && !group.status.equals(existingGroup.status))
                                            changedOrder = order;


                    if (changedOrder != null) {
                        OrderActivity.this.orders = orders;
                        setOrderAdapter(orders.indexOf(changedOrder), mLayoutManager.onSaveInstanceState());
                        notice();
                    }
                }

            }
        });
    }


    interface OrdersCallback {
        void ordersReady(List<Order> orders);
    }

    private void actuallyGetAllOrders(final OrdersCallback callback) {
        Utils.getApi(this).getOrders().enqueue(new Callback<List<Order>>() {
            @Override public void onResponse(Response<List<Order>> response, Retrofit retrofit) {

                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);
                List<Order> newOrders = response.body();

                if (newOrders != null) {
                    for (int i = 0; i < newOrders.size(); i++) {
                        if (newOrders.get(i).payed)
                            newOrders.remove(i--);
                    }
                    callback.ordersReady(newOrders);
                }
            }

            @Override public void onFailure(Throwable t) {}
        });
    }


    public void getAllOrders(final int pos, final Parcelable scrollState) {
        actuallyGetAllOrders(new OrdersCallback() {
            @Override
            public void ordersReady(List<Order> orders) {
                OrderActivity.this.orders = orders;
                setOrderAdapter(pos, scrollState);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent result) {

        if (resultCode == RESULT_OK) {
            final int orderId = result.getIntExtra("order-id", 0);
            final int groupId = result.getIntExtra("group-id", 0);
            final int itemId = result.getIntExtra("item-id", 0);
            final int pos = result.getIntExtra("pos", -1);
            final Parcelable scrollState = result.getParcelableExtra("scrollState");

            Callback<IdHolder> callback = new Callback<IdHolder>() {
                @Override public void onResponse(Response<IdHolder> response, Retrofit retrofit) {
                    if (response.isSuccess()) {

                        IdHolder idHolder = response.body();  // only gets OrderGroupItem id

                        final Menu.Item item = (Menu.Item)result.getSerializableExtra("picked-item");
                        item.id = idHolder.id;

                        if (item.type == 2 && requestCode == REQUEST_CODE_PICK_ITEM) { // if meat
                            NoteDialogHandler handler = new NoteDialogHandler(item, groupId, orderId, OrderActivity.this, new NoteDialogHandler.Callback() {
                                @Override
                                public void onDone() {

                                    Intent intent = MenuActivity.getPickItemIntent(OrderActivity.this);
                                    intent.putExtra("order-id", orderId);
                                    intent.putExtra("group-id", groupId);
                                    intent.putExtra("item-id", item.id);
                                    intent.putExtra("pos", pos);
                                    intent.putExtra("scrollState", scrollState);
                                    Toast.makeText(OrderActivity.this, "Välj tillbehör till " + item.name, Toast.LENGTH_LONG).show();

                                    startActivityForResult(intent, REQUEST_CODE_PICK_SUB_ITEM);

                                    getAllOrders(pos, scrollState);
                                }
                            });
                            handler.setTitle("Välj tillagning: ");
                            handler.showNoteDialogCooking();

                        } else {
                            getAllOrders(pos, scrollState);
                        }
                    }
                }

                @Override public void onFailure(Throwable t) {}
            };

            Menu.Item item = (Menu.Item)result.getSerializableExtra("picked-item");

            if(requestCode == REQUEST_CODE_PICK_ITEM)
                Utils.getApi(this).addItem(item, orderId, groupId).enqueue(callback);

            if (requestCode == REQUEST_CODE_PICK_SUB_ITEM)
                Utils.getApi(this).addSubItem(item, orderId, groupId, itemId).enqueue(callback);

        }

    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public List<Item> items = new ArrayList<>();
        private TextView mOrderTextView;
        private TextView mTotPriceTextView;
        public LinearLayout groupHolder;
        public Button mAddGroupButton;
        public Button mPayedButton;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mOrderTextView = (TextView) itemView.findViewById(R.id.order);
            mTotPriceTextView = (TextView) itemView.findViewById(R.id.totalPrice);
            mAddGroupButton = (Button) itemView.findViewById(R.id.addGroup);
            mPayedButton = (Button) itemView.findViewById(R.id.doneOrder);
            groupHolder = (LinearLayout) itemView.findViewById(R.id.group_holder);
        }
    }

    public void onScroll(Parcelable savedState) {
        if (savedState != null)
            mLayoutManager.onRestoreInstanceState(savedState);
    }

    void setOrderAdapter(final int pos, final Parcelable scrollState) {
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
                viewHolder.mOrderTextView.setText("Bord " + orders.get(i).booth);
                double totPrice = orders.get(i).getTotalPrice();
                final int orderId = orders.get(i).getId();

                LayoutInflater inflater = (LayoutInflater) getSystemService(OrderActivity.LAYOUT_INFLATER_SERVICE);
                final LinearLayout groupHolder = (LinearLayout) viewHolder.groupHolder;

                groupHolder.removeAllViews();
                if (i == expandedPosition) {
                    groupHolder.setVisibility(View.VISIBLE);
                    viewHolder.mTotPriceTextView.setVisibility(View.VISIBLE);
                    viewHolder.mAddGroupButton.setVisibility(View.VISIBLE);
                    viewHolder.mPayedButton.setVisibility(View.VISIBLE);
                } else {
                    groupHolder.setVisibility(View.GONE);
                    viewHolder.mTotPriceTextView.setVisibility(View.GONE);
                    viewHolder.mAddGroupButton.setVisibility(View.GONE);
                    viewHolder.mPayedButton.setVisibility(View.GONE);
                }

                for (final Group g : orders.get(i).groups) {
                    View groupView = inflater.inflate(R.layout.recyclerview_group_view, null);
                    final int groupID = g.id;

                    //SEND TO KITCHEN
                    Button mSendToKitchenButton = (Button) groupView.findViewById(R.id.sendToKitchen);
                    OnClickListener sendGroup = new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                //Yes or no dialog box
                                final Dialog dialog = new Dialog(OrderActivity.this);
                                dialog.setContentView(R.layout.activity_generic_yes_no_dialog);
                                dialog.setTitle("Skicka till köket");
                                final TextView sendkitchen = (TextView) dialog.findViewById(R.id.textSuretoSend);
                                sendkitchen.append(getString(R.string.confirm_send_kitchen));

                                //YES
                                Button yesButton = (Button) dialog.findViewById(R.id.genericDialogButtonYES);
                                yesButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //Sends the order to the kitchen
                                        g.status = ReadyForKitchen;
                                        Call<Void> call = Utils.getApi(OrderActivity.this).changeStatus(g, orderId, groupID);
                                        call.enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Response<Void> response, Retrofit retrofit) {
                                                System.out.println("working");
                                                g.status = ReadyForKitchen;
                                                getAllOrders(i, mLayoutManager.onSaveInstanceState());
                                            }

                                            @Override
                                            public void onFailure(Throwable t) {
                                                System.out.println("not working");

                                            }
                                        });

                                        dialog.dismiss();
                                    }
                                });
                                //NO
                                Button noButton = (Button) dialog.findViewById(R.id.genericDialogButtonNO);
                                noButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();


                        }
                    };
                    mSendToKitchenButton.setOnClickListener(sendGroup);
                    //END SENDTOKITCHEN

                    // ADD ITEM TO ORDER
                    Button mAddItemButton = (Button) groupView.findViewById(R.id.addItemToGroup);
                    OnClickListener additem = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = MenuActivity.getPickItemIntent(OrderActivity.this);
                            intent.putExtra("order-id", orderId);
                            intent.putExtra("group-id", groupID);
                            intent.putExtra("pos", i);
                            intent.putExtra("scrollState", mLayoutManager.onSaveInstanceState());
                            startActivityForResult(intent, REQUEST_CODE_PICK_ITEM);
                        }
                    };
                    mAddItemButton.setOnClickListener(additem);
                    //END

                    //MARK DONE
                    Button mDoneButton = (Button) groupView.findViewById(R.id.done);
                    OnClickListener markDone = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            g.status = Done;
                            Call<Void> call = Utils.getApi(OrderActivity.this).changeStatus(g, orderId, groupID);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Response<Void> response, Retrofit retrofit) {
                                    System.out.println("working");
                                    g.status = Done;
                                    if (orders.get(i).allDone())
                                        getAllOrders(-1, scrollState);
                                    else
                                        getAllOrders(i, scrollState);
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

                    TextView mItemStatus = (TextView) groupView.findViewById(R.id.group_status);

                    if (g.status == Initial) {
                        groupView.setBackgroundColor(Color.WHITE);
                        if (!g.items.isEmpty())
                            mSendToKitchenButton.setVisibility(View.VISIBLE);

                            mItemStatus.setVisibility(View.GONE);
                    }
                    if (g.status == ReadyForKitchen) {
                        groupView.setBackgroundColor(Color.parseColor("#FFC726"));
                        mAddItemButton.setVisibility(View.GONE);
                        mSendToKitchenButton.setVisibility(View.GONE);

                        mItemStatus.setText(R.string.status_ready_for_kitchen);
                        mItemStatus.setTextColor(Color.BLACK);
                        mItemStatus.setVisibility(View.VISIBLE);
                    }
                    if (g.status == ReadyToServe) {
                        groupView.setBackgroundColor(Color.parseColor("#609040"));
                        mAddItemButton.setVisibility(View.GONE);
                        mSendToKitchenButton.setVisibility(View.GONE);

                        mItemStatus.setText(R.string.status_ready_to_serve);
                        mItemStatus.setVisibility(View.VISIBLE);
                    }
                    if (g.status == Done) {
                        groupView.setBackgroundColor(Color.parseColor("#CDCDCD"));
                        mItemStatus.setVisibility(View.GONE);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(0, 4, 0, 4);
                    groupView.setLayoutParams(lp);

                    final LinearLayout itemHolder = (LinearLayout) groupView.findViewById(R.id.item_holder);

                    for (final Item it : g.items) {
                        View itemView = inflater.inflate(R.layout.recyclerview_item_view, null);
                        TextView tv = (TextView) itemView.findViewById(R.id.item);

                        if(g.status == Initial) {
                            tv.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    Toast.makeText(OrderActivity.this, "Lång klick " + orderId + " " + g.id + " " + it.id, Toast.LENGTH_SHORT).show();

                                    Intent intent = MenuActivity.getPickItemIntent(OrderActivity.this);
                                    intent.putExtra("order-id", orderId);
                                    intent.putExtra("group-id", g.id);
                                    intent.putExtra("item-id", it.id);
                                    intent.putExtra("pos", i);
                                    intent.putExtra("scrollState", mLayoutManager.onSaveInstanceState());

                                    startActivityForResult(intent, REQUEST_CODE_PICK_SUB_ITEM);

                                    return true;
                                }
                            });

                            tv.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(OrderActivity.this, "Tryck och håll in  för att lägga till ett tillbehör till " + it.name, Toast.LENGTH_SHORT).show();


                                }
                            });
                        }

                        final Button deletebtn = (Button) itemView.findViewById(R.id.itemRemoveId);
                        final Button addnotebtn = (Button) itemView.findViewById(R.id.itemNoteId);

                        if (g.status == Initial) {
                            itemView.setBackgroundColor(Color.WHITE);
                            tv.setBackgroundColor(Color.WHITE);
                            tv.setTextColor(Color.BLACK);
                        }
                       if (g.status == ReadyForKitchen) {
                            itemView.setBackgroundColor(Color.parseColor("#FFC726"));
                            tv.setBackgroundColor(Color.parseColor("#FFC726"));
                           tv.setTextColor(Color.BLACK);
                            deletebtn.setVisibility(View.GONE);
                            addnotebtn.setVisibility(View.GONE);
                        }
                        if (g.status == ReadyToServe) {
                            itemView.setBackgroundColor(Color.parseColor("#609040"));
                            tv.setBackgroundColor(Color.parseColor("#609040"));
                            deletebtn.setVisibility(View.GONE);
                            addnotebtn.setVisibility(View.GONE);
                        }
                        if (g.status == Done) {
                            itemView.setBackgroundColor(Color.parseColor("#CDCDCD"));
                            tv.setBackgroundColor(Color.parseColor("#CDCDCD"));
                        }

                        tv.setText(it.name + ", " + it.price + ":-");
                        itemHolder.addView(itemView);

                        if(!it.notes.isEmpty())
                        {
                            addnotebtn.setText("(" + it.notes.size() + ")" + " " + "Notering");
                        }


                        OnClickListener deletebuttonListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSecureDialog(null, it, g, orderId, i);
                            }
                        };
                        deletebtn.setOnClickListener(deletebuttonListener);


                        OnClickListener addnotebuttonListener = new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                NoteDialogHandler handler = new NoteDialogHandler(it, g.id, orderId, OrderActivity.this, new NoteDialogHandler.Callback() {
                                    @Override
                                    public void onDone() {
                                        getAllOrders(i, mLayoutManager.onSaveInstanceState());
                                    }
                                });

                                handler.setTitle("Lägg till notering: ");
                                handler.showNoteDialog();

                            }
                        };
                        addnotebtn.setOnClickListener(addnotebuttonListener);

                        /// LOOPAR UT ALLA SUBITEMS

                        for(final Item subIt : it.subItems){
                            View itemSubView = inflater.inflate(R.layout.recyclerview_item_view, null);
                            TextView tvsub = (TextView) itemSubView.findViewById(R.id.item);
                            final Button deletesubitembtn = (Button) itemSubView.findViewById(R.id.itemRemoveId);
                            final Button addnotesubitembtn = (Button) itemSubView.findViewById(R.id.itemNoteId);

                            if (g.status == Initial) {
                                itemSubView.setBackgroundColor(Color.WHITE);
                                tvsub.setBackgroundColor(Color.WHITE);
                                tvsub.setTextColor(Color.BLACK);
                            }
                            if (g.status == ReadyForKitchen) {
                                itemSubView.setBackgroundColor(Color.parseColor("#FFC726"));
                                tvsub.setBackgroundColor(Color.parseColor("#FFC726"));
                                deletesubitembtn.setVisibility(View.GONE);
                                addnotesubitembtn.setVisibility(View.GONE);
                            }
                            if (g.status == ReadyToServe) {
                                itemSubView.setBackgroundColor(Color.parseColor("#609040"));
                                tvsub.setBackgroundColor(Color.parseColor("#609040"));
                                deletesubitembtn.setVisibility(View.GONE);
                                addnotesubitembtn.setVisibility(View.GONE);
                            }
                            if (g.status == Done) {
                                itemSubView.setBackgroundColor(Color.parseColor("#CDCDCD"));
                                tvsub.setBackgroundColor(Color.parseColor("#CDCDCD"));
                            }

                            if(!subIt.notes.isEmpty())
                            {
                                addnotesubitembtn.setText("(" + subIt.notes.size() + ")" + " " + "Notering");
                            }

                            OnClickListener deletesubitembuttonListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showSecureDialog(subIt, it,  g, orderId, i);
                                }
                            };
                            deletesubitembtn.setOnClickListener(deletesubitembuttonListener);


                            OnClickListener addnotesubitembuttonListener = new OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    NoteDialogHandler handler = new NoteDialogHandler(subIt, it, g.id, orderId, OrderActivity.this, new NoteDialogHandler.Callback() {
                                        @Override
                                        public void onDone() {
                                            getAllOrders(i, scrollState);
                                        }
                                    });
                                    handler.setTitle("Lägg till notering för tillbehör: ");
                                    handler.showNoteDialog();

                                }
                            };
                            addnotesubitembtn.setOnClickListener(addnotesubitembuttonListener);

                            tvsub.setText("        " + subIt.name + ", " + subIt.price + ":-");
                            tvsub.setTextColor(Color.GRAY);
                            itemHolder.addView(itemSubView);
                        }
                        //END
                    }
                    groupHolder.addView(groupView);

                }

                //ADD GROUP
                viewHolder.mAddGroupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Group gr = new Group();
                        gr.status = Initial;
                        final Call<Group> call = Utils.getApi(OrderActivity.this).createOrderGroup(gr, orderId);
                        call.enqueue(new retrofit.Callback<Group>() {
                            @Override
                            public void onResponse(Response<Group> response, Retrofit retrofit) {
                                Log.i(MainActivity.class.getName(), "NICE");
                                getAllOrders(i, scrollState);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                            }
                        });
                    }
                });
                //END ADDGROUP

                //PAYED ORDER
                viewHolder.mPayedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            final Dialog dialog = new Dialog(OrderActivity.this);
                            dialog.setContentView(R.layout.activity_generic_yes_no_dialog);
                            dialog.setTitle("Avsluta beställning");

                        final TextView terminateorder = (TextView) dialog.findViewById(R.id.textSuretoSend);
                        terminateorder.append(getString(R.string.confirm_terminate_order));

                            Button yesButton = (Button) dialog.findViewById(R.id.genericDialogButtonYES);
                            yesButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Order or = orders.get(i);
                                    or.payed = true;
                                    final Call<Void> call = Utils.getApi(OrderActivity.this).updateOrderStatus(or, orderId);
                                    call.enqueue(new retrofit.Callback<Void>() {
                                        @Override
                                        public void onResponse(Response<Void> response, Retrofit retrofit) {
                                            Log.i(MainActivity.class.getName(), "NICE");
                                            getAllOrders(-1, scrollState);
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                                        }
                                    });

                                    dialog.dismiss();
                                }
                            });

                            Button noButton = (Button) dialog.findViewById(R.id.genericDialogButtonNO);

                            noButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();

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

                        if (expandedPosition == i) {
                            expandedPosition = -1;
                        } else {
                            expandedPosition = i;
                        }

                        mAdapter.notifyDataSetChanged();
                        if (expandedPosition != -1) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.scrollToPosition(expandedPosition);
                                }
                            });
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
        if (pos >= 0)
            onScroll(scrollState);

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
                g.status = Initial;
                o.groups.add(g);
                Call<Void> call = Utils.getApi(OrderActivity.this).createOrder(o);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        Log.i("idg", "Response succesfull");
                        getAllOrders(-1, mLayoutManager.onSaveInstanceState());
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

    public void showSecureDialog(final Item subitem, final Item item, final Group group, final int orderId, final int position) {
        final Dialog dialog = new Dialog(OrderActivity.this);
        dialog.setContentView(R.layout.activity_order_secure_remove);
        dialog.setTitle("Ta bort");

        final TextView itemtoremove = (TextView) dialog.findViewById(R.id.textSuretoremove);
        itemtoremove.append(" \"" + item.name + "\"?");

        Button yesButton = (Button) dialog.findViewById(R.id.dialogButtonYES);
        yesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(orderId, group.id, item.id, subitem == null ? null : subitem.id, position, dialog);
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
    }



    public void deleteItem(int orderId, int groupId, int itemId, final Integer subItemId, final int position, final Dialog dialog) {

        Call<Void> call;
        if (subItemId == null)
            call = Utils.getApi(this).deleteItem(orderId, groupId, itemId);
        else
            call = Utils.getApi(this).deleteSubItem(orderId, groupId, itemId, subItemId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    getAllOrders(position, mLayoutManager.onSaveInstanceState());
                    dialog.dismiss();
                }
                Log.i("DELETE", "Success");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(OrderActivity.class.getName(), "Failed to delete data " + t.getMessage());
            }
        });
    }
}
