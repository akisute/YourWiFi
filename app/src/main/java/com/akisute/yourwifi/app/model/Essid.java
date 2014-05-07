package com.akisute.yourwifi.app.model;

import android.text.TextUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Essid {

    private final Set<Network> mNetworkSet = new HashSet<Network>();

    public static Essid newInstance(Network... networks) {
        return newInstance(Arrays.asList(networks));
    }

    public static Essid newInstance(Collection<Network> networkList) {
        Essid essid = new Essid();
        essid.update(networkList);
        return essid;
    }

    private Essid() {
    }

    @Override
    public int hashCode() {
        return getSsid().hashCode();
    }

    public String getSsid() {
        Network network = mNetworkSet.iterator().next();
        return network.getSsid();
    }

    public int getCount() {
        return mNetworkSet.size();
    }

    public int getCryptoType() {
        return Ordering.from(NetworkComparators.CRYPTOTYPE_WEAKEST_TO_STRONGEST).min(mNetworkSet).getCryptoType();
    }

    public Date getUpdatedAt() {
        return Ordering.from(NetworkComparators.UPDATED_AT_ASC).min(mNetworkSet).getUpdatedAt();
    }

    public void update(Collection<Network> networkList) {
        mNetworkSet.retainAll(networkList);
        mNetworkSet.addAll(networkList);
        validate();
    }

    private void validate() {
        // Make sure networkList contains at least 1 Network instance
        // Make sure all Network instances in the list shares the same SSID (since it is ESSID)
        Iterator<Network> iterator = mNetworkSet.iterator();
        if (iterator.hasNext()) {
            final Network firstNetwork = iterator.next();
            if (!Iterators.all(iterator, new Predicate<Network>() {
                @Override
                public boolean apply(Network input) {
                    return TextUtils.equals(firstNetwork.getSsid(), input.getSsid());
                }
            })) {
                throw new IllegalStateException("All SSIDs in Essid object must be same");
            }
        } else {
            throw new IllegalStateException("Essid object must contain at least 1 Network object");
        }
    }
}
