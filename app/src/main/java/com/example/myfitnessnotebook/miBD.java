package com.example.myfitnessnotebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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
        sqLiteDatabase.execSQL("CREATE TABLE Users ('correo' VARCHAR(255) PRIMARY KEY NOT NULL, 'contra' VARCHAR(255))");
        /*Tabla Rutina:
         * nombre */
        sqLiteDatabase.execSQL("CREATE TABLE Rutinas ('nombre' VARCHAR(255) PRIMARY KEY NOT NULL)");

        /*Tabla Ejercicio:
         * nombre    numSeries   numRepes    peso   */
        sqLiteDatabase.execSQL("CREATE TABLE Ejercicios('id'INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'nombre' VARCHAR(255) NOT NULL, 'numSeries' INTEGER, 'numRepes' INTEGER, 'peso' INTEGER, 'rutina' VARCHAR(255) )");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
