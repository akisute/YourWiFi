package com.akisute.yourwifi.app.model;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NetworkCache {

    public static final Comparator COMPARATOR_UPDATED_AT_ASC = new UpdatedAtComparator(true);
    public static final Comparator COMPARATOR_UPDATED_AT_DESC = new UpdatedAtComparator(false);

    private static final long CACHE_EXPIRE_MILLIS = 30 * 1000;

    private Map<String, Network> mNetworks = new HashMap<String, Network>(); // Key is BssId

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
            if (expireDate.after(currentDate)) {
                networkIterator.remove();
            }
        }
    }

    public List<Network> getAllNetworkList() {
        // Use updatedAt comparator as default
        return getAllNetworkList(COMPARATOR_UPDATED_AT_DESC);
    }

    public List<Network> getAllNetworkList(Comparator<Network> comparator) {
        Network[] array = new Network[mNetworks.size()];
        array = mNetworks.values().toArray(array);
        List<Network> networkList = Arrays.asList(array);
        Collections.sort(networkList, comparator);
        return networkList;
    }

    public static class UpdatedAtComparator implements Comparator<Network> {

        private final boolean mAscending;

        public UpdatedAtComparator(boolean ascending) {
            mAscending = ascending;
        }

        @Override
        public int compare(Network network, Network network2) {
            if (mAscending) {
                return network.getUpdatedAt().compareTo(network2.getUpdatedAt());
            } else {
                return network2.getUpdatedAt().compareTo(network.getUpdatedAt());
            }
        }
    }
}
