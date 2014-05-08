package com.akisute.yourwifi.app;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.akisute.android.daggered.DaggeredFragment;
import com.akisute.yourwifi.app.model.EssidListAdapter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EssidListFragment extends DaggeredFragment {

    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            String appName = getResources().getString(R.string.app_name);
            getActivity().setTitle(getResources().getQuantityString(R.plurals.activity_main_title, mAdapter.getCount(), appName, mAdapter.getCount()));
        }
    };

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_essid_list, container, false);
        ButterKnife.inject(this, view);
        mListView.setAdapter(mAdapter);
        return view;
    }
}
