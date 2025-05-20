package com.example.Tasktory.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.UserDAO;
import com.example.Tasktory.objects.UserSession;

public class Login extends AppCompatActivity {

    TextView register, quenmk;
    Button btnLogin;
    EditText edtUsername, edtPassword;
    UserDAO userDAO;
    ImageView eyeIcon;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWidget();

        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        quenmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Login.this, Register.class);
                startActivity(intent2);
            }
        });
    }

    public void getWidget() {
        register = findViewById(R.id.register_prompt);
        quenmk = findViewById(R.id.quenmk);
        btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.txt_username);
        edtPassword = findViewById(R.id.txt_password);
        eyeIcon = findViewById(R.id.eye_icon);
        userDAO = new UserDAO(this);
    }

    public void login() {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(Login.this, "Vui lòng nhập tài khoản, mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean checkUser = userDAO.checkUser(username, password);
        if (checkUser) {
            Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            int user_id = userDAO.getUserId(username, password);
            if (user_id == 0) {
                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.remove("is_logged_in");
                editor.remove("user_id");
                editor.apply();
                editor.putBoolean("is_logged_in", true);
                editor.putInt("user_id", user_id);
                editor.apply();

                UserSession userSession = UserSession.getInstance();
                userSession.setUserId(user_id);
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(Login.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeIcon.setImageResource(R.drawable.ic_visibility_off);
        } else {
            edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeIcon.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        edtPassword.setSelection(edtPassword.getText().length());
    }

}