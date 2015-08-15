package com.dthomson.textback;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.dthomson.textback.interfaces.OnTextClickListener;

public class MainActivity extends AppCompatActivity
        implements OnTextClickListener {
    private DisplayTextsFrag displayFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            DisplayTextsFrag fragment = new DisplayTextsFrag();
            displayFrag = fragment;
            transaction.replace(R.id.current_fragment, fragment);
            transaction.commit();
        }

//                        Toast.makeText(MainActivity.this, "hii", Toast.LENGTH_SHORT).show();
//                        Uri uri = Uri.parse("smsto:6178956649;6302200547");
//                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(it);

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
