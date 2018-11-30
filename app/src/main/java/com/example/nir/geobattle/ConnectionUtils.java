package com.example.nir.geobattle;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public static GameData readServer(ObjectInputStream is) throws IOException {
        GameData m = null;
        Gson gson = new Gson();
        try {
            String s = (String) is.readObject();
            m = gson.fromJson(s, GameData.class);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "readServer: failed reading from server");
            e.printStackTrace();
        }
        return m;

    }

    public static void sendServer(ObjectOutputStream os ,GameData data) throws IOException {
        Gson gson = new Gson();
        String s = gson.toJson(data);
        Log.d(TAG, "sendServer: sending server " + data.toString());
        os.writeObject(s);

    }
}
