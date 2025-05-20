package com.example.Tasktory.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Tasktory.R;
import com.example.Tasktory.objects.UserSession;

public class Welcome extends AppCompatActivity {
    Button btnLogin;
    Button btnRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            int userId = sharedPreferences.getInt("user_id", 0);
            UserSession userSession = UserSession.getInstance();
            userSession.setUserId(userId);
            Intent intent = new Intent(Welcome.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btnLogin = findViewById(R.id.login_button);
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Welcome.this, Login.class);
                        startActivity(intent);
                    }
                }
        );
        btnRegister = findViewById(R.id.create_account_button);
        btnRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Welcome.this, Register.class);
                        startActivity(intent);
                    }
                }
        );

    }
}