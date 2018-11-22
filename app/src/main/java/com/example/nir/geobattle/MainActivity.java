package com.example.nir.geobattle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();

        final Button button_battle = (Button) findViewById(R.id.button_battle);
        button_battle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("=====", "onClick: battle clicked!");
                FireIntent(Battle.class);
            }
        });
        final Button button_previous_scores = (Button) findViewById(R.id.button_previous_scores);
        button_previous_scores.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("=====", "onClick: previous scores clicked!");
            }
        });

        GameData gameData = new GameData(GameData.DataType.ANSWER,"");
    }

    public void updateQuestion ( String s ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Code to run on UI thread
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


    }

    private void FireIntent(Class cls){
        Log.d("=====", "FireIntent: fired cls" + cls.toString());
        Intent i = new Intent(this , cls);
        startActivity(i);
    }

}
