package idv.hsu.metrowifirecorder.data;

public class WifiListItem {
    private String bssid;
    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;

    public WifiListItem (String bssid, String ssid, String capabilities, int frequency, int level) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.capabilities = capabilities;
        this.frequency = frequency;
        this.level = level;
    }

    public String getBssid() {
        return bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getLevel() {
        return level;
    }
}
