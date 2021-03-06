package com.dthomson.textback;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
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

import com.dthomson.textback.receiver.TextBackReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class DisplayTextsFrag  extends android.support.v4.app.Fragment {
    private String FILENAME = "saved_texts";
    private TextBackReceiver alarm;

    private static final String TAG = "RecyclerViewFragment";
    private TextMessageDB dbHelper;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private static String numOfPrevSMS = "20";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarm = new TextBackReceiver();
        dbHelper = new TextMessageDB(getActivity().getApplicationContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_display_frag_demo, menu);
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
            Toast.makeText(getActivity(),"Added texts that need textbacks",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_add_text_demo) {
            addNewTextMessage(null);
            return true;
        }

        if (id == R.id.action_clear) {
            deleteAllTexts();
            return true;
        }

        if (id == R.id.action_set_alarm) {
            setAlarm();
            return true;
        }

        if (id == R.id.action_cancel_alarm) {
            cancelAlarm();
            return true;
        }

        if (id == R.id.action_remove_old) {
            removeOldSms();
            Toast.makeText(getActivity(), "Removed Old Texts", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_reset_date) {
            resetDate();
            Toast.makeText(getActivity(), "Reset Date", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetDate() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(this.getString(R.string.last_update),"0");
        editor.commit();
    }

    private void cancelAlarm() {
        alarm.CancelAlarm(getActivity().getApplicationContext());
    }

    private void setAlarm() {
        alarm.SetAlarm(getActivity().getApplicationContext());
    }

    private void removeOldSms() {
        dbHelper.open();
        Cursor allStoredTexts = dbHelper.getAllTexts();
        ArrayList<String> whereArg = new ArrayList<>();
        HashMap<String,String> threadToRowID = new HashMap<>();
        if(!allStoredTexts.isFirst()) {
            allStoredTexts.moveToFirst();
        }
        do {
            String rowID = allStoredTexts.getString(allStoredTexts.getColumnIndex(dbHelper.KEY_ROWID));
            String date = allStoredTexts.getString(allStoredTexts.getColumnIndex(dbHelper.KEY_DATE));
            String thread = allStoredTexts.getString(allStoredTexts.getColumnIndex(dbHelper.KEY_THREAD_ID));
            whereArg.add(thread);
            whereArg.add(date);
            threadToRowID.put(thread,rowID);
        } while (allStoredTexts.moveToNext());

        String where = "";
        for (int i = 0; i < whereArg.size()/2;i++) {
            where = where + "(THREAD_ID = ? and DATE > ? ) or ";
        }
        where = where + "1 = 0";
        Cursor newTexts = getActivity().getContentResolver().
                query(Telephony.Sms.CONTENT_URI, null, where,
                        whereArg.toArray(new String[whereArg.size()]), "DATE DESC");

        if (newTexts.getCount() > 0) {
            if (!newTexts.isFirst()) {
                newTexts.moveToFirst();
            }
            do {
                String threadToDelete = newTexts.getString(
                        newTexts.getColumnIndex(Telephony.Sms.THREAD_ID));
                String rowIDToDelete = threadToRowID.get(threadToDelete);
                dbHelper.deleteText(rowIDToDelete);
                Cursor allTexts = dbHelper.getAllTexts();
                mAdapter.deleteText(allTexts,-1);

            } while (newTexts.moveToNext());
        }
        dbHelper.close();
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
        dbHelper.open();
        Cursor cursor = dbHelper.getAllTexts();
        dbHelper.close();
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
                dbHelper.open();
                dbHelper.deleteText(row_ID);
                Cursor allTexts = dbHelper.getAllTexts();
                dbHelper.close();
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
        dbHelper.open();
        if (results != null) {
            ArrayList<TextMessage> textsToAdd = cursorToTextMessage(results);
            dbHelper.addTextMessages(textsToAdd);
            Cursor cursor = dbHelper.getAllTexts();
            mAdapter.addMultipleTexts(cursor,textsToAdd.size());
        } else {
            TextMessage blue = new TextMessage("","Blue","Forget your red!","-1","0",null,null);
            dbHelper.addTextMessage(blue);
            Cursor cursor = dbHelper.getAllTexts();
            mAdapter.addTextMessage(cursor, count);
        }
        dbHelper.close();
    }

    private ArrayList<TextMessage> cursorToTextMessage(Cursor results) {
        ArrayList<TextMessage> newTexts = new ArrayList<>();
        while (results.moveToNext()) {
            int addressIndex = results.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyIndex = results.getColumnIndex(Telephony.Sms.BODY);
            int dateIndex = results.getColumnIndex(Telephony.Sms.DATE);
            int threadIdIndex = results.getColumnIndex(Telephony.Sms.THREAD_ID);
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
        dbHelper.open();
        dbHelper.insertSomeTexts();
        Cursor cursor = dbHelper.getAllTexts();
        mAdapter.defaultCards(cursor);
        dbHelper.close();

    }

    public void deleteAllTexts() {
        mAdapter.clearTexts();
        dbHelper.open();
        dbHelper.deleteAllTexts();
        Cursor emptyCursor = dbHelper.getAllTexts();
        dbHelper.close();
        mAdapter.changeCursor(emptyCursor);
    }

    private class GetOldSMS extends AsyncTask<Object, Object, Cursor> {
        @Override
        protected Cursor doInBackground(Object... params) {
            dbHelper.open();
            Cursor allTexts = dbHelper.getAllTexts();
            dbHelper.close();
            HashSet<String> alreadyAddedthreadIDs = new HashSet<>();
            if ( allTexts.getCount() > 0 ) {
                if (!allTexts.isFirst()) {
                    allTexts.moveToFirst();
                }
                do {
                    String threadID = allTexts.getString(allTexts.getColumnIndex(dbHelper.KEY_THREAD_ID));
                    alreadyAddedthreadIDs.add(threadID);
                } while (allTexts.moveToNext());
            }
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                    getString(R.string.preference_file_key),Context.MODE_PRIVATE);
            String lastUpdate = sharedPreferences.getString(getString(R.string.last_update), "0");

            Cursor cursor = getActivity().getContentResolver().query(Telephony.Sms.CONTENT_URI,
                        null, "DATE > ?", new String[] {lastUpdate}, "DATE DESC limit " + numOfPrevSMS);
            ArrayList<String> whereArg = new ArrayList<>();
            HashSet<String> alreadyAdded = new HashSet<>();
            Boolean firstText = true;
            while (cursor.moveToNext()) {
                int threadIdIndex = cursor.getColumnIndex(Telephony.Sms.THREAD_ID);
                int dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE);
                String thread = cursor.getString(threadIdIndex);
                String date = cursor.getString(dateIndex);
                if (firstText) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.last_update), date);
                    editor.commit();
                    firstText = false;
                }
                if (!alreadyAdded.contains(thread) && !alreadyAddedthreadIDs.contains(thread)) {
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