package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class addEjercicio extends AppCompatActivity {
    TextView nombreRutina;
    TextView numEjer;
    EditText nombreEjer, series, repeticiones, peso;
    Button btn_addEjer;
    miBD gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ejercicio);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);


        nombreRutina = (TextView) findViewById(R.id.nombreRutina);
        numEjer = (TextView) findViewById(R.id.currentNumEjer);
        btn_addEjer = (Button) findViewById(R.id.btn_addEjer);
        nombreEjer = (EditText) findViewById(R.id.nombreEjercicio);
        series = (EditText) findViewById(R.id.series);
        repeticiones = (EditText) findViewById(R.id.repeticiones);
        peso = (EditText) findViewById(R.id.peso);


        String nombreRutinaExtra = getIntent().getStringExtra("nombreRutina");
        nombreRutina.setText(nombreRutinaExtra);
        String numEjerExtra = getIntent().getStringExtra("numEjer");
        ArrayList<String> numEjercicios = gestorBD.getEjercicios(nombreRutinaExtra);
        numEjer.setText(String.valueOf(numEjercicios.size()));
        btn_addEjer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Nos aseguramos de que no haya ningún campo vacío*/
                if (nombreEjer.getText().toString().equals("") || series.getText().toString().equals("") || repeticiones.getText().toString().equals("") || peso.getText().toString().equals("")) {
                    Toast.makeText(addEjercicio.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                } else {

                    int numSeries = Integer.parseInt(series.getText().toString());
                    String nombreEjercicio = nombreEjer.getText().toString();
                    int repes = Integer.parseInt(repeticiones.getText().toString());
                    int pesoKG = Integer.parseInt(peso.getText().toString());
                    String rutina = getIntent().getStringExtra("nombreRutina");
                    String usuario = getIntent().getStringExtra("usuario");

                    /*Se agrega el ejercicio a la BBDD*/
                    //gestorBD.agregarEjercicio(nombreEjercicio, numSeries, repes, pesoKG, rutina);

                    Data datos = new Data.Builder().putString("user", usuario).putInt("numSeries",numSeries).putInt("numRepes",repes).putInt("peso",pesoKG).putString("nombre",nombreEjercicio).putString("rutina",rutina).build();
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpInsertEjercicio.class).setInputData(datos).build();
                    WorkManager.getInstance(addEjercicio.this).getWorkInfoByIdLiveData(otwr.getId()).observe(addEjercicio.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                System.out.println(resultadoPhp);
                                if (resultadoPhp) {
                                    Intent iBack = new Intent();
                                    iBack.putExtra("rutina", nombreRutinaExtra);
                                    setResult(Activity.RESULT_OK, iBack);
                                    finish();
                                }
                            }

                        }
                    });
                    WorkManager.getInstance(addEjercicio.this).enqueue(otwr);

                }
            }
        });

    }

}