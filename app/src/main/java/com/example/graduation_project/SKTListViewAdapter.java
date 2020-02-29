package com.example.graduation_project;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SKTListViewAdapter extends BaseAdapter {

    static int get_pos;
    private ArrayList<SKTListVO> listVO = new ArrayList();


    public SKTListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listVO.size();
    }

    @Override
    public Object getItem(int position) {
        return listVO.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        get_pos = pos;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_skt_listview, parent, false);
        }

        final ImageView img_view = (ImageView) convertView.findViewById(R.id.imageView);
        final TextView start_station_name_tv = (TextView) convertView.findViewById(R.id.skt_listview_start_station_name);
        final TextView destination_station_name_tv = (TextView) convertView.findViewById(R.id.skt_listview_desti_station_name);
        final TextView start_station_line_tv = (TextView) convertView.findViewById(R.id.skt_listview_line_number);
        final TextView station_remaining_tv = (TextView) convertView.findViewById(R.id.skt_listview_station_remain);
        final SKTListVO listViewItem = listVO.get(position);

        start_station_name_tv.setText(listViewItem.getStart_station_name());
        destination_station_name_tv.setText(listViewItem.getDestination_station_name());
        start_station_line_tv.setText(listViewItem.getStart_station_line());
        station_remaining_tv.setText(listViewItem.getStation_remaining());
        if (listViewItem.getImg().equals("1")) {
            img_view.setImageResource(R.drawable.train);
        }
        else if (listViewItem.getImg().equals("2")) {
            img_view.setImageResource(R.drawable.bus);
        }
        else if (listViewItem.getImg().equals("3")) {
            img_view.setImageResource(R.drawable.walk);
        }

        return convertView;
    }

    public void addVO(String start_station_name, String destination_station_name, String start_station_line, String station_remaining, String img)
    {
        SKTListVO item = new SKTListVO();
        item.setStart_station_name(start_station_name);
        item.setdestination_station_name(destination_station_name);
        item.setStart_station_line(start_station_line);
        item.setStation_remaining(station_remaining);
        item.setImg(img);



        listVO.add(item);
    }

    public void clear() { listVO.clear();}


}
