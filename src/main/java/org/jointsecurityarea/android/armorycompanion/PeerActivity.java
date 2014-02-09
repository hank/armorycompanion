package org.jointsecurityarea.android.armorycompanion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.bitcoin.core.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class PeerActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(PeerActivity.class);
    private int peersConnected = 0;
    ArmoryCompanionApplication application;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.application = (ArmoryCompanionApplication)getApplication();
        ListView peerListView = (ListView)findViewById(R.id.peerList);
        peerListView.setAdapter(application.getPeerListAdapter());

        Button button = (Button)findViewById(R.id.scanButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleScan();
            }
        });
        application.getPeerGroup().addEventListener(new PeerActivityEventListener());
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
                List<Peer> connectedPeers = application.getPeerGroup().getConnectedPeers();
                for(Peer p : connectedPeers) {
                    Log.i("Peering", "Peer: " + p.toString());
                }
                application.getPeerListAdapter().replace(connectedPeers);
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
        Log.i("ScanResult", "Got " + input.length() + ": " + input);
        if(input != null) {
            byte[] convertedScan = Base64.decode(input, Base64.DEFAULT);
            Intent txIntent = new Intent(this, TransactionBroadcastActivity.class);
            txIntent.putExtra("scanResult", convertedScan);
            startActivity(txIntent);
        }
    }
    class PeerActivityEventListener implements PeerEventListener {
        @Override
        public void onPeerConnected(Peer peer, int peerCount) {
            synchronized (this) {
                peersConnected += 1;
                redraw();
            }
        }

        @Override
        public void onPeerDisconnected(Peer peer, int peerCount) {
            synchronized (this) {
                peersConnected -= 1;
                redraw();
            }
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
    }
}
