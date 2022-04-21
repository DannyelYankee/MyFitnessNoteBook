package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    EditText usernameText, psswd1Text, psswd2Text;
    String username, psswd1, psswd2;
    miBD gestorBD;
    Button btnSU;
    Boolean logueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        /*Recogemos los valores del formulario de registro de usuario*/
        usernameText = (EditText) findViewById(R.id.usernameSU);
        psswd1Text = (EditText) findViewById(R.id.passwordSU);
        psswd2Text = (EditText) findViewById(R.id.passwordSU2);



        /*Registramos el usuario en la BBDD*/
        btnSU = (Button) findViewById(R.id.btnLoginSU);
        btnSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameText.getText().toString();
                psswd1 = psswd1Text.getText().toString();
                psswd2 = psswd2Text.getText().toString();


                System.out.println("Email: " + username + " psswd1: " + psswd1 + " psswd2: " + psswd2);
                if (!psswd1.equals("") && !psswd2.equals("") && !username.equals("")) {
                    if (psswd1.equals(psswd2)) {
                        Data datos = new Data.Builder().putString("user", username).putString("password", psswd1).build();
                        //Constraints restricciones = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpRegistro.class).setInputData(datos).build();
                        WorkManager.getInstance(SignUpActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(SignUpActivity.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {
                                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                    System.out.println(resultadoPhp);
                                    if (resultadoPhp) {
                                        Intent i = new Intent();
                                        i.putExtra("user", username);
                                        setResult(Activity.RESULT_OK,i);
                                        finish();

                                    } else {
                                        Toast.makeText(SignUpActivity.this, "El email ya está en uso, por favor pruebe con otro", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        WorkManager.getInstance(SignUpActivity.this).enqueue(otwr);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}