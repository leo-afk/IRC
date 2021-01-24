package vn.edu.usth.ircclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import vn.edu.usth.ircclient.Activities.MainScreenActivity;
import vn.edu.usth.ircclient.R;

public class ServerAdapter extends BaseAdapter {
    private ArrayList<String> searchArrayList;
    private LayoutInflater mInflater;
    private Context context;

    public ServerAdapter(Context context, ArrayList<String> servers) {
        this.context = context;
        searchArrayList = servers;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_row, parent, false);
        }
        else {
            row = convertView;
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.custom_tv);
        titleTextView.setText(searchArrayList.get(position));

        return row;
    }

    public void add_server_row(String title){
        searchArrayList.add(title);
        notifyDataSetChanged();
    }

    public void remove_server_row(int position){
        searchArrayList.remove(position);
        notifyDataSetChanged();
    }
}
