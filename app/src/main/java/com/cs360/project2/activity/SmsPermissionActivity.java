package com.cs360.project2.activity;
/*
 * One-time permission gate for SEND_SMS.
 * - Requests runtime permission.
 * - Continues to Login whether granted or denied (per rubric).
 * - Updates a small status label for UX.
 */
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.cs360.project2.R;

public class SmsPermissionActivity extends AppCompatActivity {

    private static final int REQ_SEND_SMS = 101;

    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms_permission);

        MaterialButton requestPermissionButton = findViewById(R.id.requestPermissionButton);
        statusText = findViewById(R.id.statusText);

        // If already granted, go straight to login
        if (isSmsGranted()) {
            statusText.setText(R.string.sms_permission_granted); // ← resource, not literal
            continueToLogin();
            return;
        }

        // Not granted yet — show rationale/status and let user request
        statusText.setText(R.string.sms_disabled_message);
        requestPermissionButton.setOnClickListener(v ->
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.SEND_SMS},
                        REQ_SEND_SMS
                )
        );
    }

    /** Helper: true if SEND_SMS is currently granted. */
    private boolean isSmsGranted() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /** Continue regardless of decision; update status label for completeness. */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_SEND_SMS) {
            if (isSmsGranted()) {
                statusText.setText(R.string.sms_permission_granted); // ← resource, not literal
            } else {
                statusText.setText(R.string.sms_disabled_message);
            }
            continueToLogin();
        }
    }

    /** Navigates to the login screen and finishes this Activity. */
    private void continueToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
