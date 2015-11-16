package idv.hsu.metrowifirecorder.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import idv.hsu.metrowifirecorder.R;

public class WifiListAdapter extends BaseAdapter {
    private static final String TAG = WifiListAdapter.class.getSimpleName();
    private static final boolean D = true;

    private LayoutInflater inflater;
    private List<WifiListItem> dataList;
    private WifiChannels<EnumChannels> channles;
    private DbHelper dbHelper;

    public WifiListAdapter(LayoutInflater inflater, List<WifiListItem> list, DbHelper dbHelper) {
        this.inflater = inflater;
        dataList = list;
        channles = new WifiChannels<EnumChannels>(EnumChannels.class);
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class WifiViewHolder {
        private TextView tv_bssid;
        private TextView tv_manufacturer;
        private TextView tv_ssid;
        private TextView tv_capab;
        private TextView tv_frequency;
        private TextView tv_level;
        public WifiViewHolder(View view) {
            tv_bssid = (TextView) view.findViewById(R.id.tv_bssid_value);
            tv_manufacturer = (TextView) view.findViewById(R.id.tv_manufacturer);
            tv_ssid = (TextView) view.findViewById(R.id.tv_ssid_value);
            tv_capab = (TextView) view.findViewById(R.id.tv_capabilities_value);
            tv_frequency = (TextView) view.findViewById(R.id.tv_frequency_value);
            tv_level = (TextView) view.findViewById(R.id.tv_level_value);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.wifi_list_item, null);
            WifiViewHolder holder = new WifiViewHolder(rowView);
            rowView.setTag(holder);
        }
        final WifiViewHolder holder = (WifiViewHolder) rowView.getTag();
        WifiListItem wifi = dataList.get(position);
        holder.tv_bssid.setText(wifi.getBssid());
        holder.tv_manufacturer.setText(dbHelper.queryManufacture(wifi.getBssid()));
        holder.tv_ssid.setText(wifi.getSsid());
        holder.tv_capab.setText(wifi.getCapabilities());
        StringBuilder channel = new StringBuilder("");
        try {
            if (channles.getChannel(wifi.getFrequency()) != null) {
                String tmp = channles.getChannel(wifi.getFrequency()).toString();
                String[] tmpArray = tmp.split("_");
                channel.append("  (" + tmpArray[1] + " " + tmpArray[2] + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.tv_frequency.setText(String.valueOf(wifi.getFrequency()) + channel);
        holder.tv_level.setText(String.valueOf(wifi.getLevel()));
        return rowView;
    }
}
