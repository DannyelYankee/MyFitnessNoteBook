package com.example.myfitnessnotebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class miBD extends SQLiteOpenHelper {
    //public static final int DATABASE_VERSION = 1;
    //public static final String DATABASE_NAME = "MyFitnessBook.db";
    int id = 0;
    public miBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*Tabla Users:
         * correo    contra*/
        //sqLiteDatabase.execSQL("CREATE TABLE Users ('correo' VARCHAR(255) PRIMARY KEY NOT NULL, 'contra' VARCHAR(255))");
        /*Tabla Rutina:
         * nombre */
        sqLiteDatabase.execSQL("CREATE TABLE Rutinas ('nombre' VARCHAR(255) PRIMARY KEY NOT NULL)");

        /*Tabla Ejercicio:
         * nombre    numSeries   numRepes    peso   */
        sqLiteDatabase.execSQL("CREATE TABLE Ejercicios ('id' INTEGER PRIMARY KEY NOT NULL, 'nombre' VARCHAR(255) NOT NULL, 'numSeries' INTEGER, 'numRepes' INTEGER, 'peso' INTEGER, 'rutina' VARCHAR(255))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<String> getRutinas() {
        ArrayList<String> rutinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Rutinas";
        Cursor c = db.rawQuery(query, null);
        while (c.moveToNext()) {
            int i = c.getColumnIndex("nombre");
            String nombre = c.getString(i);
            rutinas.add(nombre);
        }
        c.close();
        return rutinas;
    }

    public boolean agregarRutina(String nombre) {
        boolean agregado = false;
        ArrayList<String> rutinas = this.getRutinas();
        if (!rutinas.contains(nombre)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            long newRowId = db.insert("Rutinas", null, values);
            db.close();
            agregado = true;
        }
        return agregado;
    }

    public ArrayList<String> getEjercicios(String rutina) {
        ArrayList<String> ejercicios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Ejercicios WHERE rutina =" + rutina;
        Cursor c = db.rawQuery(query, null);
        while (c.moveToNext()) {
            int i = c.getColumnIndex("nombre");
            String nombre = c.getString(i);
            ejercicios.add(nombre);
        }
        c.close();
        return ejercicios;
    }

    public void agregarEjercicio(String nombreEjer, int series, int repeticiones, int peso, String nombreRutina) {
        SQLiteDatabase db = this.getWritableDatabase();

        /*
        Log.i("agregarEjercicio",nombreEjer+", "+series+", "+repeticiones+", "+peso+", "+nombreRutina);
        String query = "INSERT INTO Ejercicios ('nombre', 'numSeries', 'numRepes', 'peso', 'rutina') VALUES('"+nombreEjer+"',"+series+","+repeticiones+","+peso+",'"+nombreRutina+"');");
        db.execSQL(query);
        */

        ContentValues values = new ContentValues();
        values.put("id", this.id);
        this.id++;
        values.put("nombre", "espalda");
        values.put("numSeries", 2);
        values.put("numRepes", 3);
        values.put("peso", 70);
        values.put("rutina", "torso");
        long newRowId = db.insert("Ejercicios", null, values);

        Log.i("agregarEjercicio", "agregado");
        db.close();
    }

}
