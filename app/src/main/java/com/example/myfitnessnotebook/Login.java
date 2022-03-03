package com.example.myfitnessnotebook;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;



public class Login extends AppCompatActivity {

    EditText user;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (EditText) findViewById(R.id.username);
        password =(EditText) findViewById(R.id.password);

        login(user.toString(), password.toString());

    }
    public boolean login (String user, String password){
        boolean exito = false;



        return exito;

    }
}