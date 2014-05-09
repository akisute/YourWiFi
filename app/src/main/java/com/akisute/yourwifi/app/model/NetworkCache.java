package com.akisute.yourwifi.app.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Network getNetwork(String bssid) {
        return mNetworks.asMap().get(bssid);
    }

    public List<Network> getAllNetworkList() {
        return getAllNetworkList(NetworkComparators.DEFAULT);
    }

    public List<Network> getAllNetworkList(Comparator<Network> comparator) {
        return Ordering.from(comparator).sortedCopy(mNetworks.asMap().values());
    }

    public Essid getEssid(String ssid) {
        Map<String, Essid> essidMap = getEssidMap();
        return essidMap.get(ssid);
    }

    public List<Essid> getAllEssidList() {
        return getAllEssidList(EssidComparators.DEFAULT);
    }

    public List<Essid> getAllEssidList(Comparator<Essid> comparator) {
        Map<String, Essid> essidMap = getEssidMap();
        return Ordering.from(comparator).sortedCopy(essidMap.values());
    }

    private Map<String, Essid> getEssidMap() {
        Multimap<String, Network> multimap = HashMultimap.create(size(), size() / 2);
        for (Network network : mNetworks.asMap().values()) {
            multimap.put(network.getSsid(), network);
        }
        Map<String, Essid> resultMap = new HashMap<String, Essid>(multimap.keySet().size());
        for (Map.Entry<String, Collection<Network>> entry : multimap.asMap().entrySet()) {
            Essid essid = Essid.newInstance(entry.getValue());
            resultMap.put(entry.getKey(), essid);
        }
        return resultMap;
    }
}
