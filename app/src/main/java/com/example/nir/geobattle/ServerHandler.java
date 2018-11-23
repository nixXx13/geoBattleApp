package com.example.nir.geobattle;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ServerHandler implements Runnable {

    private final static String SERVER_IP = "54.219.181.27";
    private final String DEBUG = "GEO_DEBUG:ServerHandler";

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Battle battleContex;
    public Socket socket = null;

    private GameData answer;

    public ServerHandler(Battle battleContex) {
        this.battleContex = battleContex;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_IP, 4444);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            String errorMsg = "Error opening connection to server.";
            Log.d(DEBUG, errorMsg);
            battleContex.returnMain(errorMsg);
            return;
        }
        Log.i("ServerHandler", "Connected to server successfully");

        try {
            GameData m = ConnectionUtils.readServer(is);
            while (m != null) {
                GameData.DataType type = m.getType();
                switch (type) {
                    case QUESTION:
                        battleContex.updateQuestion(m);
                        waitUserInput();
                        ConnectionUtils.sendServer(os, answer);
                        break;
                    case ANSWER:
                        String answer = m.getContent("answer");
                        battleContex.updatePreviousCorrectAnswer(answer);
                        break;
                    case UPDATE:
                        String update = m.getContent("update");
                        battleContex.updateGameScores(update);
                        break;
                    case SKIP:
                        break;
                    case FIN:
                        //battleContex.showGameStatus()
                        break;
                }
                m = ConnectionUtils.readServer(is);

            }
        }catch (IOException e ){
            // TODO - replace with constant
            battleContex.returnMain("Error connecting to server.");
        }
        ConnectionUtils.closeServerConnection(socket);
    }

    private void waitUserInput(){
        synchronized (this){
            try {
                wait();     // can place timeout here
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAnswer(GameData answer) {
        this.answer = answer;
    }


}
