package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    boolean isFABOpen;
    Button btnLogin;
    ListView listView;
    ArrayList<String> rutinas;
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                startActivityForResult(i,1);
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
                startActivity(iLogin);
                finish();
            }
        });

        /*List view para mostrar las rutinas creadas dinamicamente*/
        listView = findViewById(R.id.listViewRutinas);
        rutinas = new ArrayList<>();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK){
            String rutina = data.getStringExtra("rutina");
            System.out.println(rutina);
            rutinas.add(rutina);
            arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
            listView.setAdapter(arrayAdapter);
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