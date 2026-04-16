package com.anuragalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button btnToggle;
    private Button btnStop;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToggle = findViewById(R.id.btnToggle);
        btnStop = findViewById(R.id.btnStop);
        tvStatus = findViewById(R.id.tvStatus);

        updateUI();

        btnToggle.setOnClickListener(v -> {
            if (!isNotificationListenerEnabled()) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            } else {
                AlertNotificationListener.isServiceEnabled = !AlertNotificationListener.isServiceEnabled;
                updateUI();
            }
        });

        btnStop.setOnClickListener(v -> {
            sendBroadcast(new Intent("com.anuragalert.STOP_ALARM"));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (!isNotificationListenerEnabled()) {
            tvStatus.setText("Permission not granted");
            tvStatus.setTextColor(0xFFFF4444);
            btnToggle.setText("Grant Permission");
            btnToggle.setBackgroundColor(0xFFFF9800);
        } else if (AlertNotificationListener.isServiceEnabled) {
            tvStatus.setText("Listening ON");
            tvStatus.setTextColor(0xFF1DB954);
            btnToggle.setText("Turn OFF");
            btnToggle.setBackgroundColor(0xFFFF4444);
        } else {
            tvStatus.setText("Listening OFF");
            tvStatus.setTextColor(0xFFAAAAAA);
            btnToggle.setText("Turn ON");
            btnToggle.setBackgroundColor(0xFF1DB954);
        }
    }

    private boolean isNotificationListenerEnabled() {
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners");
        return enabledListeners != null && enabledListeners.contains(getPackageName());
    }
}
