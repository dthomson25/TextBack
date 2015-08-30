package com.dthomson.textback;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

//TODO set date of last update
//TODO Add settings menu
//TODO add a demo mode with more controls.
//TODO add privacy settings (what to preview)
//TODO add preferences that can change the behavior of the app (syncs every ___)
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            DisplayTextsFrag displayFrag = new DisplayTextsFrag();
            transaction.replace(R.id.current_fragment, displayFrag);
            transaction.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {

        super.onSaveInstanceState(savedState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

}
