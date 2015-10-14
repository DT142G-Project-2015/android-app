package com.example.proxymeister.antonsskafferi;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.proxymeister.antonsskafferi.model.DividerItemDecoration;
import com.example.proxymeister.antonsskafferi.model.Group;
import com.example.proxymeister.antonsskafferi.model.Item;
import com.example.proxymeister.antonsskafferi.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class KitchenActivity extends AppCompatActivity {
    //Checks for new orders
    private Timer timer = new Timer();

    private List<Order> orders;
    private List<Group> groups = new ArrayList<>();
    private List<Group> deletedgroups = new ArrayList<>();


    private RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter<CustomViewHolder> mAdapter;
    private Button undodeletebtn;

    // Animation for the undo button
    private Animation animfadeout;
    // Time to show the undobutton after a group has been dismissed
    private int millisecondstoshowbutton;

    // To store the old positions of deleted groups
    private List<Integer> oldpositions = new ArrayList<>();
    SwipeDismissRecyclerViewTouchListener touchListener;

    // To handle the animation
    private AnimationHandler animationhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);


        undodeletebtn = (Button) findViewById(R.id.undodeletebutton);

        // Set the animation specs
        animfadeout = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        animfadeout.setDuration(500);
        millisecondstoshowbutton = 5000;

        mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
        mLayoutManager = new LinearLayoutManager(KitchenActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(KitchenActivity.this, DividerItemDecoration.VERTICAL_LIST));

        ready();

        // Listener for the undo button.
        // When pressed, removed groups is fetched from deletedgroups
        // inserted at the old position
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (!deletedgroups.isEmpty()) {
                if (deletedgroups.size() > 1) {

                    // Add the last deleted group to its previous position
                    groups.add(oldpositions.get(oldpositions.size() - 1), deletedgroups.get(deletedgroups.size() - 1));

                    // Then remove the previous deleted group and its old position
                    deletedgroups.remove(deletedgroups.size() - 1);
                    oldpositions.remove(oldpositions.size() - 1);
                    setAdapter();
                }
                //Deletes the undo button when no more groups can be undone
                else if (deletedgroups.size() == 1) {

                    // Add the last deleted group to its previous position
                    groups.add(oldpositions.get(oldpositions.size() - 1), deletedgroups.get(deletedgroups.size() - 1));

                    // Then remove the previous deleted group and its old position
                    deletedgroups.remove(deletedgroups.size() - 1);
                    oldpositions.remove(oldpositions.size() - 1);
                    setAdapter();
                    animationhandler = new AnimationHandler();
                    undodeletebtn.postDelayed(animationhandler, 500);
                }
            }
        };
        undodeletebtn.setOnClickListener(buttonListener);

        //Listener for new orders
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


    abstract class SimpleCallback<T> implements retrofit.Callback<T> {
        @Override
        public void onResponse(Response<T> response, Retrofit retrofit) {
            if (response.body() != null) {
                override(response.body());
            }
        }

        @Override
        public void onFailure(Throwable t) {

        }

        public abstract void override(T body);
    }


    void ready() {
        // Get all orders ready for kitchen
        Call<List<Order>> call = Utils.getApi(this).getOrdersByStatus(getString(R.string.StatusReadyForKitchen));

        call.enqueue(new SimpleCallback<List<Order>>() {
                         @Override
                         public void override(List<Order> orders) {
                             // Iterate through every order and add its group(s) to groups list
                             for (Order order : orders) {
                                 for (Group group : order.groups) {
                                     group.tablenum = order.booth;

                                     //Check if group exists in groups & deletedgroups
                                     if (!groups.contains(group) && !deletedgroups.contains(group)) {
                                         //Check if group contains relevant items
                                         Boolean relevant = false;
                                         for (Item item : group.items) {
                                             if (item.type == 0 || item.type == 2) {
                                                 relevant = true;
                                                 break;
                                             }
                                         }
                                         if (relevant) {
                                             groups.add(group);
                                             notice();
                                         }
                                     }
                                 }
                             }

                             setAdapter();
                             setSwipeListener();
                             setScrollListener();
                         }
                     }
        );
    }


    void setAdapter() {
        mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            // Set the layout for the view
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_kitchen_group_layout
                        , viewGroup, false);
                view.setBackgroundResource(android.R.drawable.list_selector_background);
                return new CustomViewHolder(view);
            }

            @Override
            // Binds each element to the viewholder
            public void onBindViewHolder(CustomViewHolder viewHolder, int i) {

                // Stores each item.name from a group(with no dublicates)
                Set<String> nodub = new HashSet<>();

                // Stores items that has subitems or notes
                List<Item> specialitems = new ArrayList<>();

                // Stores all the items (with dublicates)
                // This is used to calculate the frequency of a items name
                List<String> occur = new ArrayList<>();

                // Reset the itemname and groupnumber textviews
                viewHolder.itemname.setText("");
                viewHolder.groupnumber.setText("");

                // Print tablenumber
                viewHolder.groupnumber.setText("Bord: " + groups.get(i).tablenum);


                // If the item has subitems or notes, the item is added to specialitems
                // Else add it to occur
                for (Item it : groups.get(i).items) {
                    if (!it.subItems.isEmpty() || !it.notes.isEmpty())
                        specialitems.add(it);
                    else {
                        occur.add(it.name);
                    }
                }

                // For each string in occur, add it to nodub
                // Each item.name will be store just once
                for (String s : occur)
                    nodub.add(s);

                // Appends items name and items with subitem,
                // Items with notes and subitems with notes
                appendItemTextView(nodub, specialitems, occur, viewHolder, groups.get(i));

                // This may or may not be necessary
                viewHolder.groupnumber.setPressed(false);
                viewHolder.itemname.setPressed(false);
            }

            @Override
            public int getItemCount() {
                return groups.size();
            }

        };
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    void appendItemTextView(Set<String> nodub, List<Item> specialitems, List<String> occur, CustomViewHolder viewHolder, Group group) {
        // For each string in dub, check its frequency in the occur list
        for (String itemname : nodub) {
            int occurrences = Collections.frequency(occur, itemname);

            // If occurences is 1, just print the item.name
            // Otherwise, print the frequency and item.name
            if (occurrences == 1) {
                viewHolder.itemname.append("\n" + "   " + itemname);
                Log.e("error", "nodub" + "   " + itemname);
            } else {
                viewHolder.itemname.append("\n" + occurrences + " " + itemname);
                Log.e("error", "nodub" +  occurrences + " " + itemname);
            }

            // Check for any notes for items.
            for (Item item : group.items) {
                // Item type 1 is drink
                if (item.type != 1) {
                    if (specialitems.contains(item)) {
                        int occurrencesspecial = Collections.frequency(specialitems, item);
                        if (occurrencesspecial == 1) {
                            viewHolder.itemname.append("\n" + "   " + itemname);
                            Log.e("error", "special" + "   " + itemname);
                        } else {
                            viewHolder.itemname.append("\n" + occurrencesspecial + " " + itemname);
                            Log.e("error", "special" + occurrencesspecial + " " + itemname);

                        }
                        if (!item.notes.isEmpty()) {
                            viewHolder.itemname.append(": ");
                            for (int j = 0; j < item.notes.size(); j++) {
                                viewHolder.itemname.append(Html.fromHtml("<i><font color=\"#FF0000\">" + item.notes.get(j).text + "</font></i>"));
                                if (j != item.notes.size() - 1)
                                    viewHolder.itemname.append(", ");
                            }
                            for (Item subitem : item.subItems) {
                                viewHolder.itemname.append("\n" + "     ");
                                viewHolder.itemname.append(Html.fromHtml("<i><font color=\"#0000FF\">" + subitem.name + "</font></i>"));
                                if (!subitem.notes.isEmpty())
                                    viewHolder.itemname.append(": ");
                                for (int k = 0; k < subitem.notes.size(); k++) {
                                    viewHolder.itemname.append(Html.fromHtml("<i><font color=\"#FF0000\">" + subitem.notes.get(k).text + "</font></i>"));
                                    if (k != subitem.notes.size() - 1)
                                        viewHolder.itemname.append(", ");
                                }
                            }
                        }
                        specialitems.remove(item);
                    }
                }
            }
        }
    }


    void setSwipeListener() {
        touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView,
                        new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {

                                // When group is swipe, show the undodeletebutton
                                undodeletebtn.setVisibility(View.VISIBLE);

                                // reverseSortedPositions is a List that store the positions of all swiped groups
                                for (int position : reverseSortedPositions) {
                                    // Store the old position
                                    oldpositions.add(position);

                                    // Add the group to deletedgroups
                                    deletedgroups.add(groups.get(position));

                                    // Remove the group and notify the adapter
                                    groups.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }

                                setAdapter();

                                // Check if the animation of button is set
                                // If the animation is set, set the runnable to null (stops the animation)
                                if (animationhandler != null) {
                                    animationhandler.animationthread = null;
                                }
                                // Then start the animation and set timer
                                animationhandler = new AnimationHandler();
                                undodeletebtn.postDelayed(animationhandler, millisecondstoshowbutton);

                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
    }

    class AnimationHandler implements Runnable {
        Runnable animationthread = new Runnable() {
            @Override
            public void run() {
                undodeletebtn.startAnimation(animfadeout);
                undodeletebtn.setVisibility(View.GONE);
                animationthread = null;
                for (Group group : deletedgroups) {
                    group.status = getString(R.string.StatusReadyToServe);
                    Call<Void> call = Utils.getApi(KitchenActivity.this).changeStatus(group, group.orderId, group.id);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Response<Void> response, Retrofit retrofit) {
                            System.out.println("working");
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            System.out.println("not working");

                        }
                    });
                }
            }
        };

        @Override
        public void run() {
            if (animationthread != null) {
                animationthread.run();

            }
        }
    }

    void setScrollListener() {
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // Swipe has no effect
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());

        // Listener for if item (group) is clicked
        // This may not be necessary
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mRecyclerView,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //Toast.makeText(KitchenActivity.this, "Clicked " + groups.get(position), Toast.LENGTH_SHORT).show();
                    }
                }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kitchen, menu);
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


    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView itemname;
        // Change this to tablenumber later
        public TextView groupnumber;

        public CustomViewHolder(View itemView) {
            super(itemView);

            itemname = (TextView) itemView.findViewById(R.id.itemname);
            groupnumber = (TextView) itemView.findViewById(R.id.groupnum);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
        private OnItemClickListener mListener;

        private static final long DELAY_MILLIS = 100;

        private RecyclerView mRecyclerView;
        private GestureDetector mGestureDetector;
        private boolean mIsPrepressed = false;
        private boolean mIsShowPress = false;
        private View mPressedView = null;

        public RecyclerItemClickListener(RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mRecyclerView = recyclerView;
            mGestureDetector = new GestureDetector(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    mIsPrepressed = true;
                    mPressedView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    super.onDown(e);
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {
                    if (mIsPrepressed && mPressedView != null) {
                        mPressedView.setPressed(true);
                        mIsShowPress = true;
                    }
                    super.onShowPress(e);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (mIsPrepressed && mPressedView != null) {
                        mPressedView.setPressed(true);
                        final View pressedView = mPressedView;
                        pressedView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pressedView.setPressed(false);
                            }
                        }, DELAY_MILLIS);
                        mIsPrepressed = false;
                        mPressedView = null;
                    }
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildPosition(childView));
            } else if (e.getActionMasked() == MotionEvent.ACTION_UP && mPressedView != null && mIsShowPress) {
                mPressedView.setPressed(false);
                mIsShowPress = false;
                mIsPrepressed = false;
                mPressedView = null;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }
    }
}
