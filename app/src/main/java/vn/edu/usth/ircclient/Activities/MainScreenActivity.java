package vn.edu.usth.ircclient.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

import vn.edu.usth.ircclient.Adapter.ChatFragmentAdapter;
import vn.edu.usth.ircclient.Adapter.ServerAdapter;
import vn.edu.usth.ircclient.Fragments.ChatFragment;
import vn.edu.usth.ircclient.R;

public class MainScreenActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ChatFragmentAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String nickname = "AndroidUser";

    private DrawerLayout drawerLayout;
    private ListView listView;
    private ArrayList<String> servers = new ArrayList<>();
    private ActionBarDrawerToggle drawerListener;
    private ServerAdapter serverAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.drawer_list);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.add_server_header, listView, false);
        listView.addHeaderView(header, null, false);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServer();
            }
        });

        serverAdapter = new ServerAdapter(this, servers);
        listView.setAdapter(serverAdapter);
        listView.setOnItemClickListener(this);


        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        setIDs();
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
    }

    private void setIDs() {
        adapter = new ChatFragmentAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.chat_pager);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.channel_tab);
        viewPager.setOffscreenPageLimit(10);
    }

    private void addPage(String title) {
        ChatFragment chatFragment = new ChatFragment();
        adapter.addFragment(chatFragment, title);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) {
            tabLayout.setupWithViewPager(viewPager);
        }
        viewPager.setCurrentItem(adapter.getCount() - 1);
    }

    private void removePage() {
        int position = viewPager.getCurrentItem();
        adapter.removeFragment(position);
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(adapter.getCount() - 1);
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
                    addPage(channelTitle);
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

    private void changeNickname() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_change_nickname);

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel_change_nickname);
        TextView change = (TextView) dialog.findViewById(R.id.change_nickname);
        EditText editNickname = (EditText) dialog.findViewById(R.id.input_nickname);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nicknameTemp = editNickname.getText().toString();
                if (nicknameTemp.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                } else {
                    nickname = nicknameTemp;
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

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerListener.onOptionsItemSelected(item)){
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

            case R.id.exit: {
                this.finishAffinity();
                System.exit(0);
                return true;
            }

            case R.id.nickname: {
                changeNickname();
                return true;
            }

            case R.id.leave: {
                removePage();
                return true;
            }

            case R.id.add_channel:{
                addChannel();
            }

            default: {
                super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }




    // Drawer stuff



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= listView.getHeaderViewsCount();
        Toast.makeText(this, servers.get(position)+" was selected", Toast.LENGTH_LONG).show();
        selectItem(position);
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public void selectItem(int position){
        listView.setItemChecked(position, true);
        String title = (String) servers.get(position);
        setTitle(title);
    }

}