package vn.edu.usth.ircclient.Classes;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class IRCCon {
    private PrintWriter out;
    private Boolean update = true;
    private static String serverResponse;

    public void init() throws IOException {
        Socket socket = new Socket("cymru.us.quakenet.org", 6667);
        out = new PrintWriter(socket.getOutputStream(), true);
        Scanner in = new Scanner(socket.getInputStream());

        while (in.hasNext()) {
            update = true;
            String serverMessage = in.nextLine();
            serverResponse = serverResponse + serverMessage + "\n";
            Log.i("Dumper", "[SERVER]" + serverMessage);

            if (serverMessage.startsWith("PING")) {
                String pingContents = serverMessage.split(" ", 2)[1];
                write("PONG " + pingContents);
            }
        }
        in.close();
        out.close();
        update = false;
    }

    public static String getServerResponse() {
        return serverResponse;
    }

    public void write(String fullMessage) {
        Log.i("Writer", ">>>" + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}
