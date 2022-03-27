package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
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
    //FloatingActionButton fab3;
    boolean isFABOpen;
    Button btnLogin;
    ListView listView;
    ArrayList<String> rutinas;
    HashMap<String, Integer> hashRutinas;
    //ArrayAdapter arrayAdapter;
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
        //fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab1.setOnClickListener(new View.OnClickListener() {
            /*Botón para añadir un entrenamiento*/
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, addRoutine.class);
                startActivityForResult(i, 1);
                closeFABMenu();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            /*Botón para borrar toda la BBDD
             * Aparecerá un dialago para advertir de lo que va a ocurrir*/
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Borrar toda la BBDD").setMessage("Se borrarán todos los datos de la aplicación, ¿Desea continuar?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.deleteDatabase("MyFitnessNotebook");
                        rutinas = new ArrayList<>();
                        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(),new String[]{},new int[]{});
                        listView.setAdapter(arrayAdapter);

                        NotificationManager elManager = (NotificationManager) getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(MainActivity.this, "IdCanal");

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            NotificationChannel elCanal = new NotificationChannel("CanalBBDD", "Notificacion Eliminar",NotificationManager.IMPORTANCE_DEFAULT);
                            elManager.createNotificationChannel(elCanal);
                        }
                        elBuilder.setSmallIcon(R.drawable.ic_baseline_warning_24);
                        elBuilder.setContentTitle("Base de Datos vaciada");
                        elBuilder.setContentText("Se ha vaciado la Base de Datos");
                        elBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        elBuilder.setAutoCancel(true);
                        elManager.notify(1,elBuilder.build());
                    }
                }).setNegativeButton("No",null).show();

                closeFABMenu();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            /*Comportamiento del botón flotante*/
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        /*Boton login
        * PROXIMAMENTE.... DE MOMENTO NO ESTA OPERATIVO*/
        btnLogin = (Button) findViewById(R.id.btn_login);
        Intent iLogin = new Intent(this, Login.class);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(iLogin, 2);
            }
        });

        /*List view para mostrar las rutinas creadas dinámicamente*/
        listView = findViewById(R.id.listViewRutinas);
        hashRutinas = new HashMap<>();
        rutinas = gestorBD.getRutinas();
        this.inicializarHashMap();
        //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
        int[] imagenes = {R.drawable.zyzz};
        String[] rutinasArray = this.convertirArray(rutinas);
        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(),rutinasArray,imagenes);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreRutina = rutinas.get(i);
                if (hashRutinas.get(nombreRutina) == 0) {
                    /*Si la rutina se acaba de crear se lleva al usuario a una interfaz para que añada el primer ejercicio*/

                    Intent iEjercicio = new Intent(MainActivity.this, addEjercicio.class);
                    iEjercicio.putExtra("nombreRutina", nombreRutina);
                    iEjercicio.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                    startActivityForResult(iEjercicio, 2);
                } else {
                    /*Si la rutina ya tiene algún ejercicio se lleva al usuario a una interfaz donde aparecen listados los ejericios
                     * y podrá añadir más ejercicios, editarlos y/o borrarlos*/

                    Intent iVerEditar = new Intent(MainActivity.this, VerEditarRutina.class);
                    System.out.println(nombreRutina);
                    iVerEditar.putExtra("nombreRutina", nombreRutina);
                    iVerEditar.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                    startActivityForResult(iVerEditar, 4);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                new AlertDialog.Builder(MainActivity.this).setTitle("Eliminar rutina").setMessage("¿Deseas eliminar la rutina?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gestorBD.eliminarRutina(rutinas.get(position));
                        hashRutinas.remove(rutinas.get(position));
                        rutinas.remove(position);
                        int[] imagenes = {R.drawable.zyzz};
                        String[] rutinasArray = convertirArray(rutinas);
                        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(),rutinasArray,imagenes);
                        listView.setAdapter(arrayAdapter);
                    }
                }).setNegativeButton("No",null).show();

                return true;

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String rutina = data.getStringExtra("rutina");
                rutinas = gestorBD.getRutinas();
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
                this.hashRutinas.put(rutina, ejercicios.size());
                //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                //listView.setAdapter(arrayAdapter);
                int[] imagenes = {R.drawable.zyzz};
                String[] rutinasArray = this.convertirArray(rutinas);
                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(),rutinasArray,imagenes);
                listView.setAdapter(arrayAdapter);
            }
        }
        if (requestCode == 2) {//se ha añadido el primer ejer a la rutina
            if (resultCode == RESULT_OK) {
                System.out.println("se ha añadido el primer ejer a la rutina");
                String rutina = data.getStringExtra("rutina");
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
                this.hashRutinas.put(rutina, ejercicios.size());
                System.out.println(hashRutinas);
                rutinas = gestorBD.getRutinas();
                //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                //listView.setAdapter(arrayAdapter);
                int[] imagenes = {R.drawable.zyzz};
                String[] rutinasArray = this.convertirArray(rutinas);
                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(),rutinasArray,imagenes);
                listView.setAdapter(arrayAdapter);
            }
        }
        if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                String rutina = data.getStringExtra("rutina");
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
                this.hashRutinas.put(rutina, ejercicios.size());
                rutinas = gestorBD.getRutinas();
                //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                //listView.setAdapter(arrayAdapter);
                int[] imagenes = {R.drawable.zyzz};
                String[] rutinasArray = this.convertirArray(rutinas);
                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(),rutinasArray,imagenes);
                listView.setAdapter(arrayAdapter);

            }
        }
    }
    public String[] convertirArray(ArrayList<String> lista){
        String[] array = new String[lista.size()];
        int j= 0;
        for(String i : lista){
            array[j] = i;
            j++;
        }
        return array;
    }
    private void inicializarHashMap() {

        ArrayList<String> rutinas = gestorBD.getRutinas();
        System.out.println(rutinas);
        for (String rutina : rutinas) {
            ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
            this.hashRutinas.put(rutina, ejercicios.size());
        }
        System.out.println(hashRutinas);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_125));
        //fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_175));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        //fab3.animate().translationY(0);
    }

}