package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    EditText userNameText, passwordText;
    String userName, password;
    Button btnLogin, btnRegister;
    miBD gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*PROXIMAMENTE.......*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);


        btnLogin = (Button) findViewById(R.id.btnLogin2);
        btnRegister = (Button) findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = userNameText.getText().toString();
                password = passwordText.getText().toString();
                System.out.println("BOTON LOGIN click");

                if(userName.trim().length()>0 && password.trim().length()>0){
                    Data.Builder datos = new Data.Builder().putString("user", userName).putString("password", password);
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpLogin.class).setInputData(datos.build()).build();
                    WorkManager.getInstance(Login.this).getWorkInfoByIdLiveData(otwr.getId()).observe(Login.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                System.out.println("RESULTADO LOGIN --> "+resultadoPhp);
                                if (resultadoPhp) {//se logue?? correctamente
                                    Intent i = new Intent();
                                    i.putExtra("user",userName);
                                    setResult(Activity.RESULT_OK,i);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Email o contrase??a incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    WorkManager.getInstance(Login.this).enqueue(otwr);
                }else{
                    Toast.makeText(Login.this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Te lleva a la interfaz de Registrar usuario
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, SignUpActivity.class);
                startActivityForResult(i, 100);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                //System.out.println("SE RESISTRO");
                //El usuario fue agregado correctamente
                String username = data.getStringExtra("user");
                Intent iBack = new Intent();
                iBack.putExtra("user", username);
                setResult(Activity.RESULT_OK, iBack);
                finish();

            }
        }
    }
}