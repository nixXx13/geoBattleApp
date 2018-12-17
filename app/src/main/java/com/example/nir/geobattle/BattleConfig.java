package com.example.nir.geobattle;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BattleConfig extends AppCompatActivity {

    private TextView tvRoomSize;

    private EditText editTextPlayerName;
    private EditText editTextRoomName;
    private EditText editTextRoomPass;

    private Button buttonJoin;
    private Button buttonCreate;
    private Button[] buttoRoomSize;

    private Button play;

    private String type;
    private int roomSize;
    private UIHandler uiHandler;

    private final int NUM_ROOM_SIZE_BUTTONS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_config);

        uiHandler = new UIHandler();

        type        = "join";
        roomSize    = 1; //TODO set to 2
        tvRoomSize = (TextView)findViewById(R.id.tv_room_size);

        buttoRoomSize = new Button[3];
        buttoRoomSize[0] = (Button)findViewById(R.id.bt_battle_size2);
        uiHandler.setViewBackground(getBaseContext(),buttoRoomSize[0],R.drawable.halfleftroundbutton);

        buttoRoomSize[1] = (Button)findViewById(R.id.bt_battle_size3);
        uiHandler.setViewBackground(getBaseContext(),buttoRoomSize[1],R.drawable.squarebutton);

        buttoRoomSize[2] = (Button)findViewById(R.id.bt_battle_size4);
        uiHandler.setViewBackground(getBaseContext(),buttoRoomSize[2],R.drawable.halfrightroundbutton);


        for ( int i = 0 ; i < NUM_ROOM_SIZE_BUTTONS ; i++){
            buttoRoomSize[i].setOnClickListener(new ButtonListener(buttoRoomSize[i]));
            buttoRoomSize[i].setVisibility(View.VISIBLE);
            uiHandler.alpha(buttoRoomSize[i],1.0f,0.2f);

        }

        buttonJoin      = (Button)findViewById(R.id.bt_join);
        buttonJoin.setOnClickListener(new ButtonListener(buttonJoin));
        uiHandler.setViewBackground(getBaseContext(),buttonJoin,R.drawable.halfleftroundbutton);

        buttonCreate    = (Button)findViewById(R.id.bt_create);
        buttonCreate.setOnClickListener(new ButtonListener(buttonCreate));
        uiHandler.setViewBackground(getBaseContext(),buttonCreate,R.drawable.halfrightroundbutton);
        uiHandler.alpha(buttonCreate,1.0f,0.2f);

        play = (Button)findViewById(R.id.play);
        play.setOnClickListener(new PlayListener(this));

        editTextRoomName    = (EditText)findViewById(R.id.et_room_name);
        editTextRoomName.setText("default_room");

        editTextPlayerName  = (EditText)findViewById(R.id.et_player_name);
        editTextPlayerName.setText("Ploni");

        editTextRoomPass    = (EditText)findViewById(R.id.et_room_pass);
        editTextRoomPass.setText("");


    }

    private class ButtonListener implements View.OnClickListener {
        private Button button;

        ButtonListener(Button button) {
            this.button = button;
        }

        @Override
        public void onClick(View view) {
            String buttonText = (String)button.getText();
            Log.d("==========", "onClick: " + buttonText);
            if (buttonText.equals("Join") || buttonText.equals("Create")){
                if (buttonText.equals("Join") && type.equals("create")){
                    uiHandler.setViewBackground(getBaseContext(),buttonJoin,R.drawable.halfleftroundbutton);
                    uiHandler.alpha(buttonCreate,1.0f,0.2f);
                    uiHandler.alpha(buttonJoin,0.2f,1.0f);

                    uiHandler.alpha(tvRoomSize,1.0f,0.2f);
                    setButtonsDisplayOff();
                }
                if (buttonText.equals("Create") && type.equals("join")){
                    uiHandler.setViewBackground(getBaseContext(),buttonCreate,R.drawable.halfrightroundbutton);
                    uiHandler.alpha(buttonJoin,1f,0.2f);
                    uiHandler.alpha(buttonCreate,0.2f,1.0f);

                    uiHandler.alpha(tvRoomSize,0.2f,1.0f);
                    setButtonsDisplay(String.valueOf(roomSize));
                }
                type = buttonText.toLowerCase();
            }
            else {
                if (type.equals("create")) {
                    setButtonsDisplay(buttonText);
                    roomSize = Integer.valueOf((String) button.getText());
                }
            }
        }
    }

    private void setButtonsDisplay(String buttonOn){
        for (Button b : buttoRoomSize){
            if(buttonOn.equals(b.getText().toString())){
                uiHandler.alpha(b,0.2f,1.0f);
            }
            else if(String.valueOf(roomSize).equals(b.getText().toString())){
                uiHandler.alpha(b,1.0f,0.2f);
            }
            else{
                //
            }
        }
    }

    private void setButtonsDisplayOff(){
        for (Button b : buttoRoomSize){
            if(String.valueOf(roomSize).equals(b.getText().toString())){
                uiHandler.alpha(b,1.0f,0.2f);
            }
            else{
                //
            }
        }
    }

    private  class PlayListener implements View.OnClickListener{

        private Context contex;

        PlayListener(Context contex){
            this.contex = contex;
        }

        @Override
        public void onClick(View view) {

            Intent i = new Intent(contex , Battle.class);
            i.putExtra("roomName"       ,editTextRoomName.getText().toString());
            i.putExtra("playerName"     ,editTextPlayerName.getText().toString());
            i.putExtra("type"           ,type);
            i.putExtra("roomSize"       ,""+roomSize);
            i.putExtra("roomPassword"   ,editTextRoomPass.getText().toString());
            startActivity(i);

        }
    }


}
