package com.akisute.yourwifi.app.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.akisute.yourwifi.app.R;
import com.akisute.yourwifi.app.util.GlobalEventBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

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

    private final LayoutInflater mLayoutInflater;
    private final List<Essid> mEssidList = new ArrayList<Essid>();

    public EssidListAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void registerToEventBus() {
        GlobalEventBus.getInstance().register(this);
    }

    public void unregisterFromEventBus() {
        GlobalEventBus.getInstance().unregister(this);
    }

    public void update(List<Network> networkList) {
        NetworkCache networkCache = NetworkCache.getInstance();
        mEssidList.clear();
        mEssidList.addAll(networkCache.getAllEssidList());
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
        viewHolder.crypto.setText(String.valueOf(essid.getCryptoType()));
        viewHolder.description.setText(convertView.getResources().getQuantityString(R.plurals.list_network_item_description, essid.getCount(), essid.getCount()));

        return convertView;
    }

    @Subscribe
    public void onNewScanResultsEvent(NetworkScanManager.OnNewScanResultsEvent event) {
        update(event.getNetworkList());
    }
}
