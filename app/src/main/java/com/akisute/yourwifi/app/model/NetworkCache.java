package com.akisute.yourwifi.app.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NetworkCache {

    // Key = BssId
    private final Cache<String, Network> mNetworks = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public int size() {
        return (int) mNetworks.size();
    }

    public void put(Network network) {
        if (network != null) {
            mNetworks.put(network.getBssid(), network);
        }
    }

    public void clear() {
        mNetworks.invalidateAll();
    }

    public List<Network> getAllNetworkList() {
        return getAllNetworkList(NetworkComparators.DEFAULT);
    }

    public List<Network> getAllNetworkList(Comparator<Network> comparator) {
        return Ordering.from(comparator).sortedCopy(mNetworks.asMap().values());
    }
}
