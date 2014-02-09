package org.jointsecurityarea.android.armorycompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Transaction;

public class TransactionBroadcastActivity extends Activity {
    byte[] txInput;
    ArmoryCompanionApplication application;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.application = (ArmoryCompanionApplication)getApplication();
        Intent intent = getIntent();
        txInput = intent.getByteArrayExtra("scanResult");
        setContentView(R.layout.transaction_broadcast);
        TextView txView = (TextView)findViewById(R.id.tx);
        // Process transaction
        Log.i("TransactionParsing", "Processing");
        final Transaction tx;
        try {
            Log.i("TransactionParsing", "Processing " + txInput.length + " bytes");
            tx = new Transaction(Constants.NETWORK_PARAMETERS, txInput);
            Log.i("TransactionParsing", "Parsed transaction: " + tx.toString());
            txView.setText(tx.toString());
            Button broadcastButton = (Button)findViewById(R.id.broadcastButton);
            Button cancelButton = (Button)findViewById(R.id.cancelButton);
            broadcastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    application.getPeerGroup().broadcastTransaction(tx);
                    Toast.makeText(application, "Transaction broadcast", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(TransactionBroadcastActivity.this, PeerActivity.class));
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(TransactionBroadcastActivity.this, PeerActivity.class));
                }
            });
        } catch(ProtocolException x) {
            Log.i("TransactionParsing", "Failed to parse transaction: " + x);
        }
    }
}