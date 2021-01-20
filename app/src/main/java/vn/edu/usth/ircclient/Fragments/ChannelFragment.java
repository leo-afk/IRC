package vn.edu.usth.ircclient.Fragments;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import vn.edu.usth.ircclient.R;

public class ChannelFragment extends Fragment {
    private String message;


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
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_screen);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editText.getText().toString();
                editText.getText().clear();
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
        return view;
    }

    public String getMessage() {
        return message;
    }
}