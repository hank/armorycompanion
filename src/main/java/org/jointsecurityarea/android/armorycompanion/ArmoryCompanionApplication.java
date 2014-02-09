package org.jointsecurityarea.android.armorycompanion;

import android.app.Application;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerEventListener;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.net.discovery.DnsDiscovery;

import java.util.List;

public class ArmoryCompanionApplication extends Application {
    PeerGroup peerGroup;
    PeerListAdapter peerListAdapter;

    public PeerListAdapter getPeerListAdapter() {
        return peerListAdapter;
    }
    public PeerGroup getPeerGroup() {
        return peerGroup;
    }
    public void addPeerEventListener(PeerEventListener listener) {
        peerGroup.addEventListener(listener);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        peerGroup = new PeerGroup(Constants.NETWORK_PARAMETERS);
        peerGroup.setMaxConnections(5);
        peerGroup.setUserAgent("ArmoryCompanion Android", Constants.VERSION, "Relaying txs like a boss");
        peerGroup.addPeerDiscovery(new DnsDiscovery(Constants.NETWORK_PARAMETERS));
        peerGroup.start();
        List<Peer> connectedPeers = peerGroup.getConnectedPeers();
        peerListAdapter = new PeerListAdapter(this, connectedPeers);
    }

    public void customAppMethod()
    {
        // Custom application method
    }
}
