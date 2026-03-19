package com.crazy.panel;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Simple UI Layout created programmatically for simplicity
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundColor(android.graphics.Color.DKGRAY);

        TextView titleText = new TextView(this);
        titleText.setText("Crazy Mobile Panel (Non-Root)");
        titleText.setTextSize(24);
        titleText.setTextColor(android.graphics.Color.WHITE);
        titleText.setPadding(0, 0, 0, 50);
        layout.addView(titleText);

        TextView descText = new TextView(this);
        descText.setText("Notice: For non-root devices, please run this app inside a Virtual Space (e.g., Parallel Space, Virtual Android) for memory access.");
        descText.setTextColor(android.graphics.Color.LTGRAY);
        descText.setPadding(0, 0, 0, 80);
        layout.addView(descText);

        Button startBtn = new Button(this);
        startBtn.setText("Launch Mod Menu");
        startBtn.setOnClickListener(v -> checkPermissionAndStart());
        layout.addView(startBtn);

        setContentView(layout);
    }

    private void checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                Toast.makeText(this, "Please allow overlay permission first.", Toast.LENGTH_SHORT).show();
            } else {
                startService(new Intent(this, FloatingModMenuService.class));
                Toast.makeText(this, "Mod Menu Started!", Toast.LENGTH_SHORT).show();
            }
        } else {
            startService(new Intent(this, FloatingModMenuService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startService(new Intent(this, FloatingModMenuService.class));
                }
            }
        }
    }
}
