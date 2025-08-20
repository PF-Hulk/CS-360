package com.cs360.project2.activity;
/*
 * Basic login screen that:
 * - Enables Log In only when both fields are non-empty.
 * - Validates credentials or creates a new user on demand.
 * - Navigates to DataGridActivity on success.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cs360.project2.R;
import com.cs360.project2.data.AppRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton    loginButton;

    private AppRepository repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput       = findViewById(R.id.usernameInput);
        passwordInput       = findViewById(R.id.passwordInput);
        loginButton         = findViewById(R.id.loginButton);
        MaterialButton createAccountButton = findViewById(R.id.createAccountButton);

        repo = new AppRepository(this);
        loginButton.setEnabled(false);

        // Lightweight watcher to toggle button enabled state.
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c) {}
            @Override public void onTextChanged(CharSequence s,int a,int b,int c) {
                String u = usernameInput.getText()!=null?usernameInput.getText().toString():"";
                String p = passwordInput.getText()!=null?passwordInput.getText().toString():"";
                loginButton.setEnabled(!u.trim().isEmpty() && !p.trim().isEmpty());
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        usernameInput.addTextChangedListener(watcher);
        passwordInput.addTextChangedListener(watcher);

        // Validate existing user.
        loginButton.setOnClickListener(v -> {
            String u = usernameInput.getText()!=null?usernameInput.getText().toString().trim():"";
            String p = passwordInput.getText()!=null?passwordInput.getText().toString():"";
            if (repo.validateLogin(u, p)) {
                startActivity(new Intent(this, DataGridActivity.class));
            } else {
                Toast.makeText(this, R.string.err_invalid_credentials, Toast.LENGTH_SHORT).show();
            }
        });

        // Create new user; on success, proceed to the grid.
        createAccountButton.setOnClickListener(v -> {
            String u = usernameInput.getText()!=null?usernameInput.getText().toString().trim():"";
            String p = passwordInput.getText()!=null?passwordInput.getText().toString():"";
            if (repo.createUser(u, p)) {
                startActivity(new Intent(this, DataGridActivity.class));
            } else {
                Toast.makeText(this, R.string.err_username_exists, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
