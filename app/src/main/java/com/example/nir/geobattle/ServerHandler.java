package com.example.nir.geobattle;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ServerHandler implements Runnable {

    public Socket socket = null;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Battle battleContex;
    private final static String SERVER_IP = "54.219.181.27";
    private final String DEBUG = "GEO_DEBUG:ServerHandler";

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
            Log.d(DEBUG, "Connection to server falied!");
        }
        Log.i("ServerHandler", "Connected to server successfully");

        GameData m = ConnectionUtils.readServer(is);
        while (m != null) {
            GameData.DataType type = m.getType();
            if (type == GameData.DataType.QUESTION) {
                Log.d(DEBUG, "run:question");
                battleContex.updateTextView(battleContex.getmQuestion(), m.getContent());
                Log.d(DEBUG, "run:waitUserInput");
                waitUserInput();
                Log.d(DEBUG, "run:waitUserInput finished");
                ConnectionUtils.sendServer(os,answer);
            }
            if (type == GameData.DataType.ANSWER) {
                System.out.println("got correct_answer from server :" + m.getContent());
            }
            if (type == GameData.DataType.UPDATE) {
                System.out.println("server update :" + m.getContent());
            }
            m = ConnectionUtils.readServer(is);

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
