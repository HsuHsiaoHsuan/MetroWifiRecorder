package idv.hsu.metrowifirecorder;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import idv.hsu.metrowifirecorder.data.DbHelper;
import idv.hsu.metrowifirecorder.data.DbSchema;
import idv.hsu.metrowifirecorder.data.WifiCursorAdapter;
import idv.hsu.metrowifirecorder.data.WifiListAdapter;
import idv.hsu.metrowifirecorder.data.WifiListItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean D = true;

    // Wifi
    WifiManager wifiManager;
    WifiScanReceiver wifiScanReceiver;

    // UI
    private Spinner sp_line;
    private Spinner sp_station;
    private Button bt_start_stop;
    private Button bt_refresh;
    private ListView lv_data;
    private DbHelper dbHelper;
    private WifiListAdapter adapter;
    private WifiCursorAdapter cursorAdapter;
    private List<WifiListItem> dataList;
    Cursor cursor;
    private boolean registedReceiver = false;
    private String nowSelectedStation = "";

    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbSchema.AP_NAME.put("TPE-Free", "");
        DbSchema.AP_NAME.put("WIFLY", "");
        DbSchema.AP_NAME.put("CHT Wi-Fi Auto", "");
        DbSchema.AP_NAME.put("CHT Wi-Fi(HiNet)", "");
        DbSchema.AP_NAME.put("TPE-Free_CHT", "");
        DbSchema.AP_NAME.put("Freeman", "");

        dbHelper = new DbHelper(this);
        try {
            dbHelper.create();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Unable to copy database.");
        }
        dbHelper.open();
        dbHelper.getWritableDatabase();

        wifiScanReceiver = new WifiScanReceiver();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        sp_line = (Spinner) findViewById(R.id.sp_line);
        sp_station = (Spinner) findViewById(R.id.sp_station);
        bt_start_stop = (Button) findViewById(R.id.bt_start_stop);
        bt_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning) { // scanning before click.
                    if (D) {
                        Log.d(TAG, "Cursor station: " + nowSelectedStation);
                    }
                    unregisterReceiver(wifiScanReceiver);
                    registedReceiver = false;

                    cursor = dbHelper.queryTracking(nowSelectedStation);
                    cursorAdapter =
                            new WifiCursorAdapter(MainActivity.this, cursor, 0, dbHelper);
                    lv_data.setAdapter(cursorAdapter);

                    isScanning = false;
                    sp_line.setEnabled(true);
                    sp_station.setEnabled(true);
                    bt_refresh.setEnabled(false);
                    bt_start_stop.setText("START");
                } else { // ready to scan before click.
                    registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    registedReceiver = true;

                    dataList.clear();
                    adapter = new WifiListAdapter(getLayoutInflater(), dataList, dbHelper);
                    lv_data.setAdapter(adapter);

//                    wifiManager.setWifiEnabled(true);
//                    wifiManager.startScan();
                    startScan();

                    isScanning = true;
                    sp_line.setEnabled(false);
                    sp_station.setEnabled(false);
                    bt_refresh.setEnabled(true);
                    bt_start_stop.setText("STOP");
                }

            }
        });
        bt_refresh = (Button) findViewById(R.id.bt_refresh);
        bt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
//                wifiManager.setWifiEnabled(true);
//                wifiManager.startScan();
            }
        });
        lv_data = (ListView) findViewById(R.id.lv_data);

        ArrayAdapter<CharSequence> adapterLine = ArrayAdapter.createFromResource(this,
                R.array.line, android.R.layout.simple_spinner_item);
        adapterLine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_line.setAdapter(adapterLine);

        sp_line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> adapterStation = null;
                switch (position) {
                    case 0: // 文湖線
                        adapterStation = ArrayAdapter.createFromResource(MainActivity.this,
                                R.array.wenhu, android.R.layout.simple_spinner_item);
                        adapterStation.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 1: // 淡水信義線
                        adapterStation = ArrayAdapter.createFromResource(MainActivity.this,
                                R.array.tamsui, android.R.layout.simple_spinner_item);
                        adapterStation.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 2: // 松山新店線
                        adapterStation = ArrayAdapter.createFromResource(MainActivity.this,
                                R.array.songshan, android.R.layout.simple_spinner_item);
                        adapterStation.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 3: // 中和新蘆線
                        adapterStation = ArrayAdapter.createFromResource(MainActivity.this,
                                R.array.zhonghe, android.R.layout.simple_spinner_item);
                        adapterStation.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        break;
                    case 4: // 板南線
                        adapterStation = ArrayAdapter.createFromResource(MainActivity.this,
                                R.array.bannan, android.R.layout.simple_spinner_item);
                        adapterStation.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        break;
                }
                sp_station.setAdapter(adapterStation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nowSelectedStation = sp_station.getSelectedItem().toString();
                cursor = dbHelper.queryTracking(nowSelectedStation);
                cursorAdapter =
                        new WifiCursorAdapter(MainActivity.this, cursor, 0, dbHelper);
                lv_data.setAdapter(cursorAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lv_data = (ListView) findViewById(R.id.lv_data);
        dataList = new ArrayList<WifiListItem>();
        adapter = new WifiListAdapter(getLayoutInflater(), dataList, dbHelper);
        lv_data.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (D) {
            Log.d(TAG, "onResume()");
        }
//        if (!registedReceiver) {
//            registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//            registedReceiver = true;
//        }
    }

    public void startScan() {
        if (!registedReceiver) {
            registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            registedReceiver = true;
            wifiManager.setWifiEnabled(true);
            wifiManager.startScan();
        } else {
            wifiManager.setWifiEnabled(true);
            wifiManager.startScan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (registedReceiver) {
            unregisterReceiver(wifiScanReceiver);
            registedReceiver = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reset) {
            if (!isScanning) {
                dbHelper.resetStationData(nowSelectedStation);
                cursor = dbHelper.queryTracking(nowSelectedStation);
                cursorAdapter =
                        new WifiCursorAdapter(MainActivity.this, cursor, 0, dbHelper);
                lv_data.setAdapter(cursorAdapter);
            } else {
                Toast.makeText(this, "只能在瀏覽時使用", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        dbHelper.close();
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResults = wifiManager.getScanResults();

            final String nowStation = nowSelectedStation;
            if (D) {
                Log.d(TAG, "result size: " + scanResults.size() +
                           " 現在站台：" + nowStation);
            }
            dataList.clear();
            for (int x = 0; x < scanResults.size(); x++) {
                ScanResult result = scanResults.get(x);

                dataList.add(
                        new WifiListItem(
                                result.BSSID,
                                result.SSID,
                                result.capabilities,
                                result.frequency,
                                result.level));
                if (false) {
                    System.out.println(
                            "BSSID: " + scanResults.get(x).BSSID + ", " +
                                    "SSID: " + scanResults.get(x).SSID + ", " +
                                    "capabilities: " + scanResults.get(x).capabilities + ", " +
                                    "describeContents: " + scanResults.get(x).describeContents() + ", " +
                                    "frequency: " + scanResults.get(x).frequency + ", " +
                                    "level: " + scanResults.get(x).level
                    );
                }
                if (DbSchema.AP_NAME.containsKey(result.SSID)) {
                    ContentValues values = new ContentValues();
                    values.put(DbSchema.BSSID, result.BSSID);
                    values.put(DbSchema.SSID, result.SSID);
                    values.put(DbSchema.CAPABILITIES, result.capabilities);
                    values.put(DbSchema.FREQUENCY, result.frequency);
                    values.put(DbSchema.LEVEL, result.level);
                    values.put(DbSchema.STATION, nowStation);
                    dbHelper.insertTracking(values);
                }
            }
            Toast.makeText(MainActivity.this, "現在站台：" + nowStation, Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }
}
