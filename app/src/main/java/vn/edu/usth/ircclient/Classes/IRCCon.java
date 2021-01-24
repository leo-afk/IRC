package vn.edu.usth.ircclient.Classes;

import android.util.Log;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Scanner;

import vn.edu.usth.ircclient.Activities.MainScreenActivity;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

public class IRCCon {
    private PrintWriter out;
    private Boolean dm = false;
    private String dmUser = "";
    private String nickName;
    private Boolean checkNick = false;
    private static HashMap<String, String> ChannelMap = new HashMap<>();

    public void init(String host) throws IOException {
        Socket socket = new Socket(host, 6667);

        out = new PrintWriter(socket.getOutputStream(), true);
        Scanner in = new Scanner(socket.getInputStream());

        while (in.hasNext()) {
            String serverMessage = in.nextLine();
            if (serverMessage.contains("PRIVMSG " + nickName)) {
                String user = serverMessage.split(":")[1].split("!")[0];
                String user_text = serverMessage.split(" ", 4)[3].split(":", 2)[1];
                if (ChannelMap.containsKey(user)) {
                    ChannelMap.put(user, ChannelMap.get(user) + user + ": " + user_text);
                } else {
                    dm = true;
                    dmUser = user;
                    ChannelMap.put(user, user + ": " + user_text);
                }
            }

            if (serverMessage.contains("PRIVMSG #")) {
                Log.i("THIS", serverMessage);
                String channelName = serverMessage.split(" ")[2];
                String channelText = serverMessage.split(" ", 4)[3].split(":")[1];
                String user = serverMessage.split(":")[1].split("!")[0];
                if (ChannelMap.containsKey(channelName)) {
                    ChannelMap.put(channelName, ChannelMap.get(channelName) + user + ": " + channelText);
                    Log.i(channelName, ChannelMap.get(channelName));
                } else {
                    ChannelMap.put(channelName, user + ": " + channelText);
                    Log.i(channelName + "init", ChannelMap.get(channelName));
                }
            } else if (serverMessage.contains("JOIN #")) {
                String channelName = serverMessage.split(" ")[2];
                String user = serverMessage.split(":")[1].split("!")[0];
                if (ChannelMap.containsKey(channelName)) {
                    ChannelMap.put(channelName, ChannelMap.get(channelName) + user + " has joined " + channelName + "\n");
                    Log.i("User", ChannelMap.get(channelName));
                } else {
                    ChannelMap.put(channelName, user + " has joined " + channelName + "\n");
                    Log.i("User init", ChannelMap.get(channelName));
                }
            } else if (serverMessage.contains("PART #")) {
                String channelName = serverMessage.split(" ")[2];
                String user = serverMessage.split(":")[1].split("!")[0];
                if (!user.matches(nickName)) {
                    if (ChannelMap.containsKey(channelName)) {
                        ChannelMap.put(channelName, ChannelMap.get(channelName) + user + " has left " + channelName + "\n");
                        Log.i("User", ChannelMap.get(channelName));
                    } else {
                        ChannelMap.put(channelName, user + " has left " + channelName + "\n");
                        Log.i("User init", ChannelMap.get(channelName));
                    }
                }
            } else if (serverMessage.startsWith("PING")) {
                String pingContents = serverMessage.split(" ", 2)[1];
                write("PONG " + pingContents);
            } else if (ChannelMap.containsKey("Server")) {
                ChannelMap.put("Server", ChannelMap.get("Server") + serverMessage + "\n");
                Log.i("Dumper", "[SERVER]" + serverMessage);
            } else {
                ChannelMap.put("Server", serverMessage);
            }

        }
        in.close();
        out.close();
    }

    public static HashMap getChannelMap() {
        return ChannelMap;
    }


    public void write(String fullMessage) {
        if (out != null) {
            Log.i("Writer", ">>>" + fullMessage);
            out.print(fullMessage + "\r\n");
            out.flush();
        }
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        checkNick = true;
    }

    public String getNickName() {
        return nickName;
    }

    public Boolean getDm() {
        return dm;
    }

    public void setDm(Boolean dm) {
        this.dm = dm;
    }

    public String getDmUser() {
        return dmUser;
    }

    public void removeFromChannelMap(String key) {
        ChannelMap.remove(key);
    }

    public Boolean getCheckNick() {
        return checkNick;
    }

    public void setCheckNick(Boolean checkNick) {
        this.checkNick = checkNick;
    }
}
