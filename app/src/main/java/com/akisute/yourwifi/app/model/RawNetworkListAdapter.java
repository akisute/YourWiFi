package com.akisute.yourwifi.app.model;

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

public class RawNetworkListAdapter extends BaseAdapter {

    class ViewHolder {

        @InjectView(R.id.ssid)
        TextView ssid;
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

    @Inject
    LayoutInflater mLayoutInflater;
    @Inject
    GlobalResources mGlobalResources;
    @Inject
    GlobalEventBus mGlobalEventBus;
    @Inject
    NetworkCache mNetworkCache;

    private final List<Network> mNetworkList = new ArrayList<Network>();

    @Inject
    public RawNetworkListAdapter(LayoutInflater layoutInflater, GlobalResources globalResources, GlobalEventBus globalEventBus, NetworkCache networkCache) {
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
            convertView = mLayoutInflater.inflate(R.layout.list_raw_network_item, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Network network = getItem(position);
        viewHolder.ssid.setText(network.getSsid());
        viewHolder.bssid.setText(network.getBssid());
        viewHolder.crypto.setText(mGlobalResources.getCryptoTypeName(network.getCryptoType()));
        viewHolder.crypto.setTextColor(mGlobalResources.getCryptoTypeFontColor(network.getCryptoType()));
        viewHolder.level.setText(mGlobalResources.getResources().getString(R.string.list_network_item_level, network.getLevel()));
        viewHolder.capabilities.setText(network.getCapabilities());

        int channel = network.getChannel();
        if (channel == Network.CHANNEL_UNKNOWN) {
            viewHolder.frequency.setText(mGlobalResources.getResources().getString(R.string.list_network_item_frequency, network.getFrequency()));
        } else {
            viewHolder.frequency.setText(mGlobalResources.getResources().getString(R.string.list_network_item_frequency_channel, network.getFrequency(), channel));
        }

        return convertView;
    }

    @Subscribe
    public void onNewScanResultsEvent(NetworkScanManager.OnNewScanResultsEvent event) {
        update();
    }
}
