package com.example.mobile_term_project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_term_project.db.SQLiteHelper;
import com.example.mobile_term_project.db.TableInfo;

public class MainActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper;
    SharedPreferences sharedPreferences;
    Button btnSignUp, btnLogin;

    LinearLayout beforeLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //db
        sqLiteHelper = new SQLiteHelper(this);
        //로그인 유지
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        btnSignUp = findViewById(R.id.BtnSignUp);
        btnLogin = findViewById(R.id.BtnLogin);

        beforeLogin = findViewById(R.id.BeforeLogin);


        //회원가입
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


        //로그인
        btnLogin.setOnClickListener(v -> {
            View dialogView = View.inflate(MainActivity.this, R.layout.login_dialog, null);
            AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
            dlg.setView(dialogView);
            AlertDialog dialog = dlg.create();

            Button loginComplete = dialogView.findViewById(R.id.LoginComplete);
            EditText loginNickname = dialogView.findViewById(R.id.LoginNickname);
            EditText loginPassword = dialogView.findViewById(R.id.LoginPassword);
            TextView loginError = dialogView.findViewById(R.id.Error);

            loginComplete.setOnClickListener(view -> {
                String nickname = loginNickname.getText().toString();
                String password = loginPassword.getText().toString();

                Cursor cursor = sqLiteHelper.login(nickname, password);

                if (cursor.moveToFirst()) { // 로그인 성공
                    int idIndex = cursor.getColumnIndex(TableInfo.MemberEntry._ID);
                    int id = cursor.getInt(idIndex);

                    SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor loginEdit = login.edit();
                    loginEdit.putInt("id", id);
                    loginEdit.putString("nickname", nickname);
                    loginEdit.apply();

                    cursor.close();
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                    // MainScreen으로 이동
                    Intent intent = new Intent(MainActivity.this, MainScreen.class);
                    startActivity(intent);
                    finish();
                } else { // 로그인 실패
                    loginError.setVisibility(View.VISIBLE);
                }
            });

            dlg.setNegativeButton("취소", null);
            dialog.show();
        });
    }
}