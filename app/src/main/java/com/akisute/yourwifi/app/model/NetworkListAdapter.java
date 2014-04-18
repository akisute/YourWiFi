package com.akisute.yourwifi.app.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkListAdapter extends ArrayAdapter<Network> {

    private class ViewHolder {
        TextView textView;
    }

    private LayoutInflater mInflater;

    public NetworkListAdapter(Context context) {
        super(context, 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null, false);
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
    public void onScanResultUpdateEvent(NetworkManager.OnScanResultsUpdateEvent event) {
        // TODO: Needs to consider about Network list update policy. Time and Location are the vital keys.
        List<Network> networkList = event.getNetworkList();
        Collections.sort(networkList, new Comparator<Network>() {
            @Override
            public int compare(Network network, Network network2) {
                String ssid1 = network.getSsid();
                String ssid2 = network2.getSsid();
                ssid1 = (ssid1 == null) ? "" : ssid1;
                ssid2 = (ssid2 == null) ? "" : ssid2;
                return ssid1.compareTo(ssid2);
            }
        });
        addAll(networkList);
    }
}
