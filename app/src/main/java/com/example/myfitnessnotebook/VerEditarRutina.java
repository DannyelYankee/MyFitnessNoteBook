package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
        arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaEjercicios) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View vista = super.getView(position, convertView, parent);
                TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                String nombreEjercicio = listaEjercicios.get(position);
                lineaPrincipal.setText(nombreEjercicio);
                ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina);
                String info = infoEjercicio.get(0) + "x" + infoEjercicio.get(1) + " con " + infoEjercicio.get(2) + "Kg";
                lineaSecundaria.setText(info);
                return vista;
            }
        };
        listView.setAdapter(arrayAdapter);

        /*Botón para añadir otro Ejercicio*/
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
        /*Al clickar en un item de la lista llevará al usuario a una interfaz dónde podrá Editar los datos del ejercicio*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ejercicio = listaEjercicios.get(i);
                System.out.println(ejercicio);
                ArrayList<Integer> infoEjer = gestorBD.getInfoEjercicio(ejercicio, rutina);
                System.out.println(infoEjer);

                Intent iVerEditar = new Intent(VerEditarRutina.this, EditarEjercicio.class);
                iVerEditar.putExtra("nombreEjercicio", ejercicio);
                iVerEditar.putExtra("numSeries", infoEjer.get(0).toString());
                iVerEditar.putExtra("numRepes", infoEjer.get(1).toString());
                iVerEditar.putExtra("peso", infoEjer.get(2).toString());
                iVerEditar.putExtra("rutina", rutina);
                startActivityForResult(iVerEditar, 10);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                rutina = getIntent().getStringExtra("nombreRutina");
                listaEjercicios = gestorBD.getEjercicios(rutina);
                arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaEjercicios) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View vista = super.getView(position, convertView, parent);
                        TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                        TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                        String nombreEjercicio = listaEjercicios.get(position);
                        lineaPrincipal.setText(nombreEjercicio);
                        ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina);
                        String info = infoEjercicio.get(0) + "x" + infoEjercicio.get(1) + " con " + infoEjercicio.get(2) + "Kg";
                        lineaSecundaria.setText(info);
                        return vista;
                    }
                };
                listView.setAdapter(arrayAdapter);
                Intent iBack = new Intent();
                iBack.putExtra("rutina", rutina);
                setResult(Activity.RESULT_OK, iBack);

            }
        }
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {

                rutina = getIntent().getStringExtra("nombreRutina");
                listaEjercicios = gestorBD.getEjercicios(rutina);
                arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaEjercicios) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View vista = super.getView(position, convertView, parent);
                        TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                        TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                        String nombreEjercicio = listaEjercicios.get(position);
                        lineaPrincipal.setText(nombreEjercicio);
                        ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina);
                        String info = infoEjercicio.get(0) + "x" + infoEjercicio.get(1) + " con " + infoEjercicio.get(2) + "Kg";
                        lineaSecundaria.setText(info);
                        return vista;
                    }
                };
                listView.setAdapter(arrayAdapter);

                Intent iBack = new Intent();
                iBack.putExtra("rutina", rutina);
                setResult(Activity.RESULT_OK, iBack);

            }
        }
    }
}