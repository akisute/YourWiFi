package com.akisute.yourwifi.app;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.akisute.android.daggered.DaggeredFragment;
import com.akisute.yourwifi.app.model.Essid;
import com.akisute.yourwifi.app.model.EssidListAdapter;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.akisute.yourwifi.app.util.GlobalResources;
import com.akisute.yourwifi.app.util.GlobalSharedPreferences;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EssidListFragment extends DaggeredFragment {

    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            String appName = mGlobalResources.getResources().getString(R.string.app_name);
            getActivity().setTitle(mGlobalResources.getResources().getQuantityString(R.plurals.activity_main_title, mAdapter.getCount(), appName, mAdapter.getCount()));
        }
    };

    @Inject
    GlobalResources mGlobalResources;
    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    GlobalSharedPreferences mGlobalSharedPreferences;
    @Inject
    EssidListAdapter mAdapter;
    @InjectView(android.R.id.list)
    ListView mListView;

    public EssidListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter.registerToEventBus();
        mAdapter.registerDataSetObserver(mDataSetObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter.unregisterDataSetObserver(mDataSetObserver);
        mAdapter.unregisterFromEventBus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_essid_list, container, false);
        ButterKnife.inject(this, view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Essid essid = mAdapter.getItem(position);
                mGlobalEventBus.postInMainThread(new OnEssidListItemSelectedEvent(essid));
            }
        });
        mAdapter.update();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_essid_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_raw_networks:
                mGlobalSharedPreferences.setNetworkListDisplayMode(GlobalSharedPreferences.NetworkListDisplayMode.SHOW_RAW_NETWORKS);
                return true;
        }
        return false;
    }

    public static class OnEssidListItemSelectedEvent {

        private final Essid mEssid;

        public OnEssidListItemSelectedEvent(Essid essid) {
            mEssid = essid;
        }

        public Essid getEssid() {
            return mEssid;
        }
    }
}
