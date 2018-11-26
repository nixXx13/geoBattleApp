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
        battleContex.toggleProgressBar(false);
        battleContex.updateInfoLabel("Welcome to another geoBattle");
        battleContex.setGameDisplay();
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
                        battleContex.markCorrectAnswer(answer);
                        break;
                    case UPDATE:
                        String update = m.getContent("update");
                        battleContex.updateInfoLabel(update);
                        break;
                    case SKIP:
                        break;
                    case FIN:
                        // game has ended
                        ConnectionUtils.sendServer(os, new GameData(GameData.DataType.FIN));
                        String summary = m.getContent("summary");
                        terminate();
                        // TODO - update gui that game ended
                        battleContex.returnMain("Game ended");
                        return;
                }
                m = ConnectionUtils.readServer(is);

            }
        }catch (IOException e ){
            // TODO - replace with constant
            e.printStackTrace();
            battleContex.returnMain("Error connecting to server.");
        }
        finally {
            terminate();
        }
    }

    private void waitUserInput(){
        int timeout = 5;
        synchronized (this){
            try {
//                while (timeout > 0){
//                    GameData tempAnswer = new GameData(GameData.DataType.ANSWER);
//                    tempAnswer.setContent("answer","-1");
//                    setAnswer(tempAnswer);
//                    battleContex.updateInfoLabel(timeout + " seconds to answer!");
//                    wait(1000);     // can place timeout here
//                    timeout--;
//                }
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAnswer(GameData answer) {
        this.answer = answer;
    }

    public void terminate(){
        // TODO - should be in connectionUtils??
        try {
            System.out.println("=========== closeServerConnection: trying to close os");
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("=========== closeServerConnection: trying to close is");
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConnectionUtils.closeServerConnection(socket);
    }

}
