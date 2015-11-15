package idv.hsu.metrowifirecorder.data;

import java.util.HashMap;

public class DbSchema {
    // DATABASE
    public static final String DB_NAME = "metro";

    // TABLE TPE
    public static final String TABLE_TPE = "tpe";
    public static final String _ID = "_id";
    public static final String LINE = "LINE";
    public static final String CODE = "CODE";
    public static final String NAME_CHT = "NAME_CHT";
    public static final String NAME_ENG = "NAME_ENG";
    public static final String NAME_JA = "NAME_JA";

    // TABLE MAC LOG
    public static final String TABLE_LOG = "mac_log";
    public static final String BSSID = "BSSID";
    public static final String SSID = "SSID";
    public static final String CAPABILITIES = "CAPABILITIES";
    public static final String FREQUENCY = "FREQUENCY";
    public static final String LEVEL = "LEVEL";
    public static final String TIME = "TIME";
    public static final String LOCATION = "LOCATION";

    // TABLE TRACKING LOG
    public static final String TABLE_TRACKING = "log";
    public static final String STATION = "STATION";

    // TABLE MANUFACTURE
    public static final String TABLE_MANUFACTURE = "mac_manufacture";
    public static final String MAC = "MAC";
    public static final String MANUFACTURE = "MANUFACTURER";
    public static final String ADDRESS = "ADDRESS";

    public static final HashMap<String, String> AP_NAME = new HashMap<>();
}
