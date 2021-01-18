package vn.edu.usth.ircclient.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import vn.edu.usth.ircclient.Activities.MainScreenActivity;
import vn.edu.usth.ircclient.R;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.chat_screen);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.send_button);
        EditText editText = (EditText) view.findViewById(R.id.edittext_chatbox);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_screen);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView message = new TextView(getContext());
                String text = editText.getText().toString();
                if (!text.matches("")) {
                    MainScreenActivity activity = (MainScreenActivity) getActivity();
                    String nickname = activity.getNickname();
                    message.setText("(@" + nickname + "): " + text);
                    message.setTextColor(Color.parseColor("#000000"));
                    message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    message.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ((LinearLayout) linearLayout).addView(message);
                    editText.getText().clear();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        });
        return view;
    }
}