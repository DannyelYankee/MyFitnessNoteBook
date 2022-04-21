package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

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
    TextView logueado;
    Boolean log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);
        Button add = (Button) findViewById(R.id.btn_addRoutine);
        logueado = (TextView) findViewById(R.id.logueadoAddRutina);
        String user = getIntent().getStringExtra("user");
        if (user != null) logueado.setText(user);
        System.out.println("LOGUEADO ADD RUTINA --> "+user);
        log = false;
        if(!logueado.getText().toString().equals("")){
            log = true;
        }
        System.out.println("INSERTART EJERCICIOS --> "+log);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombreRutinaText = (EditText) findViewById(R.id.addRoutine_nombreRutina);
                nombreRutina = nombreRutinaText.getText().toString();
                if (nombreRutina.trim().length() == 0) {
                    /*Nos aseguramos de que el campo no está vacío*/
                    Toast.makeText(addRoutine.this, "Por favor, ingrese un nombre para la rutina", Toast.LENGTH_SHORT).show();
                } else {
                    if (!log) {
                        //System.out.println("INSERTAR Ejercicios --> no logueado");
                        /*Si el usuario no está logueado se guardará en la BBDD remota*/
                        agregado = gestorBD.agregarRutina(nombreRutina);
                        if (agregado) {
                            //Si no existe ninguna rutina con este nombre
                            Intent i = new Intent();
                            i.putExtra("rutina", nombreRutina.toString());
                            setResult(Activity.RESULT_OK, i);
                            finish();
                        } else {
                            //Salta un aviso para cambiar el nombre de la rutina
                            Toast.makeText(addRoutine.this, "Ya existe una rutina con el nombre " + nombreRutina + ". Por favor pruebe con otro nombre.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        /*Si el usuario está logueado se guardará en el Servidor*/
                        //System.out.println("INSERTAR Ejercicios --> LOGUEADO");
                        Data datos = new Data.Builder().putString("user", user).putString("rutina", nombreRutina).build();
                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpInsertRutina.class).setInputData(datos).build();
                        WorkManager.getInstance(addRoutine.this).getWorkInfoByIdLiveData(otwr.getId()).observe(addRoutine.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {
                                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                    System.out.println(resultadoPhp);
                                    if (resultadoPhp) {
                                        Intent i = new Intent();
                                        i.putExtra("rutina", nombreRutina.toString());
                                        setResult(Activity.RESULT_OK, i);
                                        finish();
                                    } else {
                                        Toast.makeText(addRoutine.this, "Ya existe una rutina con el nombre " + nombreRutina + ". Por favor pruebe con otro nombre.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
                        WorkManager.getInstance(addRoutine.this).enqueue(otwr);
                    }
                }
            }
        });

    }


}