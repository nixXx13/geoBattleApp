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

public class Battle extends AppCompatActivity {

    private ServerHandler serverHandler;
    private Thread threadServerHandler;
    private final String DEBUG = "GEO_DEBUG:Battle";

    private TextView mGameScoresStatus;
    private TextView mQuestion;
    private Button mButtonOpt1;
    private Button mButtonOpt2;
    private TextView previousCorrectAnswer;
    private ProgressBar pbHeaderProgress;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnMain("Game exited.");
                if (!(serverHandler == null)){
                    // TODO - finally doesnt execute
                    // TODO - must send FIN to release server
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
        mQuestion.setVisibility(View.INVISIBLE);

        mButtonOpt1 = (Button)findViewById(R.id.bt_battle_op1);
        mButtonOpt1.setOnClickListener(new ButtonListener(mButtonOpt1));
        mButtonOpt1.setVisibility(View.INVISIBLE);

        mButtonOpt2 = (Button)findViewById(R.id.bt_battle_op2);
        mButtonOpt2.setOnClickListener(new ButtonListener(mButtonOpt2));
        mButtonOpt2.setVisibility(View.INVISIBLE);

        mGameScoresStatus = (TextView)findViewById(R.id.tv_game_scores_status);
        mGameScoresStatus.setText("Connecting to server...");
        mGameScoresStatus.setVisibility(View.VISIBLE);

        previousCorrectAnswer = (TextView)findViewById(R.id.tv_prev_correct_answer);
        previousCorrectAnswer.setVisibility(View.INVISIBLE);

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

                mButtonOpt1.setVisibility(View.VISIBLE);
                mButtonOpt2.setVisibility(View.VISIBLE);

                mGameScoresStatus.setVisibility(View.VISIBLE);
                previousCorrectAnswer.setVisibility(View.VISIBLE);
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
        updateTextView(previousCorrectAnswer,"Previous correct answer - " + correctAnswer);
        if (!mButtonOpt1.getText().equals(correctAnswer)) {
            setViewBackground(mButtonOpt1,R.drawable.roundbuttongrayed);
//            setViewColorTransition(mButtonOpt1,false);
        }
        if (!mButtonOpt2.getText().equals(correctAnswer)) {
            setViewBackground(mButtonOpt2,R.drawable.roundbuttongrayed);
//            setViewColorTransition(mButtonOpt2,false);

        }
    }

    public void updateInfoLabel(String update){
        updateTextView(mGameScoresStatus,update);
    }

    public void updateQuestion (GameData questionGameData){

        final String question = questionGameData.getContent("question");
        final String possibleAnswer1 = questionGameData.getContent("pAnswer1");
        final String possibleAnswer2 = questionGameData.getContent("pAnswer2");

        updateTextView(mQuestion, question);
        updateTextView(mButtonOpt1, possibleAnswer1);
        updateTextView(mButtonOpt2,possibleAnswer2);
        setViewBackground(mButtonOpt1,R.drawable.roundbutton);
        setViewBackground(mButtonOpt2,R.drawable.roundbutton);
//        setViewColorTransition(mButtonOpt1,true);
//        setViewColorTransition(mButtonOpt2,true);


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
            String answer = (String)button.getText();
            GameData answerResponse = new GameData(GameData.DataType.ANSWER);
            answerResponse.setContent("answer",answer);
            serverHandler.setAnswer(answerResponse);
            notifyServerHandler();
        }
    }
}
