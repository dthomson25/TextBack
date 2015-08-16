package com.dthomson.textback;

import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dthomson.textback.interfaces.OnStartDragListener;
import com.dthomson.textback.interfaces.ItemTouchHelperViewHolder;


public class DisplayTextsFrag  extends android.support.v4.app.Fragment
        implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;

    private String FILENAME = "saved_texts";

    private static final String TAG = "RecyclerViewFragment";
    private TextMessageDB dbHelper;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerAdapter mAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new TextMessageDB(getActivity().getApplicationContext());
        dbHelper.open();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_display_frag, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_default) {
            defaultTexts();
            return true;
        }

        if (id == R.id.action_add_text) {
            addNewTextMessage();
            return true;
        }

        if (id == R.id.action_clear) {
            deleteAllTexts();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_display_texts, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerList);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        Cursor cursor = dbHelper.getAllTexts();
        mAdapter = new MyRecyclerAdapter(getActivity(),cursor,this);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            public static final float ALPHA_FULL = 1.0f;

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Fade out the view as it is swiped out of the parent's bounds
                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                // We only want the active item to change
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    if (viewHolder instanceof ItemTouchHelperViewHolder) {
                        // Let the view holder know that this item is being moved or dragged
                        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                        itemViewHolder.onItemSelected();
                    }
                }

                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                viewHolder.itemView.setAlpha(ALPHA_FULL);

                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    // Tell the view holder it's time to restore the idle state
                    ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                    itemViewHolder.onItemClear();
                }
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
                if (source.getItemViewType() != target.getItemViewType()) {
                    return false;
                }

                // Notify the adapter of the move
                mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                // Notify the adapter of the dismissal
                mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        };
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        FloatingActionButton myFab = (FloatingActionButton)  rootView.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addNewTextMessage();
            }
        });

        return rootView;
    }

    public void addNewTextMessage() {
        TextMessage text = new TextMessage("Blue","Forget you Red");
        int count = 0;
        if(mAdapter.getItemCount() != 0) {
            count = mAdapter.getItemCount();
        }
        dbHelper.addTextMessage(text);
        Cursor cursor = dbHelper.getAllTexts();
        mAdapter.addTextMessage(cursor, count);
    }

    public void defaultTexts() {
        deleteAllTexts();
        dbHelper.insertSomeTexts();
        Cursor cursor = dbHelper.getAllTexts();
        mAdapter.defaultCards(cursor);
    }

    public void deleteAllTexts() {
        mAdapter.clearTexts();
        dbHelper.deleteAllTexts();
        Cursor emptyCursor = dbHelper.getAllTexts();
        mAdapter.changeCursor(emptyCursor);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void deleteText(int position) {
        dbHelper.deleteText(position);
    }
//    public interface OnItemSelectedListener {
//        public void onTextSelected(TextMessage textMessage);
//    }

}
