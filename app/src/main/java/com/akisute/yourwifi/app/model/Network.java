package com.akisute.yourwifi.app.model;

import android.net.wifi.ScanResult;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Network {

    // CryptoType enum values
    public static final int CRYPTO_NONE = 0;
    public static final int CRYPTO_WEP = 1000;
    public static final int CRYPTO_WPA = 2000;
    public static final int CRYPTO_WPA2 = 3000;
    // NetworkType enum values
    public static final int NETWORK_UNKNOWN = 0;
    public static final int NETWORK_WIFI = 1000;
    public static final int NETWORK_CELLULAR = 2000;
    public static final int NETWORK_CELLULAR_GSM = 2001;
    public static final int NETWORK_CELLULAR_CDMA = 2002;
    // Channel special values
    public static final int CHANNEL_UNKNOWN = Integer.MIN_VALUE;

    private static final Map<Integer, Integer> FREQUENCY_TO_CHANNEL;
    private static final Map<Integer, String> NETWORK_TO_NAME;

    static {
        {
            Map<Integer, Integer> buffer = new HashMap<Integer, Integer>();

            for (int i = 237; i <= 255; i++) {
                buffer.put(2312 + 5 * (i - 237), i);
            }

            for (int i = 0; i <= 13; i++) {
                buffer.put(2407 + (5 * i), i);
            }

            buffer.put(2484, 14);

            buffer.put(5170, 34);
            buffer.put(5180, 36);
            buffer.put(5190, 38);
            buffer.put(5200, 40);
            buffer.put(5210, 42);
            buffer.put(5220, 44);
            buffer.put(5230, 46);
            buffer.put(5240, 48);
            buffer.put(5260, 52);
            buffer.put(5280, 56);
            buffer.put(5300, 58);
            buffer.put(5320, 60);

            buffer.put(5500, 100);
            buffer.put(5520, 104);
            buffer.put(5540, 108);
            buffer.put(5560, 112);
            buffer.put(5570, 116);
            buffer.put(5600, 120);
            buffer.put(5620, 124);
            buffer.put(5640, 128);
            buffer.put(5660, 132);
            buffer.put(5680, 136);
            buffer.put(5700, 140);

            buffer.put(5745, 149);
            buffer.put(5765, 153);
            buffer.put(5785, 157);
            buffer.put(5805, 161);
            buffer.put(5825, 165);

            FREQUENCY_TO_CHANNEL = Collections.unmodifiableMap(buffer);
        }
        {
            Map<Integer, String> buffer = new HashMap<Integer, String>();
            buffer.put(NETWORK_UNKNOWN, "Unknown");
            buffer.put(NETWORK_WIFI, "WiFi");
            buffer.put(NETWORK_CELLULAR, "Cellular");
            buffer.put(NETWORK_CELLULAR_GSM, "GSM");
            buffer.put(NETWORK_CELLULAR_CDMA, "CDMA");
            NETWORK_TO_NAME = Collections.unmodifiableMap(buffer);
        }
    }

    private String mBssid;
    private String mSsid; // Could be null when Stealth Network
    private int mFrequency; // In MHz
    private String mCapabilities;
    private int mLevel; // Signal Level, in dBm
    private long mTimestamp; // TSF timestamp, in ms
    private int mNetworkType; // enum value

    private int mCryptoType; // enum value
    private String mDescription;
    private Date mUpdatedAt;

    public static Network newInstance(ScanResult scanResult) {
        Network network = new Network();
        network.update(scanResult);
        if (!network.validate()) {
            return null;
        }
        return network;
    }

    public static Network newInstance(String bssid, String ssid, int frequency, String capabilities, int level, long timestamp, int networkType) {
        Network network = new Network();
        network.update(bssid, ssid, frequency, capabilities, level, timestamp, networkType);
        if (!network.validate()) {
            return null;
        }
        return network;
    }

    private Network() {
    }

    public String getBssid() {
        return mBssid;
    }

    public String getSsid() {
        return mSsid;
    }

    public int getFrequency() {
        return mFrequency;
    }

    public String getCapabilities() {
        return mCapabilities;
    }

    public int getLevel() {
        return mLevel;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public int getNetworkType() {
        return mNetworkType;
    }

    public int getCryptoType() {
        return mCryptoType;
    }

    public int getChannel() {
        Integer channel = FREQUENCY_TO_CHANNEL.get(mFrequency);
        if (channel != null) {
            return channel.intValue();
        } else {
            return CHANNEL_UNKNOWN;
        }
    }

    public String getDescription() {
        return mDescription;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public void update(Network network) {
        if (network == null) {
            return;
        }
        update(network.getBssid(), network.getSsid(), network.getFrequency(), network.getCapabilities(), network.getLevel(), network.getTimestamp(), network.getNetworkType());
    }

    public void update(ScanResult scanResult) {
        if (scanResult == null) {
            return;
        }
        update(scanResult.BSSID, scanResult.SSID, scanResult.frequency, scanResult.capabilities, scanResult.level, scanResult.timestamp, NETWORK_WIFI);
    }

    public void update(final String bssid, final String ssid, final int frequency, final String capabilities, final int level, final long timestamp, final int networkType) {
        // Ignore old scan results
        if (mTimestamp > timestamp) {
            return;
        }

        mBssid = (bssid == null) ? null : bssid.toLowerCase();
        mSsid = ssid;
        mFrequency = frequency;
        mCapabilities = (capabilities == null) ? "" : capabilities;
        mLevel = level;
        mTimestamp = timestamp;
        mNetworkType = networkType;

        calculateFields();
    }

    private void calculateFields() {
        /*
        Determine CryptoType here.
        Overwrite with weakest possible crypto if there is multiple crypto type available for a Network.
         */
        mCryptoType = CRYPTO_NONE;
        if (mCapabilities.contains("[WPA2")) {
            mCryptoType = CRYPTO_WPA2;
        }
        if (mCapabilities.contains("[WPA")) {
            mCryptoType = CRYPTO_WPA;
        }
        if (mCapabilities.contains("[WEP")) {
            mCryptoType = CRYPTO_WEP;
        }

        String shortCapabilitiesString;
        if (mNetworkType == NETWORK_WIFI) {
            int semicolon = mCapabilities.lastIndexOf(";");
            if (semicolon > 0) {
                shortCapabilitiesString = mCapabilities.substring(0, semicolon);
            } else {
                shortCapabilitiesString = mCapabilities;
            }
        } else if (mCapabilities.length() > 16) {
            shortCapabilitiesString = mCapabilities.replaceAll("(\\[\\w+)\\-.*?\\]", "$1]");
        } else {
            shortCapabilitiesString = mCapabilities;
        }

        String ssidString;
        if (mSsid == null) {
            if (mNetworkType == NETWORK_WIFI) {
                ssidString = "(Stealth AP)";
            } else {
                ssidString = "(Cellular)";
            }
        } else {
            ssidString = mSsid;
        }

        String channelString;
        if (mNetworkType == NETWORK_WIFI) {
            int channel = getChannel();
            if (channel == CHANNEL_UNKNOWN) {
                channelString = String.format("%dMHz", mFrequency);
            } else {
                channelString = String.format("channel %d", channel);
            }
        } else {
            channelString = String.format("%dMHz", mFrequency);
        }

        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder
                .append(" | ")
                .append(ssidString)
                .append(" | ")
                .append(mBssid)
                .append(" - ")
                .append(NETWORK_TO_NAME.get(mNetworkType))
                .append(" ")
                .append(channelString)
                .append(" - ")
                .append(shortCapabilitiesString);
        mDescription = descriptionBuilder.toString();

        mUpdatedAt = new Date();
    }

    private boolean validate() {
        return mBssid != null;
    }
}
