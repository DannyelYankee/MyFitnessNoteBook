package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditarEjercicio extends AppCompatActivity {

    EditText editNombre, editSeries, editRepes, editPeso;
    Button btnEdit;
    miBD gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ejercicio);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);

        editNombre = (EditText) findViewById(R.id.EditNombreEjercicio);
        editSeries = (EditText) findViewById(R.id.EditSeries);
        editRepes = (EditText) findViewById(R.id.EditRepeticiones);
        editPeso = (EditText) findViewById(R.id.EditPeso);

        /*Recogemos los datos actuales del ejercicio*/
        String nombreExtra = getIntent().getStringExtra("nombreEjercicio");
        String seriesExtra = getIntent().getStringExtra("numSeries");
        String repesExtra = getIntent().getStringExtra("numRepes");
        String pesoExtra = getIntent().getStringExtra("peso");
        String rutinaExtra = getIntent().getStringExtra("rutina");

        editNombre.setHint(nombreExtra);
        editSeries.setHint(Integer.parseInt(seriesExtra));
        editRepes.setHint(Integer.parseInt(repesExtra));
        editPeso.setHint(Integer.parseInt(pesoExtra));

        String nombreEditado = editNombre.getText().toString();
        String seriesEditado = editSeries.getText().toString();
        String repesEditado = editRepes.getText().toString();
        String pesoEditado = editPeso.getText().toString();

        btnEdit = (Button) findViewById(R.id.btn_EditEjer);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            /*Botón para actualizar la BBDD con los nuevos datos del ejercicio*/
            @Override
            public void onClick(View view) {
                int series = Integer.parseInt(seriesEditado);
                int repes = Integer.parseInt(repesEditado);
                int peso = Integer.parseInt(pesoEditado);
                gestorBD.editarEjercicio(nombreEditado, series, repes, peso, rutinaExtra);
                Intent iBack = new Intent();
                setResult(RESULT_OK);
                finish();

            }
        });

    }
}