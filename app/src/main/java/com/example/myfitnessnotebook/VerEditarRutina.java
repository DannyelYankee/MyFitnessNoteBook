package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class VerEditarRutina extends AppCompatActivity {
    ListView listView;
    miBD gestorBD;
    String rutina, user;
    ArrayList<Ejercicio> listaEjercicios;
    ArrayAdapter arrayAdapter;
    int cont;
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    boolean isFABOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_editar_rutina);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        /*Menú flotante para agregar y eliminar ejercicios a nuestro cuaderno*/
        fab = (FloatingActionButton) findViewById(R.id.fabVer);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1Ver);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2Ver);
        listaEjercicios = new ArrayList<>();
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
        cont = 0;

        /*Cargamos los ejercicios de la rutina*/
        listView = (ListView) findViewById(R.id.rutinaConEjer);
        rutina = getIntent().getStringExtra("nombreRutina");
        user = getIntent().getStringExtra("user");
        //listaEjercicios = gestorBD.getEjercicios(rutina);
        Data datos = new Data.Builder().putString("user", user).putString("rutina", rutina).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectEjercicios.class).setInputData(datos).build();
        WorkManager.getInstance(VerEditarRutina.this).getWorkInfoByIdLiveData(otwr.getId()).observe(VerEditarRutina.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                    System.out.println(resultadoPhp);
                    if (resultadoPhp) {
                        String[] ejerciciosArray = workInfo.getOutputData().getStringArray("ejercicios");
                        System.out.println("GET EJERCICIOS:" + ejerciciosArray);

                        for (int i = 0; i < ejerciciosArray.length; i++) {
                            if (!ejerciciosArray[i].equals("false")) {
                                Ejercicio nuevoEjercicio = getEjercicio(rutina, ejerciciosArray[i]);
                                if (!listaEjercicios.contains(nuevoEjercicio)) {
                                    agregarEjercicio(nuevoEjercicio);
                                }
                            }
                        }

                        System.out.println("POST SELECT->>>>" + listaEjercicios);
                        ArrayList<String> listaNombresEjercicios = new ArrayList<>();
                        for (Ejercicio i : listaEjercicios) {
                            listaNombresEjercicios.add(i.getNombre());
                        }
                        System.out.println(listaNombresEjercicios);
                        arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaNombresEjercicios) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View vista = super.getView(position, convertView, parent);
                                TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                                TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                                Ejercicio ejercicio = listaEjercicios.get(position);
                                String nombreEjercicio = ejercicio.getNombre();
                                lineaPrincipal.setText(nombreEjercicio);
                                //ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina);
                                String info = ejercicio.getNumSeries() + "x" + ejercicio.getNumRepes() + " con " + ejercicio.getPeso() + "Kg";
                                lineaSecundaria.setText(info);
                                return vista;
                            }
                        };
                        listView.setAdapter(arrayAdapter);
                        /*Botón para añadir otro Ejercicio*/
                        fab1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String nombreRutina = getIntent().getStringExtra("nombreRutina");
                                String numEjer = getIntent().getStringExtra("numEjer");
                                Intent i = new Intent(VerEditarRutina.this, addEjercicio.class);
                                i.putExtra("user", user);
                                i.putExtra("nombreRutina", nombreRutina);
                                i.putExtra("numEjer", numEjer);
                                startActivityForResult(i, 3);
                            }
                        });

                        /*Botón para eliminar todos los ejercicios de la rutina de un solo golpe*/
                        fab2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(VerEditarRutina.this).setTitle("Eliminar todos los ejercicios").setMessage("Se eliminarán todos los ejercicios de la rutina, ¿Desea continuar?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //ArrayList<String> todosEjercicios = gestorBD.getEjercicios(rutina);
                                        for (Ejercicio ejercicio : listaEjercicios) {
                                            //gestorBD.eliminarEjercicio(ejercicio, rutina);
                                            /*Se elimina de la BBDD*/
                                            Data datos = new Data.Builder().putString("user", ejercicio.getUsuario()).putString("rutina", ejercicio.getRutina()).putString("nombre", ejercicio.getNombre()).build();
                                            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpEliminarEjercicio.class).setInputData(datos).build();
                                            WorkManager.getInstance(VerEditarRutina.this).getWorkInfoByIdLiveData(otwr.getId()).observe(VerEditarRutina.this, new Observer<WorkInfo>() {
                                                @Override
                                                public void onChanged(WorkInfo workInfo) {
                                                    if (workInfo != null && workInfo.getState().isFinished()) {
                                                        Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                                        System.out.println(resultadoPhp);
                                                        if (resultadoPhp) {
                                                            listaEjercicios.clear();
                                                            arrayAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            });
                                            WorkManager.getInstance(VerEditarRutina.this).enqueue(otwr);
                                        }
                                    }
                                }).setNegativeButton("No", null).show();

                            }
                        });
                        /*Al clickar en un item de la lista llevará al usuario a una interfaz dónde podrá Editar los datos del ejercicio*/
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Ejercicio ejercicio = listaEjercicios.get(i);
                                System.out.println(ejercicio.getNombre());
                                //ArrayList<Integer> infoEjer = gestorBD.getInfoEjercicio(ejercicio, rutina);


                                Intent iVerEditar = new Intent(VerEditarRutina.this, EditarEjercicio.class);
                                iVerEditar.putExtra("nombreEjercicio", ejercicio.getNombre());
                                iVerEditar.putExtra("numSeries", String.valueOf(ejercicio.getNumSeries()));
                                iVerEditar.putExtra("numRepes", String.valueOf(ejercicio.getNumRepes()));
                                iVerEditar.putExtra("peso", String.valueOf(ejercicio.getPeso()));
                                iVerEditar.putExtra("rutina", ejercicio.getRutina());
                                iVerEditar.putExtra("user", ejercicio.getUsuario());
                                startActivityForResult(iVerEditar, 10);
                                if (!isFABOpen) {
                                    showFABMenu();
                                } else {
                                    closeFABMenu();
                                }
                            }
                        });
                        /*Al clickar un rato en un tiem de la lista se podrá eliminar y saltará una alerta señalándonos lo que va a ocurrir*/

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                int position = i;
                                new AlertDialog.Builder(VerEditarRutina.this).setTitle("Eliminar Ejercicio").setMessage("¿Deseas eliminar el ejercicio?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(VerEditarRutina.this, "Eliminose", Toast.LENGTH_SHORT).show();
                                        Ejercicio ejercicio = listaEjercicios.get(position);
                                        System.out.println(listaEjercicios);
                                        /*Se elimina de la BBDD*/
                                        Data datos = new Data.Builder().putString("user", ejercicio.getUsuario()).putString("rutina", ejercicio.getRutina()).putString("nombre", ejercicio.getNombre()).build();
                                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpEliminarEjercicio.class).setInputData(datos).build();
                                        WorkManager.getInstance(VerEditarRutina.this).getWorkInfoByIdLiveData(otwr.getId()).observe(VerEditarRutina.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                                    System.out.println(resultadoPhp);
                                                    if (resultadoPhp) {
                                                        listaEjercicios.remove(position);
                                                        arrayAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                        WorkManager.getInstance(VerEditarRutina.this).enqueue(otwr);
                                        //gestorBD.eliminarEjercicio(listaEjercicios.get(position), rutina);

                                    }
                                }).setNegativeButton("No", null).show();
                                return true;
                            }
                        });
                    }


                }
            }
        });


        WorkManager.getInstance(VerEditarRutina.this).enqueue(otwr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {//Se ha añadido un nuevo ejercicio correctamente
                //this.listaEjercicios = new ArrayList<>();

                //listaEjercicios = gestorBD.getEjercicios(rutina);
                String user = data.getStringExtra("user");
                String rutina = data.getStringExtra("rutina");
                getEjercicios(user, rutina);
                System.out.println("SE HA AÑADIDO UN NUEVO EJERCICIO CORRECTAMENTE->" + listaEjercicios);
                ArrayList<String> listaNombresEjercicios = new ArrayList<>();
                for (Ejercicio i : listaEjercicios) {
                    listaNombresEjercicios.add(i.getNombre());
                }
                System.out.println(listaNombresEjercicios);
                arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaNombresEjercicios) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View vista = super.getView(position, convertView, parent);
                        TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                        TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                        Ejercicio ejercicio = listaEjercicios.get(position);
                        String nombreEjercicio = ejercicio.getNombre();
                        lineaPrincipal.setText(nombreEjercicio);
                        //ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina);
                        String info = ejercicio.getNumSeries() + "x" + ejercicio.getNumRepes() + " con " + ejercicio.getPeso() + "Kg";
                        lineaSecundaria.setText(info);
                        return vista;
                    }
                };
                listView.setAdapter(arrayAdapter);
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        }
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) { //Se ha editado un ejercicio correctamente
                this.listaEjercicios = new ArrayList<>();
                String user = data.getStringExtra("user");
                String rutina = data.getStringExtra("rutina");
                getEjercicios(user, rutina);
                ArrayList<String> listaNombresEjercicios = new ArrayList<>();
                for (Ejercicio i : listaEjercicios) {
                    listaNombresEjercicios.add(i.getNombre());
                }
                System.out.println(listaNombresEjercicios);
                arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaNombresEjercicios) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View vista = super.getView(position, convertView, parent);
                        TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                        TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                        Ejercicio ejercicio = listaEjercicios.get(position);
                        String nombreEjercicio = ejercicio.getNombre();
                        lineaPrincipal.setText(nombreEjercicio);
                        //ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina);
                        String info = ejercicio.getNumSeries() + "x" + ejercicio.getNumRepes() + " con " + ejercicio.getPeso() + "Kg";
                        lineaSecundaria.setText(info);
                        return vista;
                    }
                };
                listView.setAdapter(arrayAdapter);
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        }
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

    public Ejercicio getEjercicio(String rutina, String ejercicio) {

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
        Ejercicio nuevoEjercicio = new Ejercicio(nombre, rutina, usuario, numRepes, numSeries, peso);

        return nuevoEjercicio;
    }

    public void agregarEjercicio(Ejercicio ejercicio) {
        /*Se inicializa un HashMap con los valores Rutina - Ejericicios; siendo este una lista del tipo Ejercicio*/
        System.out.println("-----------------------------INICIALIZAR EJERCICIOS-----------------------------");
        System.out.println(ejercicio);
        /*Tratamiento de strings para crear instancias del tipo Ejercicio*/

        if (!tieneEjercicio(ejercicio)) {
            this.listaEjercicios.add(ejercicio);
            cont++;
        }

        System.out.println("final-->" + listaEjercicios);
        System.out.println("cont-->" + cont);

    }

    public boolean tieneEjercicio(Ejercicio ejercicio) {
        boolean tiene = false;
        for (Ejercicio i : listaEjercicios) {
            if (i.getNombre().equals(ejercicio.getNombre()) && i.getRutina().equals(ejercicio.getRutina()) && i.getUsuario().equals(ejercicio.getUsuario()) && i.getPeso() == ejercicio.getPeso() && i.getNumRepes() == ejercicio.getNumRepes() && i.getNumSeries() == ejercicio.getNumSeries()) {
                tiene = true;
            }
        }
        return tiene;
    }

    public void getEjercicios(String user, String rutina) {
        Data datos = new Data.Builder().putString("user", user).putString("rutina", rutina).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectEjercicios.class).setInputData(datos).build();
        WorkManager.getInstance(VerEditarRutina.this).getWorkInfoByIdLiveData(otwr.getId()).observe(VerEditarRutina.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                    System.out.println(resultadoPhp);
                    if (resultadoPhp) {
                        String[] ejerciciosArray = workInfo.getOutputData().getStringArray("ejercicios");
                        for (int i = 0; i < ejerciciosArray.length; i++) {
                            if (!ejerciciosArray[i].equals("false")) {
                                Ejercicio nuevoEjercicio = getEjercicio(rutina, ejerciciosArray[i]);
                                System.out.println("nuevoEjercicio-> " + nuevoEjercicio.getNombre());
                                if (!listaEjercicios.contains(nuevoEjercicio)) {
                                    agregarEjercicio(nuevoEjercicio);
                                }
                            }
                        }
                    }
                }
            }
        });
        WorkManager.getInstance(VerEditarRutina.this).enqueue(otwr);
    }


}