package com.example.nir.geobattle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bt_battle;
    Button bt_stats;

    Animation an_fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_battle = (Button) findViewById(R.id.button_battle);
        bt_battle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("=====", "onClick: battle clicked!");
                FireIntent(Battle.class);
            }
        });
        bt_stats = (Button) findViewById(R.id.stats);
        bt_stats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("=====", "onClick: previous scores clicked!");
            }
        });

        an_fromBottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        bt_battle.setAnimation(an_fromBottom);
        bt_stats.setAnimation(an_fromBottom);

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

    private void FireIntent(Class cls){
        Log.d("=====", "FireIntent: fired cls" + cls.toString());
        Intent i = new Intent(this , cls);
        startActivity(i);
    }

}
