package com.example.nir.geobattle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Battle extends AppCompatActivity {

    private final String DEBUG = "GEO_DEBUG:Battle";

    protected TextView mQuestion;

    private Button mButtonOpt1;
    private Button mButtonOpt2;

    private TextView mUserScore;
    private Button mButtonConnect;
    private ServerHandler serverHandler;
    private Thread threadServerHandler;

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
                mButtonConnect.setVisibility(View.INVISIBLE);
                mQuestion.setVisibility(View.VISIBLE);

                mButtonOpt1.setVisibility(View.VISIBLE);
                mButtonOpt2.setVisibility(View.VISIBLE);

                threadServerHandler = new Thread(serverHandler);
                threadServerHandler.start();
            }
        });

        mQuestion = (TextView)findViewById(R.id.tv_battle_question);
        mQuestion.setVisibility(View.INVISIBLE);

        mButtonOpt1 = (Button)findViewById(R.id.bt_battle_op1);
        mButtonOpt1.setVisibility(View.INVISIBLE);
        mButtonOpt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG, "mButtonOpt1 clicked!");

                // TODO - get answer content from button text
                String answer = "1";
                GameData answerResponse = new GameData(GameData.DataType.ANSWER,answer);
                serverHandler.setAnswer(answerResponse);
                notifyServerHandler();
            }
        });

        mButtonOpt2 = (Button)findViewById(R.id.bt_battle_op2);
        mButtonOpt2.setVisibility(View.INVISIBLE);
        mButtonOpt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG, "mButtonOpt2 clicked!");
                String answer = "2";
                GameData answerResponse = new GameData(GameData.DataType.ANSWER,answer);
                serverHandler.setAnswer(answerResponse);
                notifyServerHandler();
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

    public TextView getmQuestion() {
        return mQuestion;
    }

    public TextView getmUserScore() {
        return mUserScore;
    }

}
