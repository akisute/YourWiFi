package com.akisute.yourwifi.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.akisute.android.daggered.DaggeredFragment;
import com.akisute.yourwifi.app.model.Essid;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EssidDetailFragment extends DaggeredFragment {

    class ViewHolder {
        @InjectView(R.id.bssid)
        TextView bssid;
        @InjectView(R.id.crypto)
        TextView crypto;
        @InjectView(R.id.level)
        TextView level;
        @InjectView(R.id.frequency)
        TextView frequency;
        @InjectView(R.id.capabilities)
        TextView capabilities;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @InjectView(R.id.ssid)
    TextView mSsidView;
    @InjectView(android.R.id.list)
    ListView mListView;

    private Essid mEssid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_essid_detail, container, false);
        ButterKnife.inject(this, view);
        mSsidView.setText(mEssid.getSsid());
        return view;
    }

    public void setEssid(Essid essid) {
        mEssid = essid;
    }
}
