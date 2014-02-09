package org.jointsecurityarea.android.armorycompanion;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class PeerActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(PeerActivity.class);
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
        Button exitbutton = (Button)findViewById(R.id.exitButton);
        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.exit();
                moveTaskToBack(true);
            }
        });
        Button helpbutton = (Button)findViewById(R.id.helpButton);
        helpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PeerActivity.this)
                        .setMessage(R.string.helpText)
                        .setTitle("Help")
                        .setNeutralButton(android.R.string.ok, null)
                        .create().show();
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
                Button broadcastTransactionButton = (Button)findViewById(R.id.scanButton);
                ListView peerListView = (ListView)findViewById(R.id.peerList);
                List<Peer> connectedPeers = application.getPeerGroup().getConnectedPeers();
                application.getPeerListAdapter().replace(connectedPeers);
                // Enable button if we're sufficiently connected
                if(application.getPeerGroup().numConnectedPeers() >= 3) {
                    broadcastTransactionButton.setEnabled(true);
                } else {
                    broadcastTransactionButton.setEnabled(false);
                }
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
            Log.i("ScanResult", "Got " + input.length() + ": " + input);
            byte[] convertedScan = Base64.decode(input, Base64.DEFAULT);
            Intent txIntent = new Intent(this, TransactionBroadcastActivity.class);
            txIntent.putExtra("scanResult", convertedScan);
            startActivity(txIntent);
        }
    }
    class PeerActivityEventListener implements PeerEventListener {
        @Override
        public void onPeerConnected(Peer peer, int peerCount) {
            redraw();
        }

        @Override
        public void onPeerDisconnected(Peer peer, int peerCount) {
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
    }
}
