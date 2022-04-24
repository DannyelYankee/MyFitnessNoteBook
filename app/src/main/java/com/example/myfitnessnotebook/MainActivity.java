package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SymbolTable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    //FloatingActionButton fab3;
    boolean isFABOpen, log;
    Button btnLogin;
    ListView listView;
    ArrayList<String> rutinas;
    HashMap<String, Integer> hashRutinas;
    ArrayList<Ejercicio> listaEjercicios;
    HashMap<String, ArrayList<Ejercicio>> hashEjercicios;
    //ArrayAdapter arrayAdapter;
    miBD gestorBD;
    TextView logueado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        listaEjercicios = new ArrayList<>();
        hashRutinas = new HashMap<>();
        hashEjercicios = new HashMap<>();
        logueado = (TextView) findViewById(R.id.logueadoMain);

        /*Menú flotante para agregar y eliminar rutinas a nuestro cuaderno*/
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        //fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab1.setOnClickListener(new View.OnClickListener() {
            /*Botón para añadir un ºamiento*/
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, addRoutine.class);
                if (log) {
                    i.putExtra("user", logueado.getText().toString());
                }
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
                        //rutinas = new ArrayList<>();
                        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), new String[]{}, new int[]{});
                        listView.setAdapter(arrayAdapter);

                        /*Preparamos notificación que saldrá con un icono de Warning para avisar de que la BBDD ha sido vaciada por completo*/

                        NotificationManager elManager = (NotificationManager) getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(MainActivity.this, "IdCanal");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel elCanal = new NotificationChannel("CanalBBDD", "Notificacion Eliminar", NotificationManager.IMPORTANCE_DEFAULT);
                            elManager.createNotificationChannel(elCanal);
                        }
                        elBuilder.setSmallIcon(R.drawable.ic_baseline_warning_24);
                        elBuilder.setContentTitle("Base de Datos vaciada");
                        elBuilder.setContentText("Se ha vaciado la Base de Datos");
                        elBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        elBuilder.setAutoCancel(true);
                        elManager.notify(1, elBuilder.build());
                    }
                }).setNegativeButton("No", null).show();
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

        /*Boton login*/
        btnLogin = (Button) findViewById(R.id.btn_login);
        System.out.println("BotonLogin --> " + btnLogin.getText().toString());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnLogin.getText().toString().equals("Iniciar Sesión")) {
                    Intent iLogin = new Intent(MainActivity.this, Login.class);
                    startActivityForResult(iLogin, 20);
                } else { //Se gestiona el comportamiento de cerrar sesión
                    Toast.makeText(MainActivity.this, "Se cierra la sesion", Toast.LENGTH_SHORT).show();
                    logueado.setText("");
                    btnLogin.setText("Iniciar Sesión");
                    vaciarRutinas();
                }
            }
        });

        /*List view para mostrar las rutinas creadas dinámicamente SIN ESTAR LOGUEADO*/
        listView = findViewById(R.id.listViewRutinas);
        rutinas = new ArrayList<>();

        if (!log) {
            System.out.println("no loguoese");
            /*
            hashRutinas = new HashMap<>();
            rutinas = gestorBD.getRutinas();
            this.inicializarHashMap();
            //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
            int[] imagenes = {R.drawable.zyzz};
            String[] rutinasArray = this.convertirArray(rutinas);
            AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String nombreRutina = rutinas.get(i);
                    if (hashRutinas.get(nombreRutina) == 0) {
                        //Si la rutina se acaba de crear se lleva al usuario a una interfaz para que añada el primer ejercicio

                        Intent iEjercicio = new Intent(MainActivity.this, addEjercicio.class);
                        iEjercicio.putExtra("nombreRutina", nombreRutina);
                        iEjercicio.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                        startActivityForResult(iEjercicio, 2);
                    } else {
                        //*Si la rutina ya tiene algún ejercicio se lleva al usuario a una interfaz donde aparecen listados los ejericios
                         //* y podrá añadir más ejercicios, editarlos y/o borrarlos

                        Intent iVerEditar = new Intent(MainActivity.this, VerEditarRutina.class);
                        System.out.println(nombreRutina);
                        iVerEditar.putExtra("nombreRutina", nombreRutina);
                        iVerEditar.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                        startActivityForResult(iVerEditar, 4);
                    }
                }
            });
            //Al clickar durante x segundos una rutina saltará una alerta para eliminar la rutina si así lo desea el usuario
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
                            AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                            listView.setAdapter(arrayAdapter);
                        }
                    }).setNegativeButton("No", null).show();

                    return true;

                }
            });
            */

        } else {


            Data datos = new Data.Builder().putString("user", logueado.getText().toString()).build();
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectRutinas.class).setInputData(datos).build();
            WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                        System.out.println(resultadoPhp);
                        if (resultadoPhp) {
                            String[] rutinasArray = workInfo.getOutputData().getStringArray("rutinas");
                            actualizarRutinas(rutinasArray);
                            System.out.println("ANTES DE INICIALIZAR->"+rutinas);
                            inicializarHashMap();
                            int[] imagenes = {R.drawable.zyzz};
                            AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                            listView.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(MainActivity.this, "No tiene rutinas", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            WorkManager.getInstance(MainActivity.this).enqueue(otwr);

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String nombreRutina = rutinas.get(i);
                System.out.println("PULSASTE EN ----> "+nombreRutina);
                System.out.println(hashRutinas);
                if (hashRutinas.get(nombreRutina) == 0) {
                    /*Si la rutina se acaba de crear se lleva al usuario a una interfaz para que añada el primer ejercicio*/

                    Intent iEjercicio = new Intent(MainActivity.this, addEjercicio.class);
                    iEjercicio.putExtra("nombreRutina", nombreRutina);
                    iEjercicio.putExtra("usuario",logueado.getText().toString());
                    iEjercicio.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                    startActivityForResult(iEjercicio, 2);
                } else {
                    /*Si la rutina ya tiene algún ejercicio se lleva al usuario a una interfaz donde aparecen listados los ejericios
                     * y podrá añadir más ejercicios, editarlos y/o borrarlos*/
                    System.out.println(hashRutinas);
                    Intent iVerEditar = new Intent(MainActivity.this, VerEditarRutina.class);
                    System.out.println(nombreRutina);
                    iVerEditar.putExtra("nombreRutina", nombreRutina);
                    iVerEditar.putExtra("user",logueado.getText().toString());
                    iVerEditar.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                    startActivityForResult(iVerEditar, 4);
                }
            }
        });
        /*Al clickar durante x segundos una rutina saltará una alerta para eliminar la rutina si así lo desea el usuario*/
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                new AlertDialog.Builder(MainActivity.this).setTitle("Eliminar rutina").setMessage("¿Deseas eliminar la rutina?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //gestorBD.eliminarRutina(rutinas.get(position));
                        hashRutinas.remove(rutinas.get(position));
                        rutinas.remove(position);
                        int[] imagenes = {R.drawable.zyzz};
                        String[] rutinasArray = convertirArray(rutinas);
                        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                        listView.setAdapter(arrayAdapter);
                    }
                }).setNegativeButton("No", null).show();

                return true;

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //La rutina se agregó correctamente
                String rutina = data.getStringExtra("rutina");
                //  rutinas = gestorBD.getRutinas();
                //ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
                //this.hashRutinas.put(rutina, ejercicios.size());
                //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                //listView.setAdapter(arrayAdapter);
                inicializarHashMap();
                Data datos = new Data.Builder().putString("user", logueado.getText().toString()).build();
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectRutinas.class).setInputData(datos).build();
                WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                            System.out.println(resultadoPhp);
                            if (resultadoPhp) {
                                String[] rutinasArray = workInfo.getOutputData().getStringArray("rutinas");
                                actualizarRutinas(rutinasArray);
                                int[] imagenes = {R.drawable.zyzz};
                                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                                listView.setAdapter(arrayAdapter);
                            } else {
                                Toast.makeText(MainActivity.this, "No tiene rutinas", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                WorkManager.getInstance(MainActivity.this).enqueue(otwr);

            }
        }
        if (requestCode == 2) {//se ha añadido el primer ejer a la rutina
            if (resultCode == RESULT_OK) {
                System.out.println("se ha añadido el primer ejer a la rutina");
                //String rutina = data.getStringExtra("rutina");
                //ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
                //this.hashRutinas.put(rutina, ejercicios.size());
                inicializarHashMap();
                System.out.println(hashRutinas);
                Data datos = new Data.Builder().putString("user", logueado.getText().toString()).build();
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectRutinas.class).setInputData(datos).build();
                WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                            System.out.println(resultadoPhp);
                            if (resultadoPhp) {
                                String[] rutinasArray = workInfo.getOutputData().getStringArray("rutinas");
                                actualizarRutinas(rutinasArray);
                                int[] imagenes = {R.drawable.zyzz};
                                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                                listView.setAdapter(arrayAdapter);
                            } else {
                                Toast.makeText(MainActivity.this, "No tiene rutinas", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                WorkManager.getInstance(MainActivity.this).enqueue(otwr);
            }
        }
        if (requestCode == 4) { //Se ha añadido algun ejercicio más desde la clase VerEditarRutina
            if (resultCode == RESULT_OK) {
                String rutina = data.getStringExtra("rutina");
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina);
                this.hashRutinas.put(rutina, ejercicios.size());
                Data datos = new Data.Builder().putString("user", logueado.getText().toString()).build();
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectRutinas.class).setInputData(datos).build();
                WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                            System.out.println(resultadoPhp);
                            if (resultadoPhp) {
                                String[] rutinasArray = workInfo.getOutputData().getStringArray("rutinas");
                                actualizarRutinas(rutinasArray);
                                int[] imagenes = {R.drawable.zyzz};
                                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                                listView.setAdapter(arrayAdapter);
                            } else {
                                Toast.makeText(MainActivity.this, "No tiene rutinas", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                WorkManager.getInstance(MainActivity.this).enqueue(otwr);


            }
        }
        if (requestCode == 20) { //Se ha logueado correctamente
            if (resultCode == RESULT_OK) {
                String username = data.getStringExtra("user");
                System.out.println("LOGUEADO --> " + username);
                logueado.setText(username);
                btnLogin.setText("Cerrar sesión");
                log = true;
                this.inicializarHashMap();
                Data datos = new Data.Builder().putString("user", username).build();
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectRutinas.class).setInputData(datos).build();
                WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                            System.out.println(resultadoPhp);
                            if (resultadoPhp) {
                                String[] rutinasArray = workInfo.getOutputData().getStringArray("rutinas");
                                int[] imagenes = {R.drawable.zyzz};
                                actualizarRutinas(rutinasArray);
                                System.out.println("ANTES DE INICIALIZAR HASH--> " + rutinas);
                                inicializarHashMap();
                                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                                listView.setAdapter(arrayAdapter);
                            } else {
                                Toast.makeText(MainActivity.this, "No tiene rutinas", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                WorkManager.getInstance(MainActivity.this).enqueue(otwr);


            }
        }
    }

    public String[] convertirArray(ArrayList<String> lista) {
        String[] array = new String[lista.size()];
        int j = 0;
        for (String i : lista) {
            array[j] = i;
            j++;
        }
        return array;
    }

    public void inicializarHashMap() {
        /*Nada más loguearnos se hará un SELECT de las rutinas y sus ejercicos que tenga el usuario almacenados
        * Se incializa un hashmap con los pares Rutina - numEjercicios */
        for (String rutina : this.rutinas) {
            System.out.println("-----------------------------------------------"+rutina+"-----------------------------------------------");
            Data datos = new Data.Builder().putString("user", logueado.getText().toString()).putString("rutina", rutina).build();
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectEjercicios.class).setInputData(datos).build();
            WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                        System.out.println(resultadoPhp);
                        if (resultadoPhp) {
                            String[] ejerciciosArray = workInfo.getOutputData().getStringArray("ejercicios");
                            System.out.println("GET EJERCICIOS:");
                            System.out.println(ejerciciosArray[0]);
                            for (int i = 0; i < ejerciciosArray.length; i++) {
                                if (!ejerciciosArray[i].equals("false")) {
                                    inicializarEjercicios(rutina, ejerciciosArray[i]);
                                }
                            }
                            if(hashEjercicios.containsKey(rutina)){
                                hashRutinas.put(rutina,hashEjercicios.get(rutina).size());
                            }else{
                                hashRutinas.put(rutina,0);
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "No tiene rutinas", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            WorkManager.getInstance(MainActivity.this).enqueue(otwr);

        }
        System.out.println(hashRutinas);
    }

    public void inicializarEjercicios(String rutina, String ejercicio) {
        /*Se inicializa un HashMap con los valores Rutina - Ejericicios; siendo este una lista del tipo Ejercicio*/

        System.out.println("-----------------------------INICIALIZAR EJERCICIOS-----------------------------");
        System.out.println(ejercicio);
        /*Tratamiento de strings para crear instancias del tipo Ejercicio*/
        String[] sinComas = ejercicio.split(",");
        ArrayList<String> valores = new ArrayList<>();
        for (int i = 0; i < sinComas.length; i++) {
            valores.add(sinComas[i].split(":")[1].replace('"', ' ').trim());
        }
        String ultimoValor = valores.get(valores.size() - 1);
        valores.remove(valores.size() - 1);
        valores.add(ultimoValor.substring(0, ultimoValor.length() - 2));

        /*Creamos el objeto Ejercicio, lo añadimos a la lista de ejercicios y lo vinculamos con su rutina correspondiente haciendo uso de un HashMap*/
        String nombre = valores.get(0), usuario = valores.get(5);
        int numRepes = Integer.parseInt(valores.get(1)), numSeries = Integer.parseInt(valores.get(2)), peso = Integer.parseInt(valores.get(3));
        Ejercicio nuevoEjericio = new Ejercicio(nombre, rutina, usuario, numRepes, numSeries, peso);
        this.listaEjercicios.add(nuevoEjericio);
        this.hashEjercicios.put(rutina, listaEjercicios);
        System.out.println(this.hashEjercicios);
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

    public void vaciarRutinas() {
        int[] imagenes = {R.drawable.zyzz};
        String[] rutinasArray = new String[0];
        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
        listView.setAdapter(arrayAdapter);
    }

    public void actualizarRutinas(String[] lista) {
        for (int i = 0; i < lista.length; i++) {
            if (!this.rutinas.contains(lista[i])) {
                this.rutinas.add(lista[i]);
            }
        }
    }


}