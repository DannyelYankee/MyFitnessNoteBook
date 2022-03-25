package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {
    EditText userNameText, passwordText;
    String userName, password;
    Button btnLogin;
    miBD gestorBD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);

        userName = userNameText.getText().toString();
        password = passwordText.getText().toString();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(userName,password);
            }
        });

    }

    public SQLiteDatabase getBD(){
        gestorBD = new miBD(this,"MyFitnessNotebook",null,1);
        SQLiteDatabase bd = gestorBD.getWritableDatabase();
        return bd;
    }
    public void login(String user, String pass){
        /*Primero vamos a comprobar que el usuario existe en nuestra BBDD*/
        String selection = "correo = ? AND contra= ?";
        String selectionArgs[] = new String[]{user,pass};
        SQLiteDatabase bd = this.getBD();
        Cursor c =bd.query("Users",null,selection,selectionArgs,null,null,null,null);
        if (c.moveToFirst() && c.getCount() > 0){ //si existe

        }else{ //Como no existe el usuario lo mandamos a la venta de registro
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
            finish();
        }

    }
}