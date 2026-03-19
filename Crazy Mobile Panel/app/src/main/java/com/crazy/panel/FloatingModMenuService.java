package com.crazy.panel;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingModMenuService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private NativeBridge nativeBridge;
    private boolean isAimbotActive = false;

    @Override
    public void onCreate() {
        super.onCreate();
        nativeBridge = new NativeBridge();
        initFloatingWindow();
    }

    private void initFloatingWindow() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // UI Container
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#80000000")); // semi-transparent black
        layout.setPadding(20, 20, 20, 20);

        // Status Text
        TextView statusText = new TextView(this);
        statusText.setText("Status: Not Connected");
        statusText.setTextColor(Color.WHITE);
        layout.addView(statusText);

        // Connect Button
        Button btnConnect = new Button(this);
        btnConnect.setText("Connect to Game");
        btnConnect.setOnClickListener(v -> {
            if (nativeBridge.attach("com.dts.freefireth")) {
                statusText.setText("Status: Connected (PID: " + nativeBridge.getPid() + ")");
                btnConnect.setVisibility(View.GONE);
                Toast.makeText(this, "Game Detected!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Game Not Found. Start Game First.", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(btnConnect);

        // Aimbot Toggle Button
        Button btnAimbot = new Button(this);
        btnAimbot.setText("Aimbot: OFF");
        btnAimbot.setOnClickListener(v -> {
            isAimbotActive = !isAimbotActive;
            nativeBridge.setAimbotActive(isAimbotActive);
            btnAimbot.setText("Aimbot: " + (isAimbotActive ? "ON" : "OFF"));
            btnAimbot.setBackgroundColor(isAimbotActive ? Color.GREEN : Color.RED);
        });
        layout.addView(btnAimbot);

        // Window Parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        floatingView = layout;
        windowManager.addView(floatingView, params);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}
