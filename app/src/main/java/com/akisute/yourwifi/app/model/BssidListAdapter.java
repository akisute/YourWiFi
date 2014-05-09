package com.akisute.yourwifi.app.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.akisute.yourwifi.app.R;
import com.akisute.yourwifi.app.util.GlobalResources;
import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BssidListAdapter extends BaseAdapter {

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

    @Inject
    LayoutInflater mLayoutInflater;
    @Inject
    GlobalResources mGlobalResources;

    private final List<Network> mNetworkList = new ArrayList<Network>();

    @Inject
    public BssidListAdapter(LayoutInflater layoutInflater, GlobalResources globalResources) {
        mLayoutInflater = layoutInflater;
        mGlobalResources = globalResources;
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
            convertView = mLayoutInflater.inflate(R.layout.list_bssid_item, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Network network = getItem(position);
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

    public void update(Essid essid) {
        mNetworkList.clear();
        mNetworkList.addAll(Ordering.from(NetworkComparators.LEVEL_DESC).sortedCopy(essid.getNetworkSet()));
        notifyDataSetChanged();
    }
}
