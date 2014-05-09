package com.akisute.yourwifi.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.akisute.android.daggered.DaggeredFragment;
import com.akisute.yourwifi.app.model.BssidListAdapter;
import com.akisute.yourwifi.app.model.Essid;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EssidDetailFragment extends DaggeredFragment {

    @InjectView(R.id.ssid)
    TextView mSsidView;
    @InjectView(android.R.id.list)
    ListView mListView;

    @Inject
    BssidListAdapter mAdapter;

    private Essid mEssid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_essid_detail, container, false);
        ButterKnife.inject(this, view);
        mAdapter.update(mEssid);
        mListView.setAdapter(mAdapter);
        mSsidView.setText(mEssid.getSsid());
        return view;
    }

    public void setEssid(Essid essid) {
        mEssid = essid;
        if (mAdapter != null) {
            mAdapter.update(mEssid);
        }
    }
}
