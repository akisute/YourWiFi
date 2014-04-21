package com.akisute.yourwifi.app.model;


import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.collections4.comparators.ReverseComparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NetworkCache {

    public static final Comparator<Network> COMPARATOR_UPDATED_AT_ASC = new UpdatedAtComparator();
    public static final Comparator<Network> COMPARATOR_UPDATED_AT_DESC = new ReverseComparator<Network>(new UpdatedAtComparator());
    public static final Comparator<Network> COMPARATOR_DEFAULT = new ComparatorChain<Network>(Arrays.asList(new SsidComparator(), new BssidComparator()));

    private static final long CACHE_EXPIRE_MILLIS = 30 * 1000;

    private final Map<String, Network> mNetworks = new HashMap<String, Network>(); // Key is BssId

    public int size() {
        return mNetworks.size();
    }

    public void put(Network network) {
        Network currentNetwork = mNetworks.get(network.getBssid());
        if (currentNetwork != null) {
            currentNetwork.update(network);
        } else {
            mNetworks.put(network.getBssid(), network);
        }
    }

    public void clear() {
        mNetworks.clear();
    }

    public void purgeOutdatedNetworks() {
        Date currentDate = new Date();
        Iterator<Network> networkIterator = mNetworks.values().iterator();
        while (networkIterator.hasNext()) {
            Network network = networkIterator.next();
            Date expireDate = new Date(network.getUpdatedAt().getTime() + CACHE_EXPIRE_MILLIS);
            if (currentDate.after(expireDate)) {
                networkIterator.remove();
            }
        }
    }

    public List<Network> getAllNetworkList() {
        return getAllNetworkList(COMPARATOR_DEFAULT);
    }

    public List<Network> getAllNetworkList(Comparator<Network> comparator) {
        Network[] array = new Network[mNetworks.size()];
        array = mNetworks.values().toArray(array);
        List<Network> networkList = Arrays.asList(array);
        Collections.sort(networkList, comparator);
        return networkList;
    }

    public static class UpdatedAtComparator implements Comparator<Network> {

        @Override
        public int compare(Network network, Network network2) {
            return network.getUpdatedAt().compareTo(network2.getUpdatedAt());
        }
    }

    public static class BssidComparator implements Comparator<Network> {

        @Override
        public int compare(Network network, Network network2) {
            String bssid1 = network.getBssid();
            String bssid2 = network2.getBssid();
            return bssid1.compareTo(bssid2);
        }
    }

    public static class SsidComparator implements Comparator<Network> {

        @Override
        public int compare(Network network, Network network2) {
            String ssid1 = network.getSsid();
            String ssid2 = network2.getSsid();
            ssid1 = (ssid1 == null) ? "" : ssid1;
            ssid2 = (ssid2 == null) ? "" : ssid2;
            return ssid1.compareTo(ssid2);
        }
    }
}
