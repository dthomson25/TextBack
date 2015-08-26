package com.dthomson.textback;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;


public class DisplayTextsFrag  extends android.support.v4.app.Fragment {
    private String FILENAME = "saved_texts";

    private static final String TAG = "RecyclerViewFragment";
    private TextMessageDB dbHelper;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static String numOfPrevSMS = "10";

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
            displaySmsLog();
            return true;
        }

        if (id == R.id.action_add_text_demo) {
            addNewTextMessage(null);
        }

        if (id == R.id.action_clear) {
            deleteAllTexts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySmsLog() {
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
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                TextViewHolder textViewholder = (TextViewHolder) viewHolder;
                String row_ID = textViewholder.rowIDText.getText().toString();
                dbHelper.deleteText(row_ID);
                Cursor allTexts = dbHelper.getAllTexts();
                mAdapter.deleteText(allTexts,swipeDir);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        return rootView;
    }


    public void addNewTextMessage(Cursor results) {
        int count = 0;
        if (mAdapter.getItemCount() != 0) {
            count = mAdapter.getItemCount();
        }
        if (results != null) {
            ArrayList<TextMessage> textsToAdd = cursorToTextMessage(results);
            dbHelper.addTextMessages(textsToAdd);
        } else {
            TextMessage blue = new TextMessage("Demo","Blue","Forget your red!",null,null,null,null);
            dbHelper.addTextMessage(blue);
        }
        Cursor cursor = dbHelper.getAllTexts();
        mAdapter.addTextMessage(cursor, count);
    }

    private ArrayList<TextMessage> cursorToTextMessage(Cursor results) {
        ArrayList<TextMessage> newTexts = new ArrayList<>();
        while (results.moveToNext()) {
            int addressIndex = results.getColumnIndex("address");
            int bodyIndex = results.getColumnIndex("body");
            int dateIndex = results.getColumnIndex("date");
            int threadIdIndex = results.getColumnIndex("THREAD_ID");
            String address = results.getString(addressIndex);
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
            Cursor contactLookup = getActivity().getContentResolver().
                    query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                            ContactsContract.PhoneLookup.PHOTO_FILE_ID}, null, null, null);

            String person = "";
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                person = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
            String body = results.getString(bodyIndex);
            String date = results.getString(dateIndex);
            String threadId = results.getString(threadIdIndex);

            TextMessage text = new TextMessage(address,person,body,threadId,date,null,null);
            newTexts.add(text);
        }
        return newTexts;
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

    private class GetOldSMS extends AsyncTask<Object, Object, Cursor> {
        @Override
        protected Cursor doInBackground(Object... params) {
            Cursor cursor = getActivity().getContentResolver().query(Telephony.Sms.CONTENT_URI, null
                    , null, null , "DATE DESC limit " + numOfPrevSMS);
            ArrayList<String> whereArg = new ArrayList<>();
            HashSet<String> alreadyAdded = new HashSet<>();
            while (cursor.moveToNext()) {
                int threadIdIndex = cursor.getColumnIndex("THREAD_ID");
                int dateIndex = cursor.getColumnIndex("date");
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
            where = where + "1 = 0";
            return getActivity().getContentResolver().
                    query(Telephony.Sms.Inbox.CONTENT_URI, null, where,
                            whereArg.toArray(new String[whereArg.size()]), "DATE DESC");
        }

        @Override
        protected void onPostExecute(Cursor result) {
            addNewTextMessage(result);
        }
    }
}