package org.jointsecurityarea.android.armorycompanion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TransactionBroadcastActivity extends Activity {
    String txInput;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        txInput = intent.getStringExtra("scanResult");
        setContentView(R.layout.transaction_broadcast);
        TextView txView = (TextView)findViewById(R.id.tx);
        txView.setText(txInput);
    }
}