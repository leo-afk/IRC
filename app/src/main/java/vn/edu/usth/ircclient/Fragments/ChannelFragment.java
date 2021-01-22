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
        dump(ircCon, linearLayout);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if (!message.matches("")) {
                    /*MainScreenActivity activity = (MainScreenActivity) getActivity();
                    IRCCon ircCon = activity.getIrcCon();*/
                    addNewTextView(message, linearLayout);
                    editText.getText().clear();
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            ircCon.write(message);
                            return null;
                        }
                    };
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    linearLayout.post(new Runnable() {
                        @Override
                        public void run() {scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        });
        return view;
    }

    private void dump(IRCCon ircCon, LinearLayout linearLayout) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String response = ircCon.getServerResponse();
                        if (!response.matches("")) {
                            addNewTextView(response, linearLayout);
                            ircCon.setServerResponseToZero();
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

}