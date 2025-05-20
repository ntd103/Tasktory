package com.example.Tasktory.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.Tasktory.R;
import com.example.Tasktory.activity.MainActivity;
import com.example.Tasktory.dao.UserDAO;
import com.example.Tasktory.objects.UserSession;

public class ChangePasswordFragment extends Fragment {

    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    Button btnChangePassword;
    UserDAO userDAO;
    TextView txtShowPassword;
    ImageView eyeIcon;
    boolean isPasswordVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        edtOldPassword = view.findViewById(R.id.edt_old_password);
        edtNewPassword = view.findViewById(R.id.edt_new_password);
        edtConfirmPassword = view.findViewById(R.id.edt_confirm_password);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        txtShowPassword = view.findViewById(R.id.txt_show_password);
        eyeIcon = view.findViewById(R.id.eye_icon2);
        userDAO = new UserDAO(getContext());

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();

            }
        });
        txtShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility();
            }
        });
        return view;
    }

    private void changePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(oldPassword)) {
                edtOldPassword.requestFocus();
            } else if (TextUtils.isEmpty(newPassword)) {
                edtNewPassword.requestFocus();
            } else if (TextUtils.isEmpty(confirmPassword)) {
                edtConfirmPassword.requestFocus();
            }
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            edtNewPassword.requestFocus();
            edtConfirmPassword.requestFocus();
            return;
        }

        int userId = UserSession.getInstance().getUserId();

        if (!userDAO.checkUserById(userId, oldPassword)) {
            Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
            edtOldPassword.requestFocus();
            return;
        }

        // Update password
        boolean result = userDAO.updatePassword(userId, newPassword);
        if (result) {
            Toast.makeText(getContext(), "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Lỗi khi thay đổi mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            txtShowPassword.setText("Hiện mật khẩu");
            edtOldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeIcon.setImageResource(R.drawable.ic_visibility_off);
        } else {
            txtShowPassword.setText("Ẩn mật khẩu");
            edtOldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edtNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeIcon.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        edtOldPassword.setSelection(edtOldPassword.getText().length());
        edtNewPassword.setSelection(edtNewPassword.getText().length());
        edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
    }
}