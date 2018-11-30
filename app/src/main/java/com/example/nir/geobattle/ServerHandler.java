package com.example.nir.geobattle;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ServerHandler implements Runnable {

    private final static String TAG = "ServerHandler";
    private final static boolean TIME_ATTACK = true;
    private final static String SERVER_IP = "54.219.181.27";

    public ObjectOutputStream getOs() {
        return os;
    }

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Battle battleContex;
    private Socket socket = null;

    private GameData answer;

    ServerHandler(Battle battleContex) {
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
            String errorMsg = "run: Error opening connection to server.";
            Log.e(TAG, errorMsg);
            battleContex.returnMain(errorMsg);
            return;
        }
        battleContex.toggleProgressBar(false);
        battleContex.updateInfo("Welcome to another geoBattle");
        battleContex.setGameDisplay();
        Log.i("ServerHandler", "Connected to server successfully");

        try {
            GameData m = ConnectionUtils.readServer(is);
            while (m != null) {
                GameData.DataType type = m.getType();
                Log.d(TAG, "run: received gameData from server" + m.toString());
                switch (type) {
                    case QUESTION:
                        battleContex.updateQuestion(m);
                        waitUserInput();
                        ConnectionUtils.sendServer(os, answer);
                        break;
                    case ANSWER:
                        String answer = m.getContent("answer");
                        battleContex.markCorrectAnswer(answer);
                        break;
                    case UPDATE:
                        String update = m.getContent("update");
                        battleContex.updateScores(update);
                        break;
                    case SKIP:
                        break;
                    case FIN:
                        // game has ended
                        ConnectionUtils.sendServer(os, new GameData(GameData.DataType.FIN));
                        // TODO - update gui that game ended
                        String summary = m.getContent("summary");
                        battleContex.returnMain("Game ended");
                        return;
                }
                m = ConnectionUtils.readServer(is);

            }
        }catch (IOException e ){
            Log.e(TAG, "run: Game loop exited with exception");
            e.printStackTrace();
            battleContex.returnMain("Error connecting to server.");
        }
        finally {
            Log.d(TAG, "run: calling terminate to close socket and streams");
            terminate();
        }
    }

    private void waitUserInput(){
        synchronized (this){
            try {
                Log.d(TAG, "waitUserInput: waiting for user input");
                if (TIME_ATTACK) {
                    int timeout = 10;
                    // prepare blank answer to be sent
                    GameData blankAnswer = new GameData(GameData.DataType.ANSWER);
                    blankAnswer.setContent("answer", "-1");
                    setAnswer(blankAnswer);
                    while (timeout > 0) {
                        battleContex.updateInfo((timeout+1)/2 + " seconds to answer!");
                        wait(500);
                        timeout--;
                        if (!answer.getContent("answer").equals("-1")){
                            return;
                        }
                    }
                }
                else{
                    wait();
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "waitUserInput: error waiting for user input");
                e.printStackTrace();
            }
        }
    }

    public void setAnswer(GameData answer) {
        this.answer = answer;
    }

    public void terminate(){
        Log.d(TAG, "terminate: trying to close output stream");
        ConnectionUtils.closeStream(os);
        Log.d(TAG, "terminate: trying to close input stream");
        ConnectionUtils.closeStream(is);
        ConnectionUtils.closeSocket(socket);
    }

}
