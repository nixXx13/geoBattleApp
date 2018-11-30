package com.example.nir.geobattle;

import android.graphics.drawable.TransitionDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Battle extends AppCompatActivity {

    private ServerHandler serverHandler;
    private Thread threadServerHandler;
    private final int NUM_BUTTONS       = 4;
    private final int NUM_EXTRAS       = 3;
    private final int NUM_PLAYERS_TOTAL = 4;
    private final int NUM_PLAYERS       = 1;

    private TextView[] mPlayerScore;
    private TextView mQuestion;
    private TextView mInfo;

    private Button[] mButtonOpt;
    private Button[] mExtras;

    private ProgressBar pbHeaderProgress;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnMain("Game exited.");
                if (!(serverHandler == null)){
                    Thread fin = new Thread(){
                        @Override
                        public void run() {
                            try {
                                ConnectionUtils.sendServer(serverHandler.getOs() , new GameData(GameData.DataType.FIN));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    fin.start();
                    try {
                        fin.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serverHandler.terminate();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        mQuestion = (TextView)findViewById(R.id.tv_battle_question);
        mQuestion.setTextColor(getResources().getColor(R.color.white));
        mQuestion.setVisibility(View.INVISIBLE);

        mButtonOpt = new Button[NUM_BUTTONS];
        mButtonOpt[0] = (Button)findViewById(R.id.bt_battle_op1);
        mButtonOpt[1] = (Button)findViewById(R.id.bt_battle_op2);
        mButtonOpt[2] = (Button)findViewById(R.id.bt_battle_op3);
        mButtonOpt[3] = (Button)findViewById(R.id.bt_battle_op4);

        for ( int i = 0 ; i < NUM_BUTTONS ; i++){
            mButtonOpt[i].setOnClickListener(new ButtonListener(mButtonOpt[i]));
            mButtonOpt[i].setVisibility(View.INVISIBLE);
        }

        mExtras = new Button[NUM_EXTRAS];
        mExtras[0] = (Button)findViewById(R.id.extra1);
        mExtras[1] = (Button)findViewById(R.id.extra2);
        mExtras[2] = (Button)findViewById(R.id.extra3);
        for ( int i = 0 ; i < NUM_EXTRAS ; i++){
            mExtras[i].setVisibility(View.INVISIBLE);
        }

        mPlayerScore = new TextView[NUM_PLAYERS_TOTAL];
        mPlayerScore[0] = (TextView)findViewById(R.id.tv_pScore1);
        mPlayerScore[1] = (TextView)findViewById(R.id.tv_pScore2);
        mPlayerScore[2] = (TextView)findViewById(R.id.tv_pScore3);
        mPlayerScore[3] = (TextView)findViewById(R.id.tv_pScore4);

        for ( int i = 0 ; i < NUM_PLAYERS_TOTAL ; i++){
            int state = View.GONE;
            if ( i < NUM_PLAYERS){
                state = View.INVISIBLE;
            }
            mPlayerScore[i].setVisibility(state);
            mPlayerScore[i].setTextColor(getResources().getColor(R.color.white));

        }

        mInfo = (TextView)findViewById(R.id.tv_info);
        mInfo.setText("Connecting to server...");
        mInfo.setTextColor(getResources().getColor(R.color.white));
        mInfo.setVisibility(View.VISIBLE);

        pbHeaderProgress = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        pbHeaderProgress.setVisibility(View.VISIBLE);

        if ( this.serverHandler == null) {
            this.serverHandler = new ServerHandler(this);
            threadServerHandler = new Thread(serverHandler);
            threadServerHandler.start();
        }
    }


    public void setGameDisplay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestion.setVisibility(View.VISIBLE);

                for ( int i = 0 ; i < NUM_BUTTONS ; i++){
                    mButtonOpt[i].setOnClickListener(new ButtonListener(mButtonOpt[i]));
                    mButtonOpt[i].setVisibility(View.VISIBLE);
                }

                for ( int i = 0 ; i < NUM_PLAYERS ; i++){
                    mPlayerScore[i].setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void toggleProgressBar(final boolean isVisible){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isVisible){
                    pbHeaderProgress.setVisibility(View.VISIBLE);
                }
                else{
                    pbHeaderProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void markCorrectAnswer(final String correctAnswer){
        for ( int i = 0 ; i < NUM_BUTTONS ; i++){
            if (!mButtonOpt[i].getText().equals(correctAnswer)) {
                setViewBackground(mButtonOpt[i], R.drawable.roundbuttongrayed);
            }
        }
    }

    public void updateInfo(String update){
        updateTextView(mInfo,update);
    }
    public void updateScores(String update){
        String[] updateScores = update.split(" ");
        System.out.println(updateScores);

        for ( int i = 0 ; i < NUM_PLAYERS ; i++){
            updateScores[i] = "player" + i + "\n" + updateScores[i].replaceFirst("[a-zA-Z0-9]*:" , "");
            System.out.println(updateScores[i]);
        }

        for ( int i = 0 ; i < NUM_PLAYERS ; i++){
            updateTextView(mPlayerScore[i],updateScores[i]);
        }

    }

    public void updateQuestion (GameData questionGameData){

        final String question = "What will be the question here? will it be very long? " + questionGameData.getContent("question");

        ArrayList<String> pAnswer = new ArrayList<String>();
        for (int i = 0; i < NUM_BUTTONS; i++) {
            pAnswer.add(questionGameData.getContent("pAnswer" + i));
        }
        // shuffle answers
        Collections.shuffle(pAnswer);
        updateTextView(mQuestion, question);
        for ( int i = 0 ; i < NUM_BUTTONS ; i++){
            updateTextView(mButtonOpt[i], pAnswer.get(i));
            setViewBackground(mButtonOpt[i],R.drawable.roundbutton);
        }
    }

    public void setViewColorTransition(final View view , final boolean reverse){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TransitionDrawable transition = (TransitionDrawable) view.getBackground();
                if (!reverse) {
                    transition.startTransition(500);
                }else{
                    transition.reverseTransition(500);
                }
            }
        });

    }

    public void setViewBackground(final View view ,final  int drawableId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setBackground(ContextCompat.getDrawable(getBaseContext(), drawableId));
            }
        });
    }

    public void updateTextView ( final TextView tv , final String s ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(s);
            }
        });
    }

    public void notifyServerHandler(){
        synchronized (serverHandler) {
            serverHandler.notifyAll();
        }

    }

    public void returnMain(final String errorMsg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    private class ButtonListener implements View.OnClickListener {
        private Button button;

        ButtonListener(Button button) {
            this.button = button;
        }

        @Override
        public void onClick(View view) {
            // TODO - set buttom pressed color
            String answer = (String)button.getText();
            GameData answerResponse = new GameData(GameData.DataType.ANSWER);
            answerResponse.setContent("answer",answer);
            serverHandler.setAnswer(answerResponse);
            notifyServerHandler();
        }
    }
}
