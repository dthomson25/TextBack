package com.dthomson.textback;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            ArrayList<TextMessage> values = savedInstanceState.getParcelableArrayList("myTextMessages");
            if (values != null) {
                myRecyclerAdapter = new MyRecyclerAdapter(values);
            }
        } else {
            myRecyclerAdapter = new MyRecyclerAdapter(generateBooks());
        }
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

        // Note: getValues() is a method in your ArrayAdaptor subclass
        List<TextMessage> values = myRecyclerAdapter.getTexts();
        ArrayList<TextMessage> valueToSave = new ArrayList<>(values);
        savedState.putParcelableArrayList("myTextMessages", valueToSave);

    }

    private ArrayList<TextMessage> generateBooks() {
        ArrayList<TextMessage> texts = new ArrayList<>();
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        texts.add(new TextMessage("RED", "#Hi Red, how are you??"));
        return texts;
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
        myRecyclerAdapter.addData(text, count);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_default) {
            myRecyclerAdapter.defaultCards();
            return true;
        }

        if (id == R.id.action_add_text) {
            addNewTextMessage();
            return true;
        }
        if (id == R.id.action_clear) {
            myRecyclerAdapter.clearTexts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
//        List<TextMessage> values = myRecyclerAdapter.getTexts();
//        ArrayList<TextMessage> valueToSave = new ArrayList<>(values);
//        String FILENAME = "hello_file";
//        FileOutputStream outputStream;
//        try {
//            outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//            for (TextMessage text: values) {
//                String writeOutStr = text.toString();
//                outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//                outputStream.write(writeOutStr.getBytes());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        outputStream.close();


    }

}
