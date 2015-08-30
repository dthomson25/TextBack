package com.dthomson.textback;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

//TODO add date column

import java.util.ArrayList;

public class TextMessageDB {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PERSON = "person";
    public static final String KEY_LAST_TEXT = "last_text";
    public static final String KEY_DATE = "date";
    public static final String KEY_THREAD_ID = "thread_id";
    public static final String KEY_PICTURE_ID = "picture_id";
    public static final String KEY_PICTURE_DATA = "picture_data";

    private static final String TAG = "TextDbAdapter";
    private DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "Text Messages";
    private static final String SQLITE_TABLE = "Conversations";
    private static final int DATABASE_VERSION = 3;
    private final Context mCtx;


    private static final String DATABASE_CREATE =
     "CREATE TABLE if not exists " + SQLITE_TABLE +  "(" +
             KEY_ROWID + " integer PRIMARY KEY autoincrement," +
             KEY_ADDRESS + "," +
             KEY_PERSON + "," +
             KEY_LAST_TEXT + "," +
             KEY_DATE + "," +
             KEY_THREAD_ID + "," +
             KEY_PICTURE_ID + "," +
             KEY_PICTURE_DATA + ");";





    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

    public TextMessageDB(Context ctx) {
        this.mCtx = ctx;
    }

    public TextMessageDB open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public void addTextMessages(ArrayList<TextMessage> textsToAdd) {
        for(TextMessage text : textsToAdd) {
            addTextMessage(text);
        }
    }


    public static long addTextMessage(TextMessage text) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ADDRESS, text.getAddress());
        initialValues.put(KEY_PERSON, text.getPerson());
        initialValues.put(KEY_LAST_TEXT, text.getLastText());
        initialValues.put(KEY_DATE, text.getDate());
        initialValues.put(KEY_THREAD_ID, text.getThreadId());
        initialValues.put(KEY_PICTURE_ID, text.getPictureID());
        initialValues.put(KEY_PICTURE_DATA, text.getPicture_Data());
        Log.w(TAG,Boolean.toString(mDb == null));

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    public int deleteText(String rowID) {
        String selection = KEY_ROWID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowID) };
        // Issue SQL statement.
        return mDb.delete(SQLITE_TABLE, selection, selectionArgs);
    }


    public boolean deleteAllTexts() {

        int doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public static Cursor getAllTexts() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_ADDRESS, KEY_PERSON, KEY_LAST_TEXT, KEY_DATE, KEY_THREAD_ID,
                        KEY_PICTURE_ID, KEY_PICTURE_DATA},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public static void insertSomeTexts() {
        TextMessage red = new TextMessage("","Red","I'm da best","-1","0",null,null);
        TextMessage yellow = new TextMessage("","Yellow","Pika!","-1","0",null,null);
        TextMessage green = new TextMessage("","Green","I'm important too!","-1","0",null,null);
        addTextMessage(red);
        addTextMessage(yellow);
        addTextMessage(green);
    }

}