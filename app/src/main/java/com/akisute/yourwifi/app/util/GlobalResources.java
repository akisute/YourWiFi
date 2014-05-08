package com.akisute.yourwifi.app.util;

import android.content.res.Resources;

import com.akisute.yourwifi.app.R;
import com.akisute.yourwifi.app.model.Network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class GlobalResources {

    @Inject
    Resources mResources;

    private final Map<Integer, String> mNetworkTypeName;
    private final Map<Integer, String> mCryptoTypeName;
    private final Map<Integer, Integer> mCryptoTypeFontColor;

    @Inject
    public GlobalResources(Resources resources) {
        mResources = resources;
        {
            Map<Integer, String> buffer = new HashMap<Integer, String>();
            buffer.put(Network.NETWORK_UNKNOWN, mResources.getString(R.string.network_unknown));
            buffer.put(Network.NETWORK_WIFI, mResources.getString(R.string.network_wifi));
            buffer.put(Network.NETWORK_CELLULAR, mResources.getString(R.string.network_cellular));
            buffer.put(Network.NETWORK_CELLULAR_GSM, mResources.getString(R.string.network_cellular_gsm));
            buffer.put(Network.NETWORK_CELLULAR_CDMA, mResources.getString(R.string.network_cellular_cdma));
            mNetworkTypeName = Collections.unmodifiableMap(buffer);
        }
        {
            Map<Integer, String> buffer = new HashMap<Integer, String>();
            buffer.put(Network.CRYPTO_NONE, mResources.getString(R.string.crypto_none));
            buffer.put(Network.CRYPTO_WEP, mResources.getString(R.string.crypto_wep));
            buffer.put(Network.CRYPTO_WPA, mResources.getString(R.string.crypto_wpa));
            buffer.put(Network.CRYPTO_WPA2, mResources.getString(R.string.crypto_wpa2));
            mCryptoTypeName = Collections.unmodifiableMap(buffer);
        }
        {
            Map<Integer, Integer> buffer = new HashMap<Integer, Integer>();
            buffer.put(Network.CRYPTO_NONE, mResources.getColor(R.color.font_crypto_none));
            buffer.put(Network.CRYPTO_WEP, mResources.getColor(R.color.font_crypto_wep));
            buffer.put(Network.CRYPTO_WPA, mResources.getColor(R.color.font_crypto_wpa));
            buffer.put(Network.CRYPTO_WPA2, mResources.getColor(R.color.font_crypto_wpa2));
            mCryptoTypeFontColor = Collections.unmodifiableMap(buffer);
        }
    }

    public String getNetworkTypeName(int networkType) {
        return mNetworkTypeName.get(networkType);
    }

    public String getCryptoTypeName(int cryptoType) {
        return mCryptoTypeName.get(cryptoType);
    }

    public int getCryptoTypeFontColor(int cryptoType) {
        return mCryptoTypeFontColor.get(cryptoType);
    }
}
