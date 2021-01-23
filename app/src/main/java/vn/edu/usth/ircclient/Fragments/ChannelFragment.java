package vn.edu.usth.ircclient.Fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import vn.edu.usth.ircclient.Activities.MainScreenActivity;
import vn.edu.usth.ircclient.Classes.IRCCon;
import vn.edu.usth.ircclient.R;

public class ChannelFragment extends Fragment {
    private String title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ChannelFragment newInstance() {
        ChannelFragment fragment = new ChannelFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        EditText editText = (EditText) view.findViewById(R.id.edittext_chatbox);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.send_button);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_in_scroll);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_screen);
        MainScreenActivity activity = (MainScreenActivity) getActivity();
        IRCCon ircCon = activity.getIrcCon();
        dump(ircCon, linearLayout, ircCon.getChannelMap(), scrollView);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                editText.getText().clear();
                if (!message.matches("")) {
                    if (title.startsWith("#")) {
                        String display = ircCon.getNickName() + ": " + message;
                        addNewTextView(display, linearLayout);
                        String send = "privmsg " + title + " " + message;
                        sendToServer(send, ircCon);
                    }
                    else {
                        sendToServer(message, ircCon);
                    }
                    scrollDown(linearLayout, scrollView);
                }
            }
        });
        return view;
    }

    private void dump(IRCCon ircCon, LinearLayout linearLayout, HashMap channelMap, ScrollView scrollView) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String response = (String) channelMap.get(title);
                        if (response != null) {
                            if (!response.matches("")) {
                                addNewTextView(response, linearLayout);
                                channelMap.put(title, "");
                                scrollDown(linearLayout, scrollView);
                            }
                        }
                    }
                });
            }
        }, 0, 1000);
    }


    public void addNewTextView(String response, LinearLayout linearLayout) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setText(response);
        linearLayout.addView(textView);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void sendToServer(String message, IRCCon ircCon) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ircCon.write(message);
                return null;
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void scrollDown(LinearLayout linearLayout, ScrollView scrollView) {
        linearLayout.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}