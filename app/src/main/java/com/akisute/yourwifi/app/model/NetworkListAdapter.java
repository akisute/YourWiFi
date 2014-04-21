package com.akisute.yourwifi.app.model;

import android.content.Context;
import android.provider.Settings;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkListAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView textView;
    }

    private final LayoutInflater mLayoutInflater;
    private final NetworkCache mNetworkCache = new NetworkCache();
    private List<Network> mCurrentList = new ArrayList<Network>();

    public NetworkListAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void registerToEventBus() {
        GlobalEventBus.getInstance().register(this);
    }

    public void unregisterFromEventBus() {
        GlobalEventBus.getInstance().unregister(this);
    }

    public void update(List<Network> networkList) {
        for (Network network : networkList) {
            mNetworkCache.put(network);
        }
        mNetworkCache.purgeOutdatedNetworks();
        mCurrentList = mNetworkCache.getAllNetworkList();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCurrentList.size();
    }

    @Override
    public Network getItem(int position) {
        return mCurrentList.get(position);
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
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder =  (ViewHolder) convertView.getTag();
        }

        Network network = getItem(position);
        viewHolder.textView.setText(network.getDescription());

        return convertView;
    }

    @Subscribe
    public void onNewScanResultsEvent(NetworkScanManager.OnNewScanResultsEvent event) {
        update(event.getNetworkList());
    }
}
