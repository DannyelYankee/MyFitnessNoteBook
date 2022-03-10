package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class addEjercicio extends AppCompatActivity {
    ListView listview;
    TextView nombreRutina;
    TextView numEjer;
    EditText nombreEjer, series, repeticiones, peso;
    Button btn_addEjer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ejercicio);
        nombreRutina = (TextView) findViewById(R.id.nombreRutina);
        numEjer = (TextView) findViewById(R.id.currentNumEjer);
        nombreEjer = (EditText) findViewById(R.id.nombreEjercicio);
        series = (EditText) findViewById(R.id.series);
        repeticiones = (EditText) findViewById(R.id.repeticiones);
        peso = (EditText) findViewById(R.id.peso);
        btn_addEjer = (Button) findViewById(R.id.btn_addEjer);

        String nombreRutinaExtra = getIntent().getStringExtra("nombreRutina");
        nombreRutina.setText(nombreRutinaExtra);
        String numEjerExtra = getIntent().getStringExtra("numEjer");
        numEjer.setText(numEjerExtra);

    }
}