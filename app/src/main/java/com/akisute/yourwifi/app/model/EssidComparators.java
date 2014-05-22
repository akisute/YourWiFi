package com.akisute.yourwifi.app.model;

import com.google.common.collect.Ordering;

import java.util.Comparator;

public class EssidComparators {

    public static final Comparator<Essid> UPDATED_AT_ASC = new UpdatedAtComparator();
    public static final Comparator<Essid> UPDATED_AT_DESC = Ordering.from(new UpdatedAtComparator()).reverse();

    public static final Comparator<Essid> LEVEL_ASC = new LevelComparator();
    public static final Comparator<Essid> LEVEL_DESC = Ordering.from(new LevelComparator()).reverse();

    public static final Comparator<Essid> CRYPTOTYPE_WEAKEST_TO_STRONGEST = new CryptoTypeStrengthComparator();
    public static final Comparator<Essid> CRYPTOTYPE_STRONGEST_TO_WEAKEST = Ordering.from(new CryptoTypeStrengthComparator()).reverse();

    public static final Comparator<Essid> DEFAULT = Ordering.from(LEVEL_DESC).compound(CRYPTOTYPE_WEAKEST_TO_STRONGEST).compound(new SsidComparator());


    public static class UpdatedAtComparator implements Comparator<Essid> {
        @Override
        public int compare(Essid essid, Essid essid2) {
            return essid.getUpdatedAt().compareTo(essid2.getUpdatedAt());
        }
    }

    public static class SsidComparator implements Comparator<Essid> {
        @Override
        public int compare(Essid essid, Essid essid2) {
            String ssid1 = essid.getSsid();
            String ssid2 = essid2.getSsid();
            ssid1 = (ssid1 == null) ? "" : ssid1;
            ssid2 = (ssid2 == null) ? "" : ssid2;
            return ssid1.compareTo(ssid2);
        }
    }


    public static class LevelComparator implements Comparator<Essid> {
        @Override
        public int compare(Essid essid, Essid essid2) {
            int level1 = essid.getLevel();
            int level2 = essid2.getLevel();
            return Integer.compare(level1, level2);
        }
    }

    public static class CryptoTypeStrengthComparator implements Comparator<Essid> {
        @Override
        public int compare(Essid essid, Essid essid2) {
            int type1 = essid.getCryptoType();
            int type2 = essid2.getCryptoType();
            return Integer.compare(type1, type2);
        }
    }
}
