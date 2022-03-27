package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class VerEditarRutina extends AppCompatActivity {
    ListView listView;
    miBD gestorBD;
    String rutina;
    ArrayList<String> listaEjercicios;
    ArrayAdapter arrayAdapter;
    Button btn_otroEjer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_editar_rutina);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);


        /*Cargamos los ejercicios de la rutina*/
        listView = (ListView) findViewById(R.id.rutinaConEjer);
        rutina = getIntent().getStringExtra("nombreRutina");
        listaEjercicios = gestorBD.getEjercicios(rutina);
        arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_1, listaEjercicios);
        listView.setAdapter(arrayAdapter);

        btn_otroEjer = (Button) findViewById(R.id.btn_otroEjer);
        btn_otroEjer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreRutina = getIntent().getStringExtra("nombreRutina");
                String numEjer = getIntent().getStringExtra("numEjer");
                Intent i = new Intent(VerEditarRutina.this, addEjercicio.class);
                i.putExtra("nombreRutina", nombreRutina);
                i.putExtra("numEjer", numEjer);
                startActivityForResult(i, 3);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                String rutina = data.getStringExtra("rutina");
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
                arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_1, ejercicios);
                listView.setAdapter(arrayAdapter);
                Intent iBack = new Intent();
                iBack.putExtra("rutina",rutina);
                setResult(Activity.RESULT_OK, iBack);
                finish();
            }
        }
    }
}