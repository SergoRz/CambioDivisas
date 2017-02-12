package com.example.dam212.cambiodivisas;

import android.content.Context;
import android.database.Cursor;
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
            "moneda1 VARCHAR(3) PRIMARY KEY NOT NULL," +
            "moneda2 VARCHAR(3) PRIMARY KEY NOT NULL," +
            "valor DOUBLE NOT NULL" +
            ");";

    public AccesoBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(SQLiteDatabase db, ConversionMoneda cm){
        String sqlInsert = "INSERT INTO divisas VALUES("
                + cm.getMoneda1() + ","
                + cm.getMoneda2() + ","
                + cm.getValor() +
                "')";

        db.execSQL(sqlInsert);//Se ejecuta la sentencia creada anteriormente
    }

    public double obtenerValor(SQLiteDatabase db, ConversionMoneda cm){
        String[] args = new String[] {cm.getMoneda1(), cm.getMoneda2()};
        Cursor c = db.rawQuery(" SELECT valor FROM divisas WHERE moneda1=? and moneda2=?", args);
        double valor = c.getDouble(0);

        return valor;
    }
}
