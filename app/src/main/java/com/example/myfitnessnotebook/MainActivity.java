package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    boolean isFABOpen;
    Button btnLogin;
    ListView listView;
    ArrayList<String> rutinas;
    HashMap<String, Integer> hashRutinas;
    ArrayAdapter arrayAdapter;
    miBD gestorBD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);

        /*Menú flotante para agregar y eliminar rutinas a nuestro cuaderno*/
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Añadir nuevo entrenamiento", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, addRoutine.class);
                startActivityForResult(i, 1);
                closeFABMenu();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.deleteDatabase("MyFitnessNotebook");
                Toast.makeText(MainActivity.this, "BBDD borrada con exito", Toast.LENGTH_SHORT).show();
                rutinas = new ArrayList<>();
                arrayAdapter.clear();
                arrayAdapter.notifyDataSetChanged();
                closeFABMenu();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        /*Boton login*/
        btnLogin = (Button) findViewById(R.id.btn_login);
        Intent iLogin = new Intent(this, Login.class);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(iLogin, 2);
            }
        });

        /*List view para mostrar las rutinas creadas dinamicamente*/
        listView = findViewById(R.id.listViewRutinas);
        //rutinas = gestorBD.getRutinas();
        hashRutinas = new HashMap<>();
        rutinas = gestorBD.getRutinas();
        this.inicializarHashMap();
        arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreRutina = rutinas.get(i);
                Toast.makeText(MainActivity.this, "Pulsaste " + nombreRutina, Toast.LENGTH_SHORT).show();
                System.out.println(nombreRutina + " --> " + hashRutinas.get(nombreRutina));
                if (hashRutinas.get(nombreRutina) == 0) {
                    Intent iEjercicio = new Intent(MainActivity.this, addEjercicio.class);
                    iEjercicio.putExtra("nombreRutina", nombreRutina);
                    iEjercicio.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                    startActivityForResult(iEjercicio, 2);
                } else {
                    Toast.makeText(MainActivity.this, "Se abrira ver/editar rutina", Toast.LENGTH_SHORT).show();
                    Intent iVerEditar = new Intent(MainActivity.this, VerEditarRutina.class);
                    iVerEditar.putExtra("nombreRutina", nombreRutina);
                    startActivity(iVerEditar);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String rutina = data.getStringExtra("rutina");
                System.out.println(rutina);
                rutinas = gestorBD.getRutinas();
                this.inicializarHashMap();
                arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                listView.setAdapter(arrayAdapter);
            }
        }
        if (requestCode == 2) {//se ha añadido el primer ejer a la rutina
            if (resultCode == RESULT_OK) {
                System.out.println("se ha añadido el primer ejer a la rutina");
                String rutina = data.getStringExtra("rutina");
                hashRutinas.put(rutina, hashRutinas.get(rutina) + 1);
                rutinas = gestorBD.getRutinas();
                arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                listView.setAdapter(arrayAdapter);
            }
        }
    }

    private void inicializarHashMap() {
        for (String i : this.rutinas) {
            this.hashRutinas.put(i, 0);
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_125));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_175));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }

}