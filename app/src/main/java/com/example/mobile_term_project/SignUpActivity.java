package com.example.mobile_term_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_term_project.db.SQLiteHelper;

public class SignUpActivity extends AppCompatActivity {

    EditText nickname, password, checkPassword;
    TextView errorPwd;
    Button btnCheckNickname, btnSignUp;
    SQLiteHelper sqLiteHelper;
    boolean checkDup, checkPwd = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nickname = findViewById(R.id.Nickname);
        password = findViewById(R.id.Password);
        checkPassword = findViewById(R.id.CheckPassword);
        errorPwd = findViewById(R.id.ErrorPwd);
        btnCheckNickname = findViewById(R.id.BtnCheckNickname);
        btnSignUp = findViewById(R.id.BtnSignUp);
        //db
        sqLiteHelper = new SQLiteHelper(this);

        //닉네임 중복 검사
        btnCheckNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = nickname.getText().toString();
                if(input.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                    checkDup = false;
                }
                else {
                    if(!sqLiteHelper.checkNickname(input)) {
                        Toast.makeText(SignUpActivity.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                        checkDup = true;
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                        checkDup = false;
                    }
                }
            }
        });

        //비밀번호 확인 검사
        checkPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputPassword = password.getText().toString();
                String inputCheckPw = checkPassword.getText().toString();

                if(!inputPassword.equals(inputCheckPw)) {
                    errorPwd.setVisibility(View.VISIBLE);
                    errorPwd.setText("비밀번호가 다릅니다. 확인해주세요.");
                    errorPwd.setTextColor(Color.RED);
                    checkPwd = false;
                }
                else {
                    errorPwd.setVisibility(View.VISIBLE);
                    errorPwd.setText("비밀번호가 일치합니다.");
                    errorPwd.setTextColor(Color.GREEN);
                    checkPwd = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        //회원가입 (db에 넣기)
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputNickname = nickname.getText().toString();
                String inputPassword = password.getText().toString();
                if(checkDup == false) {
                    Toast.makeText(SignUpActivity.this, "닉네임 '중복확인'버튼을 클릭해주세요. ", Toast.LENGTH_SHORT).show();
                } else if(checkPwd == false) {
                    Toast.makeText(SignUpActivity.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if(sqLiteHelper.addMember(inputNickname, inputPassword) == -1) {
                        Toast.makeText(SignUpActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}