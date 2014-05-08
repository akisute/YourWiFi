package com.akisute.yourwifi.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akisute.android.daggered.DaggeredFragment;

public class EssidDetailFragment extends DaggeredFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_essid_detail, container, false);
        return view;
    }

}
