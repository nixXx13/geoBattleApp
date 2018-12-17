package com.example.nir.geobattle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Battle extends AppCompatActivity {

    private ServerHandler serverHandler;
    private Thread threadServerHandler;
    private final int NUM_BUTTONS       = 4;
    private final int NUM_EXTRAS       = 3;
    private final int NUM_PLAYERS_TOTAL = 4;
    private int NUM_PLAYERS;

    private TextView[] mPlayerScore;
    private TextView mQuestion;
    private TextView mInfo;

    private Button[] mButtonOpt;
    private Button[] mExtras;

    private ProgressBar pbHeaderProgress;
    private UIHandler   uiHandler;

    private List<Button> LastCorrect;

    public enum GameStage{
        CONNECTED,
        BATTLE_STARTED,
    }

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
                                serverHandler.sendServer( new GameData(GameData.DataType.FIN));
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

        uiHandler = new UIHandler();

        mQuestion = (TextView)findViewById(R.id.tv_battle_question);
        mQuestion.setTextColor(getResources().getColor(R.color.white));
        mQuestion.setVisibility(View.INVISIBLE);

        mButtonOpt = new Button[NUM_BUTTONS];
        mButtonOpt[0] = (Button)findViewById(R.id.bt_battle_op1);
        mButtonOpt[1] = (Button)findViewById(R.id.bt_battle_op2);
        mButtonOpt[2] = (Button)findViewById(R.id.bt_battle_op3);
        mButtonOpt[3] = (Button)findViewById(R.id.bt_battle_op4);

        LastCorrect = new ArrayList<>();
        for ( int i = 0 ; i < NUM_BUTTONS ; i++){
            mButtonOpt[i].setOnClickListener(new ButtonListener(mButtonOpt[i]));
            mButtonOpt[i].setVisibility(View.INVISIBLE);
            LastCorrect.add(mButtonOpt[i]);
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

        Intent i = getIntent();
        String roomName         = i.getStringExtra("roomName");
        String playerName       = i.getStringExtra("playerName");
        String type             = i.getStringExtra("type");
        String roomPassword     = i.getStringExtra("roomPass");
        int roomSize            = Integer.valueOf(i.getStringExtra("roomSize"));

        if ( this.serverHandler == null) {
            this.serverHandler = new ServerHandler(this,playerName,roomName,type,roomPassword,roomSize);
            threadServerHandler = new Thread(serverHandler);
            threadServerHandler.start();
        }
    }

    public void setGameDisplay(final GameStage gameStage) {
        if (gameStage.equals(GameStage.BATTLE_STARTED)) {
            uiHandler.setViewVisability(mQuestion,View.VISIBLE);
            for (int i = 0; i < NUM_BUTTONS; i++) {
                uiHandler.setViewVisability(mButtonOpt[i], View.VISIBLE);
            }
        }
        if (gameStage.equals(GameStage.CONNECTED)) {
            toggleProgressBar(false);
            updateInfo("Waiting for other players...");
            for (int i = 0; i < NUM_BUTTONS; i++) {
                mButtonOpt[i].setOnClickListener(new ButtonListener(mButtonOpt[i]));
            }
        }
    }

    public void setScoresDisplay(final String playersName) {

        final String[] players = playersName.split(":");
        NUM_PLAYERS = players.length;

        for (int i = 0; i < NUM_PLAYERS; i++) {
            uiHandler.updateTextView(mPlayerScore[i],players[i] + "\n0");
            uiHandler.setViewVisability(mPlayerScore[i], View.VISIBLE);
        }
    }

    public void updateInfo(String update){
        uiHandler.updateTextView(mInfo,update);
    }

    public void updateScores(String update){
        String[] updateScores = update.split(" ");

        for ( int i = 0 ; i < NUM_PLAYERS ; i++){
            String updatedScore = updateScores[i].replace(":","\n");
            uiHandler.updateTextView(mPlayerScore[i],updatedScore);
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
        uiHandler.updateTextView(mQuestion, question);
        for ( int i = 0 ; i < NUM_BUTTONS ; i++){
            uiHandler.setViewVisability(mButtonOpt[i],View.VISIBLE);
            uiHandler.updateTextView(mButtonOpt[i], pAnswer.get(i));
            if ( i != NUM_BUTTONS-1 ){
                //last is correct , no need to animate
                uiHandler.alpha(LastCorrect.get(i),0.2f,1.0f);
            }
        }
    }

    public void toggleProgressBar(final boolean setVisible){
        int visibility = setVisible ? View.VISIBLE : View.INVISIBLE;
        uiHandler.setViewVisability(pbHeaderProgress,visibility);
    }

    public void markCorrectAnswer(final String correctAnswer){
        int correctIndex = 0;
        for ( int i = 0 ; i < NUM_BUTTONS ; i++){
            if (!LastCorrect.get(i).getText().equals(correctAnswer)) {
                uiHandler.alpha(LastCorrect.get(i),1.0f,0.2f);
            }
            else{
               correctIndex = i;
            }
        }
        Button correctButton = LastCorrect.get(correctIndex);
        LastCorrect.remove(correctIndex);
        LastCorrect.add(correctButton);
    }

    public void notifyServerHandler(){
        synchronized (serverHandler) {
            serverHandler.notifyAll();
        }
    }

    public void returnMain(final String errorMsg){
        uiHandler.makeToast(getApplicationContext(),errorMsg);
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
