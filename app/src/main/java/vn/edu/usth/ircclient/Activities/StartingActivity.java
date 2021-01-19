package vn.edu.usth.ircclient.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import vn.edu.usth.ircclient.R;

public class StartingActivity extends AppCompatActivity {
    Button freenode;
    Button epiknet;
    Button quakenet;
    Button undernet;
    Button kottnet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        setIDs();
        setEvents();
    }

    private void setIDs() {
        freenode = (Button) findViewById(R.id.freenode_button);
        epiknet = (Button) findViewById(R.id.epiknet_button);
        quakenet = (Button) findViewById(R.id.quakenet_button);
        undernet = (Button) findViewById(R.id.undernet_button);
        kottnet = (Button) findViewById(R.id.kottnet_button);
    }

    private void setEvents() {
        freenode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainScreen("freenode");
            }
        });

        epiknet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainScreen("EpiKnet");
            }
        });

        quakenet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainScreen("QuakeNet");
            }
        });

        undernet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainScreen("UnderNet");
            }
        });

        kottnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainScreen("KottNet");
            }
        });
    }

    private void startMainScreen(String title) {
        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.starting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            default: {
                super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}