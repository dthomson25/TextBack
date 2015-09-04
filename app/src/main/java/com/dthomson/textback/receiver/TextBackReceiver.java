package com.dthomson.textback.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.widget.Toast;

import com.dthomson.textback.DisplayTextsFrag;
import com.dthomson.textback.MyRecyclerAdapter;
import com.dthomson.textback.R;
import com.dthomson.textback.TextMessage;
import com.dthomson.textback.TextMessageDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TextBackReceiver extends BroadcastReceiver {
    public TextBackReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TEXT_BACK_RECEIVER");
        //Acquire the lock
        wl.acquire();
        String isRunningString = "is App running: ";
        boolean isAppRunning = MyLifecycleHandler.isApplicationInForeground();
        TextMessageDB dbHelper = new TextMessageDB(context);
        dbHelper.open();
        addNewTexts(isAppRunning, context, dbHelper);
        addNotifications(context, dbHelper);
        dbHelper.close();
        isRunningString = isRunningString + Boolean.toString(isAppRunning);
        Toast.makeText(context, isRunningString, Toast.LENGTH_LONG).show();

        //Release the lock
        wl.release();

    }

    private void addNotifications(Context context, TextMessageDB dbHelper) {


    }

    private void addNewTexts(boolean isAppRunning, Context context, TextMessageDB dbHelper) {
        removeOldSMS(context, dbHelper);
        addToDB(context, dbHelper);
        if(isAppRunning) {
            refreshAdapter(context,dbHelper);
        }
    }

    private void addToDB(Context context, TextMessageDB dbHelper) {
        HashSet<String> alreadyAddedthreadIDs = getAlreadyAddedSMS(dbHelper);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),context.MODE_PRIVATE);
        String lastUpdate = sharedPreferences.getString(context.getString(R.string.last_update), "0");
        Cursor possResults = context.getContentResolver().query(Telephony.Sms.CONTENT_URI,
                null, "DATE > ?", new String[]{lastUpdate}, "DATE DESC limit 10");
        //TODO find any to get numOfPrevSMS in the above query instead of 10
        ArrayList<String> whereArg = new ArrayList<>();
        HashSet<String> alreadyAdded = new HashSet<>();
        Boolean firstText = true;
        while (possResults.moveToNext()) {
            int threadIdIndex = possResults.getColumnIndex(Telephony.Sms.THREAD_ID);
            int dateIndex = possResults.getColumnIndex(Telephony.Sms.DATE);
            String thread = possResults.getString(threadIdIndex);
            String date = possResults.getString(dateIndex);
            if (firstText) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(context.getString(R.string.last_update), date);
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
        Cursor results = context.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, null, where,
                        whereArg.toArray(new String[whereArg.size()]), "DATE DESC");
        if (results != null) {
            ArrayList<TextMessage> textsToAdd = cursorToTextMessage(results,context);
            dbHelper.addTextMessages(textsToAdd);
        }

    }

    private HashSet<String> getAlreadyAddedSMS(TextMessageDB dbHelper) {
        Cursor allTexts = dbHelper.getAllTexts();
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
        return  alreadyAddedthreadIDs;
    }

    private ArrayList<TextMessage> cursorToTextMessage(Cursor results, Context context) {
        ArrayList<TextMessage> newTexts = new ArrayList<>();
        while (results.moveToNext()) {
            int addressIndex = results.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyIndex = results.getColumnIndex(Telephony.Sms.BODY);
            int dateIndex = results.getColumnIndex(Telephony.Sms.DATE);
            int threadIdIndex = results.getColumnIndex(Telephony.Sms.THREAD_ID);
            String address = results.getString(addressIndex);
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
            Cursor contactLookup = context.getContentResolver().
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


    private void refreshAdapter(Context context, TextMessageDB dbHelper) {
        //TODO implement refresh adapter when adding new texts
    }

    private void removeOldSMS(Context context, TextMessageDB dbHelper) {
        Cursor allStoredTexts = dbHelper.getAllTexts();
        ArrayList<String> whereArg = new ArrayList<>();
        HashMap<String, String> threadToRowID = new HashMap<>();
        if (!allStoredTexts.isFirst()) {
            allStoredTexts.moveToFirst();
        }
        do {
            String rowID = allStoredTexts.getString(allStoredTexts.getColumnIndex(dbHelper.KEY_ROWID));
            String date = allStoredTexts.getString(allStoredTexts.getColumnIndex(dbHelper.KEY_DATE));
            String thread = allStoredTexts.getString(allStoredTexts.getColumnIndex(dbHelper.KEY_THREAD_ID));
            whereArg.add(thread);
            whereArg.add(date);
            threadToRowID.put(thread, rowID);
        } while (allStoredTexts.moveToNext());

        String where = "";
        for (int i = 0; i < whereArg.size() / 2; i++) {
            where = where + "(THREAD_ID = ? and DATE > ? ) or ";
        }
        where = where + "1 = 0";
        removedTextBacks(where, whereArg, threadToRowID, context, dbHelper);
    }

    private void removedTextBacks(String where, ArrayList<String> whereArg, HashMap<String, String> 
            threadToRowID, Context context, TextMessageDB dbHelper) {
        Cursor newTexts = context.getContentResolver().
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
            } while (newTexts.moveToNext());
        }
    }

    public void SetAlarm(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TextBackReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 30 seconds
        //TODO change time here
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi);
        Toast.makeText(context,"Alarm set",Toast.LENGTH_SHORT).show();
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, TextBackReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Toast.makeText(context, "Alarm canceled", Toast.LENGTH_SHORT).show();
    }
    
    
}
