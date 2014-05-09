package com.akisute.yourwifi.app.model;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.akisute.yourwifi.app.R;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.akisute.yourwifi.app.util.GlobalResources;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EssidListAdapter extends BaseAdapter {

    class ViewHolder {

        @InjectView(R.id.ssid)
        TextView ssid;
        @InjectView(R.id.crypto)
        TextView crypto;
        @InjectView(R.id.description)
        TextView description;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Inject
    LayoutInflater mLayoutInflater;
    @Inject
    GlobalResources mGlobalResources;
    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    NetworkCache mNetworkCache;

    private final List<Essid> mEssidList = new ArrayList<Essid>();

    @Inject
    public EssidListAdapter(LayoutInflater layoutInflater, GlobalResources globalResources, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        mLayoutInflater = layoutInflater;
        mGlobalResources = globalResources;
        mGlobalEventBus = globalEventBus;
        mNetworkCache = networkCache;
    }

    public void registerToEventBus() {
        mGlobalEventBus.register(this);
    }

    public void unregisterFromEventBus() {
        mGlobalEventBus.unregister(this);
    }

    public void update() {
        mEssidList.clear();
        mEssidList.addAll(mNetworkCache.getAllEssidList());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mEssidList.size();
    }

    @Override
    public Essid getItem(int position) {
        return mEssidList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_essid_item, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Essid essid = getItem(position);
        viewHolder.ssid.setText(essid.getSsid());
        viewHolder.crypto.setText(mGlobalResources.getCryptoTypeName(essid.getCryptoType()));
        viewHolder.crypto.setTextColor(mGlobalResources.getCryptoTypeFontColor(essid.getCryptoType()));
        viewHolder.description.setText(mGlobalResources.getResources().getQuantityString(R.plurals.list_essid_item_description, essid.getCount(), essid.getCount()));

        return convertView;
    }

    @Subscribe
    public void onNewScanResultsEvent(NetworkScanManager.OnNewScanResultsEvent event) {
        update();
    }
}
