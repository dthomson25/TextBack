package com.dthomson.textback;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Telephony;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class DisplayTextsFrag  extends android.support.v4.app.Fragment {
    private String FILENAME = "saved_texts";

    private static final String TAG = "RecyclerViewFragment";
    //    private Cursor currentCursor;
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

        if (id == R.id.action_default) {
            defaultTexts();
            return true;
        }

        if (id == R.id.action_add_text) {
//            addNewTextMessage();
            displaySmsLog();
            return true;
        }

        if (id == R.id.action_clear) {
            deleteAllTexts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySmsLog() {
        //Cursor cursor = managedQuery(allMessages, null, null, null, null); Both are same
        new GetOldSMS().execute((Object[]) null);
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
        mAdapter = new MyRecyclerAdapter(getActivity(), cursor);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }
//
//    private OnItemSelectedListener mListener;
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnItemSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
//        }
//    }

    public void addNewTextMessage() {
        TextMessage text = new TextMessage("Blue", "Forget you Red");
        int count = 0;
        if (mAdapter.getItemCount() != 0) {
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
//    public interface OnItemSelectedListener {
//        public void onTextSelected(TextMessage textMessage);
//    }

    private class GetOldSMS extends AsyncTask<Object, Object, Cursor> {
        @Override
        protected Cursor doInBackground(Object... params) {
            Cursor cursor = getActivity().getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null , "DATE DESC limit 10");
            ArrayList<String> whereArg = new ArrayList<String>();
            HashSet<String> alreadyAdded = new HashSet<String>();
            while (cursor.moveToNext()) {

                int threadIdIndex = cursor.getColumnIndex("THREAD_ID");
                int dateIndex = cursor.getColumnIndex("date");
                String creator = cursor.getString(cursor.getColumnIndex("Type"));
                Log.d("Last 5 texts", cursor.getString(cursor.getColumnIndex("BODY"))
                    + " " + cursor.getString(threadIdIndex) + " From "  + creator);

                String thread = cursor.getString(threadIdIndex);
                String date = cursor.getString(dateIndex);
                if (!alreadyAdded.contains(thread)) {
                    whereArg.add(thread);
                    whereArg.add(date);
                    alreadyAdded.add(thread);
                }
            }
            String where = "";
            for (String id : alreadyAdded) {
                where = where + "(THREAD_ID = ? and DATE = ? ) or ";
            }
            Log.d("Where Arg: ",Integer.toString(alreadyAdded.size()));
            where = where + "1 = 0";
            return getActivity().getContentResolver().
                    query(Telephony.Sms.Inbox.CONTENT_URI, null, where,
                            whereArg.toArray(new String[whereArg.size()]), "DATE DESC");
        }

        @Override
        protected void onPostExecute(Cursor result) {
            while (result.moveToNext()) {
                int index = result.getColumnIndex("body");
                Log.d("Text", result.getString(index)+ " " + result.getString(result.getColumnIndex("THREAD_ID")));
            }
            Toast.makeText(getActivity(),"DONE!"+Integer.toString(result.getCount()),Toast.LENGTH_LONG).show();
        }
    }
}