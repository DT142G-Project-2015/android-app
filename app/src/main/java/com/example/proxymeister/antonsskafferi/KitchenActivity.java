package com.example.proxymeister.antonsskafferi;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proxymeister.antonsskafferi.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class KitchenActivity extends Activity {
    private List<String> orders = new ArrayList<>();
    private List<String> deletedorders = new ArrayList<>();
    private List<String> strings;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter<CustomViewHolder> mAdapter;
    SwipeDismissRecyclerViewTouchListener touchListener;

    //private ArrayAdapter<String> mAdapter;
    int oldposition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);


        Call<List<Order>> call = Utils.getApi().getOrdersByStatus("readyForKitchen");

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Response<List<Order>> response, Retrofit retrofit) {

                int statusCode = response.code();
                Log.i(MainActivity.class.getName(), "Status: " + statusCode);

                List<Order> orders = response.body();

                if (orders != null) {

                    // strings = orders.map(_.toString())
                    strings = new ArrayList<>();
                    for (Order o : orders) {
                        strings.add(o.toStringKitchenFormat());
                    }


                    mRecyclerView = (RecyclerView) findViewById(R.id.ordersRecyclerView);
                    mLayoutManager = new LinearLayoutManager(KitchenActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    setAdapter();
                    setSwipeListener();
                    setScrollListener();

/*
                        // create simple ArrayAdapter to hold the strings for the ListView
                        ArrayAdapter<String> ordersAdapter =
                                new ArrayAdapter<String>(KitchenActivity.this, android.R.layout.simple_list_item_1, strings);

                        // pass the adapter to the ListView
                        ListView list = (ListView) findViewById(R.id.ordersListView);
                        list.setAdapter(ordersAdapter);
                    */
                    }
                }

                @Override
                public void onFailure (Throwable t){
                    Log.i(MainActivity.class.getName(), "Failed to fetch data: " + t.getMessage());
                }
            }

            );

        /*
        for (Databas.Order o : Databas.getInstance().orders) {
            orders.add(o.text);
        }


        // Find the ListView in activity_kitchen
        mListView = (ListView) findViewById(R.id.ordersListView);

        // ListAdapter acts as a bridge between the data and each ListItem
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, orders);

        // Tells the ListView to use adapter to display its content
        mListView.setAdapter(mAdapter);
        */

            // Undo delete button
      /*      Button deletebtn = (Button) findViewById(R.id.undodeletebutton);

            View.OnClickListener oclbtn = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!deletedorders.isEmpty()) {
                        String item = deletedorders.get(deletedorders.size() - 1);
                        orders.add(item);
                    /*
                    if(orders.get(oldposition) == null)
                        orders.add(item);
                    else
                    {
                        orders.add("");
                        for(int i = orders.size()-1; i > oldposition; i++ )
                        {
                            String temp = orders.get(i);

                            orders.add(oldposition, item);
                        }
                    }

                    */
        /*

                        deletedorders.remove(item);
                        mAdapter = new ArrayAdapter<>(KitchenActivity.this,
                                android.R.layout.simple_list_item_1, orders);

                        mListView.setAdapter(mAdapter);
                        Databas.Order o = new Databas.Order();
                        o.text = item;
                        Databas.getInstance().orders.add(o);
                    }
                }
            };

            deletebtn.setOnClickListener(oclbtn);
    */

        }

    void setAdapter()
    {
        mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1
                        , viewGroup, false);
                view.setBackgroundResource(android.R.drawable.list_selector_background);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CustomViewHolder viewHolder, int i) {
                viewHolder.mTextView.setText(strings.get(i));
                viewHolder.mTextView.setPressed(false);
            }

            @Override
            public int getItemCount() {
                return strings.size();
            }
        };
        mRecyclerView.setAdapter(mAdapter);


    }
    void setSwipeListener()
    {
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
                                for (int position : reverseSortedPositions) {
                                    final int originalHeight = recyclerView.getHeight();

                                    //orders.remove(position);
                                    strings.remove(position);
                                }

                                setAdapter();
                                // do not call notifyItemRemoved for every item, it will cause gaps on deleting items
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
    }

    void setScrollListener()
    {
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mRecyclerView,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(KitchenActivity.this, "Clicked " + strings.get(position), Toast.LENGTH_SHORT).show();
                    }
                }));
    }
/*
    @Override
    public ListView getListView() {
        return mListView;
    }

    // See SwipeListViewActivity
    @Override
    public void deleteSwipedItem(boolean isLeft, int position) {

        if(isLeft)
        {
            String item = orders.get(position);
            deletedorders.add(item);
            orders.remove(item);
            mAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, orders);
            mListView.setAdapter(mAdapter);
            Databas.getInstance().orders.remove(position);
           // oldposition = position;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kitchen, menu);
        return true;
    }

    @Override
    // Single tap on each item.
    public void onItemClickListener(ListAdapter adapter, int position) {
        //Toast.makeText(this, "Single tap on item position " + position,
         //       Toast.LENGTH_SHORT).show();
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

*/

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public CustomViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
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


