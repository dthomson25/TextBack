package com.dthomson.textback.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.PowerManager;
import android.provider.Telephony;
import android.widget.Toast;

import com.dthomson.textback.TextMessageDB;

import java.util.ArrayList;
import java.util.HashMap;

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
        addNewTexts(isAppRunning, context);

        isRunningString = isRunningString + Boolean.toString(isAppRunning);
        Toast.makeText(context, isRunningString, Toast.LENGTH_LONG).show();

        //Release the lock
        wl.release();

    }

    private void addNewTexts(boolean isAppRunning, Context context) {
        TextMessageDB dbHelper = new TextMessageDB(context);
        dbHelper.open();
        removeOldSMS(context,dbHelper);
        addToDB(context, dbHelper);
        if(isAppRunning) {
            refreshAdapter(context,dbHelper);
        }
        dbHelper.close();
    }

    private void addToDB(Context context, TextMessageDB dbHelper) {

    }

    private void refreshAdapter(Context context, TextMessageDB dbHelper) {
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
        dbHelper.close();
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
