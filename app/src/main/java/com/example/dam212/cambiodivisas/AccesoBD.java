package com.example.dam212.cambiodivisas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EmilioCB on 12/02/2017.
 */

public class AccesoBD extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;//Contexto de acción para el helper.
    public static final String DATABASE_NAME = "divisasDB.db";
    //Nombre del archivo con extensión .db

    //Sentencia SQL que crea la tabla
    String sqlCreate = "CREATE TABLE divisas(" +
            "moneda1 DOUBLE PRIMARY KEY NOT NULL," +
            "moneda2 DOUBLE PRIMARY KEY NOT NULL," +
            "valor DOUBLE NOT NULL" +
            ");";

    public AccesoBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(){

    }
}
