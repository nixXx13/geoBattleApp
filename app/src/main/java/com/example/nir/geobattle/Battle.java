package com.example.nir.geobattle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private Button mButtonConnect;

    // TODO add update label

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        this.serverHandler = new ServerHandler(this);

        mButtonConnect = (Button)findViewById(R.id.bt_battle_connect);
        mButtonConnect.setVisibility(View.VISIBLE);
        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG, "Connect clicked!");

                setGameDisplay();

                threadServerHandler = new Thread(serverHandler);
                threadServerHandler.start();
            }
        });

        mQuestion = (TextView)findViewById(R.id.tv_battle_question);
        mQuestion.setVisibility(View.INVISIBLE);

        mButtonOpt1 = (Button)findViewById(R.id.bt_battle_op1);
        mButtonOpt1.setVisibility(View.INVISIBLE);
        mButtonOpt1.setOnClickListener(new ButtonListener(mButtonOpt1));

        mButtonOpt2 = (Button)findViewById(R.id.bt_battle_op2);
        mButtonOpt2.setVisibility(View.INVISIBLE);
        mButtonOpt2.setOnClickListener(new ButtonListener(mButtonOpt2));

        mGameScoresStatus = (TextView)findViewById(R.id.tv_game_scores_status);
        mGameScoresStatus.setVisibility(View.INVISIBLE);

        previousCorrectAnswer = (TextView)findViewById(R.id.tv_prev_correct_answer);
        previousCorrectAnswer.setVisibility(View.INVISIBLE);
    }

    private void setGameDisplay() {
        mButtonConnect.setVisibility(View.INVISIBLE);
        mQuestion.setVisibility(View.VISIBLE);

        mButtonOpt1.setVisibility(View.VISIBLE);
        mButtonOpt2.setVisibility(View.VISIBLE);

        mGameScoresStatus.setVisibility(View.VISIBLE);
        previousCorrectAnswer.setVisibility(View.VISIBLE);

    }

    public void updatePreviousCorrectAnswer(String update){
        updateTextView(previousCorrectAnswer,"Previous correct answer - " + update);
    }

    public void updateGameScores(String update){
        updateTextView(mGameScoresStatus,update);
    }

    public void updateQuestion (GameData questionGameData){

        final String question = questionGameData.getContent("question");
        final String possibleAnswer1 = questionGameData.getContent("pAnswer1");
        final String possibleAnswer2 = questionGameData.getContent("pAnswer2");

        updateTextView(mQuestion, question);
        updateTextView(mButtonOpt1, possibleAnswer1);
        updateTextView(mButtonOpt2,possibleAnswer2);

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

        public ButtonListener(Button button) {
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
