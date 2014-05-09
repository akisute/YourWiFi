package com.akisute.yourwifi.app.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RawNetworkListAdapter extends BaseAdapter {

    class ViewHolder {
        @InjectView(android.R.id.text1)
        TextView textView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Inject
    LayoutInflater mLayoutInflater;
    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    NetworkCache mNetworkCache;

    private final List<Network> mNetworkList = new ArrayList<Network>();

    @Inject
    public RawNetworkListAdapter(LayoutInflater layoutInflater, GlobalEventBus globalEventBus, NetworkCache networkCache) {
        mLayoutInflater = layoutInflater;
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
        mNetworkList.clear();
        mNetworkList.addAll(mNetworkCache.getAllNetworkList());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mNetworkList.size();
    }

    @Override
    public Network getItem(int position) {
        return mNetworkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Network network = getItem(position);
        viewHolder.textView.setText(network.getDescription());

        return convertView;
    }

    @Subscribe
    public void onNewScanResultsEvent(NetworkScanManager.OnNewScanResultsEvent event) {
        update();
    }
}
