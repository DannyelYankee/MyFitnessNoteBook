package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

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

        username = usernameText.getText().toString();
        psswd1 = psswd1Text.getText().toString();
        psswd2 = psswd2Text.getText().toString();

        /*Registramos el usuario en la BBDD*/
        btnSU = (Button) findViewById(R.id.btnLoginSU);
        btnSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (psswd1 == psswd2) {
                    if(agregarUsuario(username, psswd1)){
                        logueado = true;
                        Intent i = new Intent();
                        i.putExtra("logueado",logueado);
                    }

                } else {
                    Toast.makeText(SignUpActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public SQLiteDatabase getBD() {
        gestorBD = new miBD(this,"MyFitnessNotebook",null,1);
        SQLiteDatabase bd = gestorBD.getWritableDatabase();
        return bd;
    }

    public boolean agregarUsuario(String email, String psswd) {
        SQLiteDatabase bd = this.getBD();
        Boolean agreado = false;
        /*Primero comprobamos que no exista un usuario con este correo*/
        String selection = "correo LIKE ?";
        String selectionArgs[] = new String[]{email};
        Cursor c = bd.query("Users", null, selection, selectionArgs, null, null, null, null);
        if (c.moveToFirst() && c.getCount() == 0) { // No hay usuarios con este correo
            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("contra", psswd);
            bd.insert("Users", null, values);
            agreado = true;
        }


        return agreado;
    }

}