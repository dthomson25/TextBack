package com.dthomson.textback;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String FILENAME = "saved_texts";
    private MyRecyclerAdapter myRecyclerAdapter;
    private TextMessageDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new TextMessageDB(this);
        dbHelper.open();
        Cursor c = dbHelper.getAllTexts();
        myRecyclerAdapter= new MyRecyclerAdapter(this,c);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerList);
        LinearLayoutManager linearLM = new LinearLayoutManager(this);
        linearLM.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLM);
        recyclerView.setAdapter(myRecyclerAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {

        super.onSaveInstanceState(savedState);

//        // Note: getValues() is a method in your ArrayAdaptor subclass
//        List<TextMessage> values = myRecyclerAdapter.getTexts();
//        ArrayList<TextMessage> valueToSave = new ArrayList<>(values);
//        savedState.putParcelableArrayList("myTextMessages", valueToSave);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void addNewTextMessage() {
        TextMessage text = new TextMessage("Blue","Fuck you blue");
        int count = 0;
        if(myRecyclerAdapter.getItemCount() != 0) {
            count = myRecyclerAdapter.getItemCount();
        }
        dbHelper.addTextMessage(text);
        Cursor cursor = dbHelper.getAllTexts();
        myRecyclerAdapter.addTextMessage(cursor, count);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_default) {
            dbHelper.deleteAllTexts();
            Cursor emptyCursor = dbHelper.getAllTexts();
            myRecyclerAdapter.clearTexts(emptyCursor);
            dbHelper.insertSomeTexts();
            Cursor cursor = dbHelper.getAllTexts();
            myRecyclerAdapter.defaultCards(cursor);
            return true;
        }

        if (id == R.id.action_add_text) {
            addNewTextMessage();
            return true;
        }

        if (id == R.id.action_clear) {
            dbHelper.deleteAllTexts();
            Cursor cursor = dbHelper.getAllTexts();
            myRecyclerAdapter.clearTexts(cursor);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

}
