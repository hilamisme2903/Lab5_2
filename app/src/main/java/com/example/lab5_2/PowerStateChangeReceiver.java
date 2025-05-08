package com.example.lab5_2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class PowerStateChangeReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "power_state_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        createNotificationChannel(context); // Tạo channel nếu cần

        String title = "Power State Changed";
        String message = "";

        if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            message = context.getString(R.string.power_connected);
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            message = context.getString(R.string.power_disconnected);
        } else {
            return;
        }

        // Kiểm tra quyền trước khi gửi thông báo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1001, builder.build());

        } else {
            // Nếu chưa được cấp quyền, có thể log hoặc hiển thị Toast tùy ý
            // Toast.makeText(context, "Permission not granted for notifications", Toast.LENGTH_SHORT).show();
        }
    }

    // Tạo notification channel cho Android 8.0+
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Power State Channel";
            String description = "Notifies when power is connected or disconnected";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
