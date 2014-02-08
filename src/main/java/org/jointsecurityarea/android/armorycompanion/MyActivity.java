package org.jointsecurityarea.android.armorycompanion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.bitcoin.core.Block;
import com.google.bitcoin.core.GetDataMessage;
import com.google.bitcoin.core.Message;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerEventListener;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.net.discovery.DnsDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class MyActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(MyActivity.class);
    PeerGroup peerGroup;
    int peersConnected = 0;
    PeerListAdapter peerListAdapter;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        peerGroup = new PeerGroup(Constants.NETWORK_PARAMETERS);
        peerGroup.setMaxConnections(5);
        peerGroup.setUserAgent("ArmoryCompanion Android", Constants.VERSION, "Relaying txs like a boss");
        peerGroup.addEventListener(new PeerEventListener() {
            @Override
            public void onPeerConnected(Peer peer, int peerCount) {
                peersConnected += 1;
                redraw();
            }

            @Override
            public void onPeerDisconnected(Peer peer, int peerCount) {
                peersConnected -= 1;
                redraw();
            }

            @Override
            public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
            }

            @Override
            public void onChainDownloadStarted(Peer peer, int blocksLeft) {
            }

            @Override
            public Message onPreMessageReceived(Peer peer, Message m) {
                return null;
            }

            @Override
            public void onTransaction(Peer peer, Transaction t) {
            }

            @Nullable
            @Override
            public List<Message> getData(Peer peer, GetDataMessage m) {
                return null;
            }
        });
        peerGroup.addPeerDiscovery(new DnsDiscovery(Constants.NETWORK_PARAMETERS));
        peerGroup.start();
        setContentView(R.layout.main);
        ListView peerListView = (ListView)findViewById(R.id.peerList);
        List<Peer> connectedPeers = peerGroup.getConnectedPeers();
        peerListAdapter = new PeerListAdapter(this, connectedPeers);
        peerListView.setAdapter(peerListAdapter);

        Button button = (Button)findViewById(R.id.scanButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleScan();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        redraw();
    }

    private void redraw() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView peersConnectedView = (TextView)findViewById(R.id.numPeers);
                peersConnectedView.setText(Integer.toString(peersConnected));
                ListView peerListView = (ListView)findViewById(R.id.peerList);
                List<Peer> connectedPeers = peerGroup.getConnectedPeers();
                for(Peer p : connectedPeers) {
                    Log.i("Peering", "Peer: " + p.toString());
                }
                peerListAdapter.replace(connectedPeers);
            }
        });
    }

    public void handleScan()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        String input = null;
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        input = scanResult.getContents();
        if(input != null) {
            Intent txIntent = new Intent(this, TransactionBroadcastActivity.class);
            txIntent.putExtra("scanResult", input);
            startActivity(txIntent);
        }
    }

}
