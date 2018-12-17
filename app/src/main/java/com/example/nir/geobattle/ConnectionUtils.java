package com.example.nir.geobattle;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import com.google.gson.Gson;

public class ConnectionUtils {

    private static final String TAG = "ConnectionUtils";

    static void closeSocket(Socket socket){
        try {
            Log.d(TAG, "closeSocket: trying to close server socket");
            socket.close();
        } catch (IOException e) {
            Log.d(TAG, "closeSocket: failed to close server socket");
            e.printStackTrace();
        }
    }

    static void closeStream(Closeable s ){
        try {
            Log.d(TAG, "closeStream: trying to close stream");
            s.close();
        } catch (IOException e) {
            Log.d(TAG, "closeStream: failed to close stream");
            e.printStackTrace();
        }
    }

    static void sendObjectOutputStream(ObjectOutputStream os, String s) throws IOException {
        os.writeObject(s);

        PrintStream ps = new PrintStream(os);
        if (ps.checkError()){
            throw new IOException("Error sending client with objectStream " + os.toString());
        }
    }

    static String readObjectInputStream(ObjectInputStream is) throws IOException {
        String s = null;
        try {
            s = (String) is.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return s;
    }

    static String gameDataToJson(GameData gameData){
        Gson gson = new Gson();
        return gson.toJson(gameData);
    }

    static GameData jsonToGameData(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, GameData.class);
    }
}
