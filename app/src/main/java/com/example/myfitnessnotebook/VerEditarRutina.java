package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class VerEditarRutina extends AppCompatActivity {
    ListView listView;
    miBD gestorBD;
    String rutina;
    ArrayList<String> listaEjercicios;
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_editar_rutina);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);

        listView = (ListView) findViewById(R.id.rutinaConEjer);
        rutina = getIntent().getStringExtra("nombreRutina");
        listaEjercicios = gestorBD.getEjercicios(rutina);
        arrayAdapter = new ArrayAdapter(VerEditarRutina.this,  android.R.layout.simple_list_item_1, listaEjercicios);
        listView.setAdapter(arrayAdapter);

    }

}