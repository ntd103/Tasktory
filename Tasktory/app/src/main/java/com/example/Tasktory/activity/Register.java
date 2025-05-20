package com.example.Tasktory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.UserDAO;
import com.example.Tasktory.model.User;

public class Register extends AppCompatActivity {
    Button btnRegister;
    TextView txtLogin;
    EditText edtUsername, edtPassword, edtRePassword, edtEmail;
    UserDAO userDAO;

    public void getWidget() {
        btnRegister = findViewById(R.id.btn_register_register);
        txtLogin = findViewById(R.id.login_prompt);
        edtUsername = findViewById(R.id.edit_username_register);
        edtPassword = findViewById(R.id.edit_password_register);
        edtRePassword = findViewById(R.id.edit_repassword_register);
        edtEmail = findViewById(R.id.edit_email_register);
        userDAO = new UserDAO(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWidget();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();

            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Register.this, Login.class);
                startActivity(intent1);
            }
        });
    }

    private boolean isValidUserName(String userName) {
        return userName.length() >= 6 && userName.matches("[a-zA-Z0-9]+");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches("[a-zA-Z0-9@]+");
    }

    private boolean isConfirm(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private boolean isValidEmail(String email) {
        String regexEmail = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email.matches(regexEmail);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void register() {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();
        String rePassword = edtRePassword.getText().toString();
        String email = edtEmail.getText().toString();

        if (username.isEmpty()) {
            showToast("Vui lòng nhập tên đăng nhập!");
            edtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showToast("Vui lòng nhập mật khẩu!");
            edtPassword.requestFocus();
            return;
        }
        if (rePassword.isEmpty()) {
            showToast("Vui lòng xác nhận mật khẩu!");
            edtRePassword.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            showToast("Vui lòng nhập email!");
            edtEmail.requestFocus();
            return;
        }

        if (!isValidUserName(username)) {
            showToast("Tên đăng nhập không hợp lệ!");
            edtUsername.requestFocus();
            return;
        }
        if (!isValidPassword(password)) {
            showToast("Mật khẩu không hợp lệ!");
            edtPassword.requestFocus();
            return;
        }
        if (!isConfirm(password, rePassword)) {
            showToast("Mật khẩu xác nhận không khớp!");
            edtRePassword.requestFocus();
            return;
        }
        if (!isValidEmail(email)) {
            showToast("Email không hợp lệ!");
            edtEmail.requestFocus();
            return;
        }

        if (userDAO.checkExistUser(username)) {
            showToast("Tên đăng nhập đã tồn tại!");
            edtUsername.requestFocus();
            return;
        }
        if (userDAO.checkExistEmail(email)) {
            showToast("Email đã tồn tại");
            edtUsername.requestFocus();
            return;
        }
        User newUser = new User(username, email, password);
        userDAO.insert(newUser);
        showToast("Đăng ký thành công!");
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }

}