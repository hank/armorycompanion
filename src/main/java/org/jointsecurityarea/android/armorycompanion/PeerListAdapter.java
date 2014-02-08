package org.jointsecurityarea.android.armorycompanion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.bitcoin.core.Peer;

import java.util.List;

public class PeerListAdapter extends BaseAdapter {
    private final Context context;
    public List<Peer> values;

    public PeerListAdapter(Context context, List<Peer> values) {
        this.context = context;
        this.values = values;
    }

    public void replace(List<Peer> peers) {
        values.clear();
        values.addAll(peers);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return values.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.peer_row, parent, false);
        }
        Log.i("Adapter", "In getView, position " + position);
        Peer peer = values.get(position);

        TextView textView = (TextView) convertView.findViewById(R.id.peerName);
        textView.setText(peer.getPeerVersionMessage().toString());
        return convertView;
    }
}
