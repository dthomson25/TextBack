package com.dthomson.textback;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


/**
 * Created by dthomson on 8/9/2015.
 */
public class TextMessageDB {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LAST_TEXT = "last_text";
    public static final String KEY_THREAD_ID = "thread_id";
    public static final String KEY_PICTURE_ID = "picture_id";
    public static final String KEY_PICTURE_DATA = "picture_data";

    private static final String TAG = "TextDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "Text Messages";
    private static final String SQLITE_TABLE = "Conversations";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;


    private static final String DATABASE_CREATE =
     "CREATE TABLE if not exists " + SQLITE_TABLE +  "(" +
             KEY_ROWID + " integer PRIMARY KEY autoincrement," +
             KEY_ADDRESS + "," +
             KEY_LAST_TEXT + "," +
             KEY_THREAD_ID + "," +
             KEY_PICTURE_ID + "," +
             KEY_PICTURE_DATA + ");";

    public void deleteText(RecyclerView.ViewHolder viewHolder, int swipeDir) {

    }


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
    public long addTextMessage(TextMessage text) {
        return addTextMessage(text.getAddress(),
                text.getThreadId(),
                text.getLastText(),
                text.getPictureID(),
                text.getPicture_Data());
    }

    public long addTextMessage(String address, String threadId, String lastText,
                                  String pictureID, String pictureData) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ADDRESS, address);
        initialValues.put(KEY_LAST_TEXT, lastText);
        initialValues.put(KEY_THREAD_ID, threadId);
        initialValues.put(KEY_PICTURE_ID, pictureID);
        initialValues.put(KEY_PICTURE_DATA, pictureData);
        Log.w(TAG,Boolean.toString(mDb == null));

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    public boolean deleteAllTexts() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public Cursor getAllTexts() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_ADDRESS, KEY_LAST_TEXT, KEY_THREAD_ID, KEY_PICTURE_ID, KEY_PICTURE_DATA},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void insertSomeTexts() {
        addTextMessage("Red", null, "I'm da best", null, null);
        addTextMessage("Green", null, "I'm important too!!", null, null);
        addTextMessage("Yellow", null, "Piku", null, null);
    }

}