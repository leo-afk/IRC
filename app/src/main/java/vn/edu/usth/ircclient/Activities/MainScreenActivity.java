package vn.edu.usth.ircclient.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import vn.edu.usth.ircclient.Adapter.ServerAdapter;
import vn.edu.usth.ircclient.Adapter.ViewPagerAdapter;
import vn.edu.usth.ircclient.Classes.IRCCon;
import vn.edu.usth.ircclient.Fragments.ChannelFragment;
import vn.edu.usth.ircclient.R;

public class MainScreenActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private DrawerLayout drawerLayout;
    private ListView listView;
    private ArrayList<String> servers = new ArrayList<>();
    private ActionBarDrawerToggle drawerListener;
    private ServerAdapter serverAdapter;
    private ViewPagerAdapter viewPagerAdapter;
    private ChannelFragment channelFragment;

    private IRCCon ircCon = new IRCCon();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.drawer_list);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.add_server_header, listView, false);
        listView.addHeaderView(header, null, false);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServer();
            }
        });


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        serverAdapter = new ServerAdapter(this, servers);
        listView.setAdapter(serverAdapter);
        listView.setOnItemClickListener(this);


        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createFirstPage();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    private void createFirstPage() {
        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        serverAdapter.add_server_row(title);
        addTab(title);

        String host = null;
        switch (title) {
            case "QuakeNet":
                host = "cymru.us.quakenet.org";
                break;
            case "freenode":
                host = "chat.freenode.net";
                break;
            case "EpiKnet":
                host = "irc.epiknet.org";
                break;
            case "UnderNet":
                host = "irc.undernet.org";
                break;

            case "KottNet":
                host = "alice.kottnet.net";
                break;
        }

        connectServer(host);
        enterAccount(ircCon);
    }

    private void addServer() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_server);

        TextView freenode = (TextView) dialog.findViewById(R.id.freenode_popup);
        TextView epiknet = (TextView) dialog.findViewById(R.id.epiknet_popup);
        TextView quakenet = (TextView) dialog.findViewById(R.id.quakenet_popup);
        TextView undernet = (TextView) dialog.findViewById(R.id.undernet_popup);
        TextView kottnet = (TextView) dialog.findViewById(R.id.kottnet_popup);


        freenode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAdapter.add_server_row("freenode");
                dialog.dismiss();
            }
        });

        epiknet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAdapter.add_server_row("EpiKnet");
                dialog.dismiss();
            }
        });

        quakenet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAdapter.add_server_row("QuakeNet");
                dialog.dismiss();
            }
        });

        undernet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAdapter.add_server_row("Undernet");
                dialog.dismiss();
            }
        });

        kottnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAdapter.add_server_row("kottnet");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void connectServer(String host) {
        AsyncTask<Void, Void, Void> connect_task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ircCon.init(host);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        connect_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private ArrayList<String> ChannelList = new ArrayList<>();

    private void addTab(String title) {
        ChannelFragment channelFragment = new ChannelFragment();
        View view = channelFragment.getView();
        viewPagerAdapter.addFrag(channelFragment, title);
        viewPagerAdapter.notifyDataSetChanged();
        ChannelList.add(title);
        viewPager.setOffscreenPageLimit(ChannelList.size());
    }

    private void addChannel() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_join_channel);

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_join_channel);
        TextView join = (TextView) dialog.findViewById(R.id.join_channel);
        EditText editChannel = (EditText) dialog.findViewById(R.id.input_channel);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelTitle = editChannel.getText().toString();
                if (channelTitle.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a channel", Toast.LENGTH_SHORT).show();
                } else {
                    addTab(channelTitle);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void enterAccount(IRCCon ircCon) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_enter_account);

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_enter);
        TextView enter = (TextView) dialog.findViewById(R.id.enter);
        EditText editUsername = (EditText) dialog.findViewById(R.id.edit_username);
        EditText editNickname = (EditText) dialog.findViewById(R.id.edit_nickname);
        EditText editRealname = (EditText) dialog.findViewById(R.id.edit_realname);
        dialog.show();

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String username = editUsername.getText().toString();
               String nickname = editNickname.getText().toString();
               String realname = editRealname.getText().toString();
               if (!username.matches("") && !nickname.matches("") && !realname.matches("")) {
                   AsyncTask<Void, Void, Void> init_account = new AsyncTask<Void, Void, Void>() {
                       @Override
                       protected Void doInBackground(Void... voids) {
                           Log.i("kien", username + nickname + realname);
                           ircCon.write("user " + username + " 0 * :" + realname);
                           ircCon.write("nick " + nickname);
                           return null;
                       }
                   };
                   init_account.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                   dialog.dismiss();
               } else {
                   Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
               }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ircCon.write("quit");
                dialog.dismiss();
            }
        });
    }

    public IRCCon getIrcCon() {
        return ircCon;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.setting: {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            }

            case R.id.about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }

            case R.id.nickname: {
                return true;
            }

            case R.id.leave: {
                return true;
            }

            case R.id.add_channel: {
                addChannel();
            }

            default: {
                super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= listView.getHeaderViewsCount();
        Toast.makeText(this, servers.get(position) + " was selected", Toast.LENGTH_LONG).show();
        selectItem(position);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void selectItem(int position) {
        listView.setItemChecked(position, true);
        String title = (String) servers.get(position);
        setTitle(title);
    }

}