package com.example.nir.geobattle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;

// TODO - improve exception handling
public class ConnectionUtils {

    static void closeServerConnection(Socket socket){
        try {
            System.out.println("=========== closeServerConnection: trying to close socket");
            socket.close();
        } catch (IOException e) {
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
            e.printStackTrace();
        }
        return m;

    }

    public static void sendServer(ObjectOutputStream os ,GameData data) throws IOException {
        Gson gson = new Gson();
        String s = gson.toJson(data);
        os.writeObject(s);

    }
}
