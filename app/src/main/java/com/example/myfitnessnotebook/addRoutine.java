package com.example.myfitnessnotebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class addRoutine extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);
        Button add = (Button) findViewById(R.id.btn_addRoutine);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombreRutina = (EditText) findViewById(R.id.addRoutine_nombreRutina);
                Intent i = new Intent();
                i.putExtra("rutina", nombreRutina.getText().toString());
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

    }
}