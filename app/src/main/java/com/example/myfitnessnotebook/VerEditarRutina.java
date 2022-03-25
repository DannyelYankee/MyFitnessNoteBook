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
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_editar_rutina);

        listView = (ListView) findViewById(R.id.rutinaConEjer);
        rutina = getIntent().getStringExtra("nombreRutina");

        arrayAdapter = new ArrayAdapter(VerEditarRutina.this, R.layout.listview_rutinas, this.getEjercicios());
        listView.setAdapter(arrayAdapter);

    }
    public SQLiteDatabase getBD(){
        gestorBD = new miBD(this,"MyFitnessNotebook",null,1);
        SQLiteDatabase bd = gestorBD.getWritableDatabase();
        return bd;
    }
    public ArrayList<String> getEjercicios(){
        ArrayList<String> ejercicios = new ArrayList<>();

        String selection = "rutina LIKE ?";
        String selectionArgs[] = new String[]{rutina};
        SQLiteDatabase bd = this.getBD();
        Cursor c = bd.query("Rutinas", null, selection, selectionArgs, null, null, null, null);
        while(c.moveToNext()){
            String nombre = c.getString(1);
            ejercicios.add(nombre);
        }
        return ejercicios;
    }
}