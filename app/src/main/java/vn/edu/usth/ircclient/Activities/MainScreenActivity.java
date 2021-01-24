package vn.edu.usth.ircclient.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
import java.nio.channels.Channel;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.transform.sax.TemplatesHandler;

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
    private IRCCon ircCon = new IRCCon();
    private String host = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.drawer_list);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.add_server_header, listView, false);
        listView.addHeaderView(header, null, false);


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        serverAdapter = new ServerAdapter(this, servers);
        listView.setAdapter(serverAdapter);
        listView.setOnItemClickListener(this);
        serverAdapter.add_server_row("Help");
        serverAdapter.add_server_row("Quit");


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
        addTab("Server");


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

            default:
                host = title;
        }

        connectServer(host);
        enterAccount(ircCon);
    }


    public void checkNick() {
        TextView tv = findViewById(R.id.nickname_header);
        tv.setText("No nick associated.\n\nServer name: " + host);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ircCon.getCheckNick()) {
                            tv.setText("@" + ircCon.getNickName() + "\n\nServer name: " + host);
                            ircCon.setCheckNick(false);
                        }
                    }
                });

            }
        }, 0, 1000);
    }

    public void checkdm() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ircCon.getDm()) {
                            addTab(ircCon.getDmUser());
                            ircCon.setDm(false);
                        }
                    }
                });

            }
        }, 0, 1000);
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

    public void addTab(String title) {
        ChannelFragment channelFragment = new ChannelFragment();
        channelFragment.setTitle(title);
        View view = channelFragment.getView();
        viewPagerAdapter.addFrag(channelFragment, title);
        viewPagerAdapter.notifyDataSetChanged();
        ChannelList.add(title);
        viewPager.setOffscreenPageLimit(ChannelList.size());
        viewPager.setCurrentItem(viewPagerAdapter.getCount() - 1);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public ViewPagerAdapter getViewPagerAdapter() {
        return viewPagerAdapter;
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
                HashMap channelMap = ircCon.getChannelMap();
                String channelTitle = editChannel.getText().toString();
                if (channelTitle.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a channel", Toast.LENGTH_SHORT).show();
                } else if (!channelTitle.startsWith("#")) {
                    Toast.makeText(MainScreenActivity.this, "Channel name must start with #", Toast.LENGTH_SHORT).show();
                } else if (channelTitle.contains(" ")) {
                    Toast.makeText(MainScreenActivity.this, "Channel name must not have spaces", Toast.LENGTH_SHORT).show();
                } else if (channelMap.containsKey(channelTitle)) {
                    int position = 0;
                    for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
                        Log.i("pagetitle", String.valueOf(viewPagerAdapter.getPageTitle(i)));
                        if (String.valueOf(viewPagerAdapter.getPageTitle(i)).matches(channelTitle)) {
                            position = i;
                        }
                    }
                    viewPager.setCurrentItem(position);
                    dialog.dismiss();
                    Toast.makeText(MainScreenActivity.this, "Joined " + channelTitle, Toast.LENGTH_SHORT).show();
                } else {
                    addTab(channelTitle);
                    AsyncTask<Void, Void, Void> channel_task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            ircCon.write("join " + channelTitle);
                            return null;
                        }
                    };
                    channel_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    dialog.dismiss();
                    Toast.makeText(MainScreenActivity.this, "Joined " + channelTitle, Toast.LENGTH_SHORT).show();
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
                    ircCon.setNickName(nickname);
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
                    checkdm();
                    checkNick();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkdm();
                checkNick();
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
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.popup_change_nickname);

                TextView cancel = (TextView) dialog.findViewById(R.id.cancel_change_nickname);
                TextView change = (TextView) dialog.findViewById(R.id.change_button);
                EditText editNickname = (EditText) dialog.findViewById(R.id.input_change_nickname);
                dialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_nick = editNickname.getText().toString();
                        if (!new_nick.matches("")) {
                            ChannelFragment cf = new ChannelFragment();
                            if (cf.isSpecialCharacter(new_nick)) {
                                Toast.makeText(MainScreenActivity.this, "Nickname cant contain special characters", Toast.LENGTH_SHORT).show();
                                editNickname.getText().clear();
                            } else {
                                AsyncTask<Void, Void, Void> nickname_task = new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        ircCon.write("nick " + new_nick);
                                        return null;
                                    }
                                };
                                nickname_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                dialog.dismiss();
                                ircCon.setNickName(new_nick);
                            }

                        } else {
                            Toast.makeText(MainScreenActivity.this, "Please enter a nickname.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }

            case R.id.add_channel: {
                addChannel();
                return true;
            }

            case R.id.leave: {
                HashMap channelMap = ircCon.getChannelMap();
                int currentFrag = viewPager.getCurrentItem();
                String currentFragTitle = viewPagerAdapter.getPageTitle(currentFrag);
                if (currentFrag == 0) {
                    Toast.makeText(this, "Cannot exit this channel.", Toast.LENGTH_SHORT).show();
                } else {
                    AsyncTask<Void, Void, Void> part_task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            ircCon.write("part " + currentFragTitle);
                            return null;
                        }
                    };
                    part_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    ircCon.removeFromChannelMap(currentFragTitle);
                    viewPagerAdapter.removeFrag(currentFrag);
                    viewPagerAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(currentFrag - 1);
                }
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
        selectItem(position);
        if (position == 0) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.popup_help);
            TextView gotit = (TextView) dialog.findViewById(R.id.gotit);
            dialog.show();
            gotit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } else {
            AsyncTask<Void, Void, Void> quit_task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ircCon.write("quit");
                    return null;
                }
            };
            quit_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            finish();
        }
    }


    public void selectItem(int position) {
        listView.setItemChecked(position, true);
    }

    @Override
    public void onBackPressed() {
        AsyncTask<Void, Void, Void> quit_task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ircCon.write("quit");
                return null;
            }
        };
        super.onBackPressed();
    }
}