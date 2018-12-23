package com.example.nir.geobattle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BattleSummary extends AppCompatActivity {
    private UIHandler uiHandler;

    private TextView[] mPlayerScore;
    private Button btDone;

    private final int NUM_PLAYERS_TOTAL = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHandler = new UIHandler();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_summary);

        btDone = (Button)findViewById(R.id.done);
        btDone.setOnClickListener(new DoneListener(this));

        mPlayerScore = new TextView[NUM_PLAYERS_TOTAL];
        mPlayerScore[0] = (TextView)findViewById(R.id.tv_pScore1);
        mPlayerScore[1] = (TextView)findViewById(R.id.tv_pScore2);
        mPlayerScore[2] = (TextView)findViewById(R.id.tv_pScore3);
        mPlayerScore[3] = (TextView)findViewById(R.id.tv_pScore4);

        Intent intent = getIntent();
        String playersScore = intent.getStringExtra("playersScore").trim();

        String[] playersScoreArr = playersScore.split(" ");
        int NUM_PLAYERS = playersScoreArr.length;

        for ( int i = 0 ; i < NUM_PLAYERS_TOTAL ; i++){
            int state = View.GONE;
            if ( i < NUM_PLAYERS){
                state = View.INVISIBLE;
            }
            mPlayerScore[i].setVisibility(state);
            mPlayerScore[i].setTextColor(getResources().getColor(R.color.white));
        }

        for (int i =0; i< playersScoreArr.length;i++) {
            String[] nameAndScore = playersScoreArr[i].split(":");
            String name = nameAndScore[0];
            String score = nameAndScore[1];
            uiHandler.updateTextView(mPlayerScore[i],name + "\n"+score);
            uiHandler.setViewVisability(mPlayerScore[i], View.VISIBLE);
        }
    }

    private  class DoneListener implements View.OnClickListener{

        private Context contex;

        DoneListener(Context contex){
            this.contex = contex;
        }

        @Override
        public void onClick(View view) {
            System.out.println("=====FINISH");
            // TODO - proper way to return? now mainActivity dups?
            Intent returnBtn = new Intent(getApplicationContext(),
                    MainActivity.class);

            startActivity(returnBtn);
        }
    }


}
