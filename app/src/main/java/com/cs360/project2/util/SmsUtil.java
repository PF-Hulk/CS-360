package com.cs360.project2.util;
/*
 * Sends SMS only when SEND_SMS is granted.
 * Silent no-op if denied to keep app functional without notifications.
 */
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.core.content.ContextCompat;

public class SmsUtil {
    public static void sendIfPermitted(Context ctx, String phone, String message) {
        boolean granted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
        if (!granted) return;
        try {
            SmsManager.getDefault().sendTextMessage(phone, null, message, null, null);
        } catch (Exception ignored) {}
    }
}
