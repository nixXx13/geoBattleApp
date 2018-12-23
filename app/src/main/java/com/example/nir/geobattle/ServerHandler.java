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


    private ObjectOutputStream  os;
    private ObjectInputStream   is;
    private Battle              battleContex;
    private Socket              socket = null;

    private GameData answer;

    private GameData dataConnection;

    private double timeCoefficient = 10;
    private final static double DECREASE_FACTOR = 0.1;

    ServerHandler(Battle battleContex,String playerName,String roomName,String type,String roomPassword,int roomSize) {
        this.battleContex = battleContex;

        dataConnection = new GameData(GameData.DataType.UPDATE);
        dataConnection.setContent("name"            , playerName);
        dataConnection.setContent("roomName"        , roomName );
        dataConnection.setContent("type"            , type );
        dataConnection.setContent("roomPassword"    , roomPassword);
        dataConnection.setContent("roomSize"        , ""+roomSize);
        Log.d(TAG, "ServerHandler: created instance with" + playerName + ":" + roomName+ ":" +
                type+ ":" + roomPassword+ ":" + roomSize);
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
        battleContex.setGameDisplay(Battle.GameStage.CONNECTED);
        Log.i("ServerHandler", "Connected to server successfully");

        try {
            sendServer(dataConnection);

            GameData m = readServer();
            battleContex.setGameDisplay(Battle.GameStage.BATTLE_STARTED);
            while (m != null) {
                GameData.DataType type = m.getType();
                Log.d(TAG, "run: received gameData from server" + m.toString());
                switch (type) {
                    case QUESTION:
                        battleContex.updateInfo("Your turn!");
                        battleContex.updateQuestion(m);
                        waitUserInput();
                        sendServer(answer);
                        break;
                    case ANSWER:
                        battleContex.updateInfo("Rivals turn");
                        String answer = m.getContent("answer");
                        battleContex.markCorrectAnswer(answer);
                        break;
                    case UPDATE:
                        if (m.getContent("scores") !=null){
                            String update = m.getContent("scores");
                            battleContex.updateScores(update);
                        }
                        if (m.getContent("update") !=null){
                            battleContex.updateInfo(m.getContent("update"));
                        }
                        if (m.getContent("settings:playersName") !=null){
                            battleContex.setScoresDisplay(m.getContent("settings:playersName"));
                        }
                        break;
                    case SKIP:
                        break;
                    case FIN:
                        // game has ended
                        GameData fin = new GameData(GameData.DataType.FIN);
                        fin.setContent("reason" , "game finished");
                        sendServer(fin);

                        // TODO - update gui that game ended
                        String summary = m.getContent("summary");
                        battleContex.fireSummaryIntent(summary);
                        return;
                }
                m = readServer();

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
                    timeCoefficient = 10;
                    // prepare blank answer to be sent
                    GameData blankAnswer = new GameData(GameData.DataType.ANSWER);
                    blankAnswer.setContent("answer", "-1");
                    setAnswer(blankAnswer);
                    while (timeCoefficient > 0) {
                        battleContex.updateInfo(timeCoefficient+ " seconds to answer!");
                        wait(100);
                        timeCoefficient = Math.round((timeCoefficient - DECREASE_FACTOR)* 10d) / 10d;
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
        answer.setContent("timeCoeffient",String.valueOf(this.timeCoefficient));
        this.answer = answer;
    }

    public void terminate(){
        Log.d(TAG, "terminate: trying to close output stream");
        ConnectionUtils.closeStream(os);
        Log.d(TAG, "terminate: trying to close input stream");
        ConnectionUtils.closeStream(is);
        ConnectionUtils.closeSocket(socket);
    }

    private GameData readServer() throws IOException {
        GameData m;
        String input = ConnectionUtils.readObjectInputStream(is);
        if (input!=null) {
            m = ConnectionUtils.jsonToGameData(input);
        }else{
            throw new IOException(TAG + "readServer: Error reading response");
        }
        return m;
    }

    void sendServer(GameData gameData) throws IOException {
        String s = ConnectionUtils.gameDataToJson(gameData);
        ConnectionUtils.sendObjectOutputStream(os,s);
    }
}
