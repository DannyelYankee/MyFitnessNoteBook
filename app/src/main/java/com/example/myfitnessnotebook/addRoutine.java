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
    SQLiteDatabase bd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);
        Button add = (Button) findViewById(R.id.btn_addRoutine);
        gestorBD = new miBD(this,"MyFitnessNotebook",null,1);
        SQLiteDatabase bd = gestorBD.getWritableDatabase();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombreRutinaText = (EditText) findViewById(R.id.addRoutine_nombreRutina);
                nombreRutina = nombreRutinaText.getText().toString();
                agregado = agregarRutina(nombreRutina);
                if(agregado){
                    Intent i = new Intent();
                    i.putExtra("rutina", nombreRutina.toString());
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }else{
                    Toast.makeText(addRoutine.this, "Ya existe una rutina con el nombre "+nombreRutina+". Por favor pruebe con otro nombre.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public SQLiteDatabase getBD() {

        return bd;
    }

    public boolean agregarRutina(String rutina) {
        /*Vamos a comprobar que no haya una rutina con el mismo nombre*/

        String selection = "nombre = ?";
        String selectionArgs[] = new String[]{rutina};
        //SQLiteDatabase bd = this.getBD();
        Cursor c = bd.query("Rutinas", null, selection, selectionArgs, null, null, null);
        System.out.println("Select hecho");
        if (c.moveToFirst() && c.getCount() == 0) { //No existe ninguna rutina con ese nombre
            ContentValues values = new ContentValues();
            values.put("nombre",rutina);
            bd.insert("Rutinas", null, values);
            agregado = true;
            System.out.println("Rutina agregada");
        }


        return agregado;
    }
}