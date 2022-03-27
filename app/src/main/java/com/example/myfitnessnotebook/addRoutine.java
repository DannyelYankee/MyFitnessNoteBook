package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class addRoutine extends AppCompatActivity {
    miBD gestorBD;
    EditText nombreRutinaText;
    String nombreRutina;
    Boolean agregado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);
        Button add = (Button) findViewById(R.id.btn_addRoutine);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombreRutinaText = (EditText) findViewById(R.id.addRoutine_nombreRutina);
                nombreRutina = nombreRutinaText.getText().toString();
                if (nombreRutina.equals("")) {
                    Toast.makeText(addRoutine.this, "Por favor, ingrese un nombre para la rutina", Toast.LENGTH_SHORT).show();
                } else {
                    agregado = gestorBD.agregarRutina(nombreRutina);
                    if (agregado) {
                        Intent i = new Intent();
                        i.putExtra("rutina", nombreRutina.toString());
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(addRoutine.this, "Ya existe una rutina con el nombre " + nombreRutina + ". Por favor pruebe con otro nombre.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


}