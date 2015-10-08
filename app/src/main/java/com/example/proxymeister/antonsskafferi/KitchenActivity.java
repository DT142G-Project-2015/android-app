package com.example.proxymeister.antonsskafferi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
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

import android.widget.Toast;

import java.util.Timer;

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
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class KitchenActivity extends AppCompatActivity {
    private List<Order> orders;
    private List<Group> groups = new ArrayList<>();
    private List<Group> deletedgroups = new ArrayList<>();


    private RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter<CustomViewHolder> mAdapter;
    private Button undodeletebtn;
    private Animation animfadeout;
    private int millisecondstoshowbutton;
    private List<Integer> oldpositions = new ArrayList<>();
    SwipeDismissRecyclerViewTouchListener touchListener;
    private ComponentRemover componentRemover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);


        undodeletebtn = (Button) findViewById(R.id.undodeletebutton);
        animfadeout = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        animfadeout.setDuration(500);


        millisecondstoshowbutton = 5000;

        mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
        mLayoutManager = new LinearLayoutManager(KitchenActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(KitchenActivity.this, DividerItemDecoration.VERTICAL_LIST));


        Call<List<Order>> call = Utils.getApi().getOrdersByStatus("readyForKitchen");
        if (call != null)
            call.enqueue(new Callback<List<Order>>() {
                             @Override
                             public void onResponse(Response<List<Order>> response, Retrofit retrofit) {

                                 int statusCode = response.code();
                                 Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                                 orders = response.body();


                                 if (orders != null) {

                                     // strings = orders.map(_.toString())

                                     for (int i = 0; i < orders.size(); i++) {
                                         List<Group> temp = orders.get(i).groups;
                                         for (int j = 0; j < temp.size(); j++) {
                                             groups.add(temp.get(j));

                                         }
                                     }


                                     setAdapter();
                                     setSwipeListener();
                                     setScrollListener();


                                 }
                             }

                             @Override
                             public void onFailure(Throwable t) {
                                 Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                             }
                         }

            );

        // Listener for the undo button.
        // When pressed, removed groups is fetched from deletedgroups and inserted
        // at the old position
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!deletedgroups.isEmpty()){
                    Group g = deletedgroups.get(deletedgroups.size()-1);
                    Group gtemp = deletedgroups.get(deletedgroups.size()-1);
                    groups.add(oldpositions.get(oldpositions.size()-1), gtemp);
                    deletedgroups.remove(deletedgroups.size() - 1);
                    oldpositions.remove(oldpositions.size()-1);
                    setAdapter();
                }

            }
        };
        undodeletebtn.setOnClickListener(buttonListener);

    }

    void setAdapter() {
        mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_kitchen_group_layout
                        , viewGroup, false);
                view.setBackgroundResource(android.R.drawable.list_selector_background);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CustomViewHolder viewHolder, int i) {

                Map<Item, Integer> frequency = new HashMap<>();

                viewHolder.itemname.setText("");
                viewHolder.groupnumber.setText("");
                // This should be table number instead of group id
                viewHolder.groupnumber.setText("Bord: " + groups.get(i).getId());

                List<String> occur = new ArrayList<>();
                for (Item it : groups.get(i).items) {
                    occur.add(it.name);
                }

                Set<String> nodub = new HashSet<>();

                for (String s : occur)
                    nodub.add(s);

                for (String name : nodub) {
                    int occurrences = Collections.frequency(occur, name);

                    if (occurrences == 1)
                        viewHolder.itemname.append("\n" + "   " + name);
                    else
                        viewHolder.itemname.append("\n" + occurrences + " " + name);
                }


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

                                undodeletebtn.setVisibility(View.VISIBLE);
                                for (int position : reverseSortedPositions) {
                                    oldpositions.add(position);

                                    Group gtemp = groups.get(position);
                                    deletedgroups.add(gtemp);

                                    //groups.get(position).items.clear();
                                    groups.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }




                                setAdapter();
                                // do not call notifyItemRemoved for every item, it will cause gaps on deleting items
                                if(componentRemover != null){
                                    componentRemover.r = null;
                                }
                                componentRemover = new ComponentRemover();
                                // After millisecondstoshowbutton has ended, status should change to readyFordelivery
                                undodeletebtn.postDelayed(componentRemover, millisecondstoshowbutton);

                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
    }

    class ComponentRemover implements Runnable{
        Runnable r = new Runnable() {
            @Override
            public void run() {
                undodeletebtn.startAnimation(animfadeout);
                undodeletebtn.setVisibility(View.GONE);
                r = null;
            }
        };
        @Override
        public void run(){
            if ( r != null ) {
                r.run();

            }
        }

    }
    void setScrollListener() {
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());


        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mRecyclerView,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        setAdapter();
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


