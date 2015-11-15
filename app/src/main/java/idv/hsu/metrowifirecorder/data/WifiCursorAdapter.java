package idv.hsu.metrowifirecorder.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.io.IOException;

import idv.hsu.metrowifirecorder.R;

public class WifiCursorAdapter extends CursorAdapter {
    private static final String TAG = WifiCursorAdapter.class.getSimpleName();
    private static final boolean D = true;

    private Context mContext;
    private LayoutInflater inflater;
    private WifiChannels<EnumChannels> channels;
    private DbHelper dbHelper;

    public WifiCursorAdapter(Context context, Cursor cursor, int flag, DbHelper dbHelper) {
        super(context, cursor, 0);
        mContext = context;
        channels = new WifiChannels<EnumChannels>(EnumChannels.class);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dbHelper = dbHelper;
    }

    private static class WifiViewHolder {
        private TextView tv_bssid;
        private TextView tv_ssid;
        private TextView tv_capab;
        private TextView tv_frequency;
        private TextView tv_level;

        private int idx_bssid;
        private int idx_ssid;
        private int idx_capab;
        private int idx_freq;
        private int idx_levle;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.wifi_list_item, null);
        WifiViewHolder holder = new WifiViewHolder();
        holder.tv_bssid = (TextView) view.findViewById(R.id.tv_bssid_value);
        holder.tv_ssid = (TextView) view.findViewById(R.id.tv_ssid_value);
        holder.tv_capab = (TextView) view.findViewById(R.id.tv_capabilities_value);
        holder.tv_frequency = (TextView) view.findViewById(R.id.tv_frequency_value);
        holder.tv_level = (TextView) view.findViewById(R.id.tv_level_value);

        holder.idx_bssid = cursor.getColumnIndexOrThrow(DbSchema.BSSID);
        holder.idx_ssid = cursor.getColumnIndexOrThrow(DbSchema.SSID);
        holder.idx_capab = cursor.getColumnIndexOrThrow(DbSchema.CAPABILITIES);
        holder.idx_freq = cursor.getColumnIndexOrThrow(DbSchema.FREQUENCY);
        holder.idx_levle = cursor.getColumnIndexOrThrow(DbSchema.LEVEL);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        WifiViewHolder holder = (WifiViewHolder) view.getTag();
        holder.tv_bssid.setText(cursor.getString(holder.idx_bssid));
        if (dbHelper.isBssidRedundant(cursor.getString(holder.idx_bssid))) {
            holder.tv_bssid.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            holder.tv_bssid.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        }
        holder.tv_ssid.setText(cursor.getString(holder.idx_ssid));
        holder.tv_capab.setText(cursor.getString(holder.idx_capab));
        StringBuilder channel = new StringBuilder("");
        try {
            int freq = Integer.valueOf(cursor.getString(holder.idx_freq));
            if (channels.getChannel(freq) != null) {
                String tmp = channels.getChannel(freq).toString();
                String[] tmpArray = tmp.split("_");
                channel.append("  (" + tmpArray[1] + " " + tmpArray[2] + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.tv_frequency.setText(cursor.getString(holder.idx_freq) + channel);
        holder.tv_level.setText(cursor.getString(holder.idx_levle));

        if (D) {
            Log.d(TAG, "資料位置: " + cursor.getString(cursor.getColumnIndexOrThrow(DbSchema.STATION)));
        }
    }

}
