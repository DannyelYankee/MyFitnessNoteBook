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
        sqLiteDatabase.execSQL("CREATE TABLE Usuario ('nombre' VARCHAR(255) PRIMARY KEY NOT NULL, 'logueado' VARCHAR(255))");

        /*Tabla Ejercicio:
         * nombre    numSeries   numRepes    peso   */
        sqLiteDatabase.execSQL("CREATE TABLE Ejercicios ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'nombre' VARCHAR(255) NOT NULL, 'numSeries' INTEGER, 'numRepes' INTEGER, 'peso' INTEGER, 'rutina' VARCHAR(255))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void loguearUsuario(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", user);
        values.put("logueado", "true");
        long newRowId = db.insert("Usuario", null, values);
        db.close();
    }

    public boolean estaLogueado(String user) {
        boolean esta = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Usuario WHERE nombre= ?";
        Cursor c = db.rawQuery(query, new String[]{user});
        String logueado = "";
        while (c.moveToNext()) {
            int i = c.getColumnIndex("logueado");
            logueado = c.getString(i);
        }
        if (logueado.equals("true")) {
            esta = true;
        }
        System.out.println(esta);
        c.close();
        db.close();
        return esta;
    }

    public ArrayList<String> getRutinas() {
        /*Devuelve los nombres de las rutinas*/
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
        //Agrega nueva rutina a la BBDD
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
        /*Devuelve una lista con los nombres de todos los ejercicios dada una rutina*/
        ArrayList<String> ejercicios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Ejercicios WHERE rutina= ?";
        Cursor c = db.rawQuery(query, new String[]{rutina});
        while (c.moveToNext()) {
            int i = c.getColumnIndex("nombre");
            String nombre = c.getString(i);
            ejercicios.add(nombre);
        }
        c.close();
        db.close();
        Log.i("ejercicios", "size: " + ejercicios.size());
        return ejercicios;
    }

    public ArrayList<Integer> getInfoEjercicio(String ejercicio, String rutina) {
        /*Dado el nombre de un ejercicio y el nombre de la rutina a la que pertenece:
         * devuelve una lista con los datos de dicho ejercicio: n?? series, n?? repeticiones y peso*/
        ArrayList<Integer> info = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Ejercicios WHERE nombre= ? AND rutina= ?";
        Cursor c = db.rawQuery(query, new String[]{ejercicio, rutina});
        while (c.moveToNext()) {
            int indexSeries = c.getColumnIndex("numSeries");
            int indexRepes = c.getColumnIndex("numRepes");
            int indexPeso = c.getColumnIndex("peso");
            info.add(c.getInt(indexSeries));
            info.add(c.getInt(indexRepes));
            info.add(c.getInt(indexPeso));
        }
        c.close();
        db.close();

        return info;
    }

    public void agregarEjercicio(String nombreEjer, int series, int repeticiones, int peso, String nombreRutina) {
        //Agrega un ejercicio nuevo a la BBDD
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombreEjer);
        values.put("numSeries", series);
        values.put("numRepes", repeticiones);
        values.put("peso", peso);
        values.put("rutina", nombreRutina);
        long newRowId = db.insert("Ejercicios", null, values);

        Log.i("agregarEjercicio", "agregado");
        db.close();
    }

    public void eliminarEjercicio(String nombreEjer, String rutina) {
        //Elimina un ejercicio en espec??fico perteneciente a una rutina en concreto
        int id = this.getID(nombreEjer, rutina);
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("id de " + nombreEjer + "--->" + id);
        db.delete("Ejercicios", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void eliminarRutina(String rutina) {
        //Elimina una rutina de la BBDD
        SQLiteDatabase db = this.getWritableDatabase();
        /*Primero eliminamos todos los ejercicios relacionados con la rutina*/
        db.delete("Ejercicios", "rutina=?", new String[]{rutina});
        db.delete("Rutinas", "nombre=?", new String[]{rutina});
        db.close();
    }

    public int getID(String ejercicio, String rutina) {
        //Dado el nombre de un ejercicio y el de la rutina a la que pertence, devuelve el id de dicha row de la BBDD
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Ejercicios WHERE nombre = ? AND rutina = ?";
        Cursor c = db.rawQuery(query, new String[]{ejercicio, rutina});
        int id = -1;
        while (c.moveToNext()) {
            int index = c.getColumnIndex("id");
            id = c.getInt(index);
        }
        return id;
    }

    public void editarEjercicio(String nombreOriginal, String nombreEjer, int series, int repeticiones, int peso, String nombreRutina) {
        //Actualiza en la BBDD los datos de un ejercicio
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int idEjercicio = this.getID(nombreOriginal, nombreRutina);
        values.put("numSeries", series);
        values.put("numRepes", repeticiones);
        values.put("peso", peso);
        db.update("Ejercicios", values, "id=?", new String[]{String.valueOf(idEjercicio)});
        if (nombreEjer != nombreOriginal) {//Si el usuario decide cambiar el nombre del ejercicio
            ContentValues values2 = new ContentValues();
            values2.put("nombre", nombreEjer);
            db.update("Ejercicios", values2, "id=?", new String[]{String.valueOf(idEjercicio)});
        }
        db.close();
        System.out.println("Update hecho");
    }
}
