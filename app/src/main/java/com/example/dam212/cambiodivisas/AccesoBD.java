package com.example.dam212.cambiodivisas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase que se encarga de hacer los accesos a la base de datos cuando no hoy conexion a Internet.
 * Se utiliza la base de datos divisasDB.db de SQLite, en contrecto se trabaja con la tabla divisas.
 * Esta tabla contiene el valor de cambio entre las diferentes monedas.
 * Created by EmilioCB on 12/02/2017.
 */

public class AccesoBD extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1; //Contexto de acción para el helper.
    public static final String DATABASE_NAME = "divisasDB.db"; //Nombre de la base de datos

    //Sentencia SQL que crea la tabla divisas
    private String sqlCreate = "CREATE TABLE IF NOT EXISTS divisas(" +
            "moneda1 VARCHAR(3) NOT NULL," +
            "moneda2 VARCHAR(3) NOT NULL," +
            "valor DOUBLE NOT NULL," +
            " PRIMARY KEY(moneda1, moneda2));";

    /**
     * Constructor de la clase, contiene el contexto, el nombre y la version de la base de datos
     * @param context Contexto de la actividad principal
     */
    public AccesoBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Metodo que se ejecuta al instanciar un objeto de la clase AccesoBD
     * Se encarga de crear la tabla en la base de datos y de realizar los inserts correspondientes
     * @param db Base de datos que recibe
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate); //Se ejecuta el script de la construccion de la tabla
        try{
            introducirDivisasBD(db);
        }catch(SQLiteException e){}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    /**
     * Metodo que permite insertar un objeto ConversionMoneda en la BD
     * @param db Bd a la que se va a insertar el dato
     * @param cm objeto ConversionMoneda a insertar
     * @throws SQLiteException error al intentar hacer el insert en la BD
     */
    public void insert(SQLiteDatabase db, ConversionMoneda cm) throws SQLiteException{
        //String con la sentencia insert
        String sqlInsert = "INSERT INTO divisas VALUES('"
                + cm.getMoneda1() + "','"
                + cm.getMoneda2() + "',"
                + cm.getValor() +
                ")";
        db.execSQL(sqlInsert);//Se ejecuta la sentencia creada anteriormente
    }

    public double obtenerValor(SQLiteDatabase db, ConversionMoneda cm){
        double valor = 0;
        String[] campos = new String[] {"valor"};
        Cursor c = db.query("divisas", campos, "moneda1 ='" + cm.getMoneda1() + "' AND moneda2 ='" + cm.getMoneda2() + "'", null, null, null, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                valor = c.getDouble(0);
            } while(c.moveToNext());
        }
        return valor;
    }

    /**
     * Metodo que hace la carga inicial en la BD con los datos de las conversiones disponibles
     * @param db BD  en la que se van a insertar los datos
     */
    public void introducirDivisasBD(SQLiteDatabase db) throws SQLiteException{

        //Craecion de objetos ConversionMoneda
        ConversionMoneda cmEE = new ConversionMoneda("EUR", "EUR", 1);
        ConversionMoneda cmEU = new ConversionMoneda("EUR", "USD", 1.06);
        ConversionMoneda cmEG = new ConversionMoneda("EUR", "GBP", 0.85);
        ConversionMoneda cmEM = new ConversionMoneda("EUR", "MXN", 21.62);

        ConversionMoneda cmUU = new ConversionMoneda("USD", "USD", 1);
        ConversionMoneda cmUE = new ConversionMoneda("USD", "EUR", 0.94);
        ConversionMoneda cmUG = new ConversionMoneda("USD", "GBP", 0.80);
        ConversionMoneda cmUM = new ConversionMoneda("USD", "MXN", 20.33);

        ConversionMoneda cmGE = new ConversionMoneda("GBP","EUR",1.17);
        ConversionMoneda cmGU = new ConversionMoneda("GBP","USD",1.24);
        ConversionMoneda cmGG = new ConversionMoneda("GBP","GBP", 1);
        ConversionMoneda cmGM = new ConversionMoneda("GBP","MXN", 25.37);

        ConversionMoneda cmME = new ConversionMoneda("MXN","EUR", 0.0462);
        ConversionMoneda cmMU = new ConversionMoneda("MXN","USD", 0.049);
        ConversionMoneda cmMG = new ConversionMoneda("MXN","GBP",0.039);
        ConversionMoneda cmMM = new ConversionMoneda("MXN","MXN", 1);

        //Se ejecutan los insert
        insert(db, cmEE);
        insert(db, cmEU);
        insert(db, cmEG);
        insert(db, cmEM);

        insert(db, cmUU);
        insert(db, cmUE);
        insert(db, cmUG);
        insert(db, cmUM);

        insert(db, cmGE);
        insert(db, cmGU);
        insert(db, cmGG);
        insert(db, cmGM);

        insert(db, cmME);
        insert(db, cmMU);
        insert(db, cmMG);
        insert(db, cmMM);
    }
}
