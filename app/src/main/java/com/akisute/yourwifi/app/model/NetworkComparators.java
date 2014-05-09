package com.akisute.yourwifi.app.model;

import com.google.common.collect.Ordering;

import java.util.Comparator;

public class NetworkComparators {

    public static final Comparator<Network> DEFAULT = Ordering.from(new SsidComparator()).compound(new FrequencyComparator());

    public static final Comparator<Network> UPDATED_AT_ASC = new UpdatedAtComparator();
    public static final Comparator<Network> UPDATED_AT_DESC = Ordering.from(new UpdatedAtComparator()).reverse();

    public static final Comparator<Network> LEVEL_ASC = new LevelComparator();
    public static final Comparator<Network> LEVEL_DESC = Ordering.from(new LevelComparator()).reverse();

    public static final Comparator<Network> CRYPTOTYPE_WEAKEST_TO_STRONGEST = new CryptoTypeStrengthComparator();
    public static final Comparator<Network> CRYPTOTYPE_STRONGEST_TO_WEAKEST = Ordering.from(new CryptoTypeStrengthComparator()).reverse();


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

    public static class FrequencyComparator implements Comparator<Network> {
        @Override
        public int compare(Network network, Network network2) {
            int frequency1 = network.getFrequency();
            int frequency2 = network2.getFrequency();
            return Integer.compare(frequency1, frequency2);
        }
    }

    public static class LevelComparator implements Comparator<Network> {
        @Override
        public int compare(Network network, Network network2) {
            int level1 = network.getLevel();
            int level2 = network2.getLevel();
            return Integer.compare(level1, level2);
        }
    }

    public static class CryptoTypeStrengthComparator implements Comparator<Network> {
        @Override
        public int compare(Network network, Network network2) {
            int type1 = network.getCryptoType();
            int type2 = network2.getCryptoType();
            return Integer.compare(type1, type2);
        }
    }
}

