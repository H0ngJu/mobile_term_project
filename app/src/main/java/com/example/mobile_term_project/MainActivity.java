package com.example.mobile_term_project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper;
    SharedPreferences sharedPreferences;
    Button btnSignUp, btnLogin, loginComplete, btnLogout;
    View dialogView;
    EditText loginNickname, loginPassword;
    TextView loginError, nickname;
    LinearLayout beforeLogin, afterLogin;

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
        btnLogout = findViewById(R.id.BtnLogout);

        nickname = findViewById(R.id.Nickname);

        afterLogin = findViewById(R.id.AfterLogin);
        beforeLogin = findViewById(R.id.BeforeLogin);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });


        //로그인
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogView = View.inflate(MainActivity.this, R.layout.login_dialog, null);

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setView(dialogView);
                AlertDialog dialog = dlg.create();

                loginComplete = dialogView.findViewById(R.id.LoginComplete);
                loginComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loginNickname = dialogView.findViewById(R.id.LoginNickname);
                        loginPassword = dialogView.findViewById(R.id.LoginPassword);
                        loginError = dialogView.findViewById(R.id.Error);

                        String nickname = loginNickname.getText().toString();
                        String password = loginPassword.getText().toString();

                        Cursor cursor = sqLiteHelper.login(nickname, password);

                        if(cursor.moveToFirst()) { //로그인 성공시 -> SharedPreferences에 로그인 정보 저장
                            int idIndex = cursor.getColumnIndex(TableInfo.MemberEntry._ID);
                            //int nicknameIndex = cursor.getColumnIndex(TableInfo.MemberEntry.COLUMN_NAME_NICKNAME);
                            //int passwordIndex = cursor.getColumnIndex(TableInfo.MemberEntry.COLUMN_NAME_PASSWORD);

                            int id = cursor.getInt(idIndex);
                            //String dbNickname = cursor.getString(nicknameIndex); //로그인할 때 받는데 굳이 디비에서 가져온거?
                            //String dbPassword = cursor.getString(passwordIndex);

                            SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor loginEdit = login.edit();
                            loginEdit.putInt("id", id);
                            loginEdit.putString("nickname", nickname);
                            loginEdit.putString("password", password);
                            loginEdit.commit();

                            dialog.dismiss();
                            afterLogin();
                            Toast.makeText(MainActivity.this, "로그인 성공.", Toast.LENGTH_SHORT).show();

                        }else { //로그인 실패 시 -> Error 메세지 visible
                            loginError.setVisibility(View.VISIBLE);
                        }
                        cursor.close();
                    }
                });
                dlg.setNegativeButton("취소", null);
                dialog.show();
            }
        });

        //로그아웃
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
                SharedPreferences.Editor loginEdit = login.edit();
                loginEdit.clear();
                loginEdit.commit();

                afterLogin.setVisibility(View.GONE);
                beforeLogin.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "로그아웃 완료되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void afterLogin () {
        beforeLogin.setVisibility(View.GONE);
        afterLogin.setVisibility(View.VISIBLE);

        SharedPreferences login = getSharedPreferences("login", Activity.MODE_PRIVATE);
        String name = login.getString("nickname", null);
        nickname.setText(name + "님");
    }

}