package com.example.dam212.cambiodivisas;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Clase principal de la aplicacion, se ejecuta al iniciar la aplicacion
 * Se encarga de cargar la interfaz de la pantalla, la cual esta descrita en el xml
 * "activity_main".
 * Contiene dos clases:
 * La clase AsynConversionSOAP se encarga de acceder al servicio web mediante la
 * tecnologia SOAP, recoger el valor de cambio y aplicarlo a la cantidad introducida.
 * La clase AsynConversionREST se encarga de acceder al servicio web mediante la
 * tecnologia REST, recoger el valor de cambio y aplicarlo a la cantidad introducida.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends Activity {
    private final String NAMESPACE = "http://www.webserviceX.NET/";
    private final String URLSOAP = "http://www.webservicex.net/CurrencyConvertor.asmx";
    private final String SOAP_ACTION = "http://www.webserviceX.NET/ConversionRate";
    private final String METHOD_NAME = "ConversionRate";
    private String TAG = "Accion";
    private static String moneda1;
    private static String moneda2;
    private static String respuesta;
    private TextView tvSolucion;
    private Spinner spMoneda1;
    private Spinner spMoneda2;
    private EditText edCantidad ;
    private ArrayAdapter spinner_adapter;
    private AsynConversionSOAP conversionSOAP;
    private AsynConversionREST conversionREST;
    private static SQLiteDatabase db;
    private AccesoBD abd;

    /**
     * Metodo que se ejecuta al iniciar la clase, se encarga de enlazar los atributos de la clase con los objetos
     * dispuesto en la interfaz grafica.
     * Ademas instancia la clase AccesoBD para poder acceder a la tabla de divisas y recoger el valor de cambio
     * cuando no haya conexion a Internet.
     * @param savedInstanceState Estado de la aplicacion
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edCantidad = (EditText) findViewById(R.id.edCantidad); //EditText para introducir la cantidad que se desea convertir
        spMoneda1 = (Spinner) findViewById(R.id.spMoneda1); //Spinner que contiene el tipo de moneda que se quiere convertir
        spMoneda2 = (Spinner) findViewById(R.id.spMoneda2); //Spinner que contiene el tipo de moneda a la que se quiere convertir
        tvSolucion = (TextView) findViewById(R.id.tvSolucion); //TextView donde se muestra el valor convertido

        //Adaptadores que introduce el array de monedas en los sppiners
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.monedas , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoneda1.setAdapter(spinner_adapter);
        spMoneda2.setAdapter(spinner_adapter);

        abd = new AccesoBD(this); //Instancia de la clase AccesoBD
        db = abd.getWritableDatabase(); //Base de datos de la clase AccesoBD
    }

    /**
     * Clase interna que se encarga de convertir la cantidad mediante la tecnologia SOAP.
     * La clase extiende de AsyncTask para que la ejecucion de la conversion se haga en segundo plano
     * y no haga esperar al hilo principal. Esto se consigue mediante el metodo doInBackground.
     */
    private class AsynConversionSOAP extends AsyncTask<String, Void, Void> {
        /**
         * Metodo que se ejecuta en segundo plano, se encarga de llamar al metodo getConversionSOAP, el cual se
         * encarga de realizar la conversion.
         * @param params Parametros que se le pueden pasar, en este caso no se pasa ninguno
         * @return Devuelve null
         */
        @Override
        protected Void doInBackground(String... params) {
            getConversionSOAP(); //Metodo que obtiene la cantidad convertida
            return null;
        }

        /**
         * Metodo que se ejecuta una vez finalizado el metodo doInBackground
         * Se encarga de multiplicar la cantidad deseada por el valor de cambio recibido del servicio web
         * y mostrarlo en el TextView de la solucion.
         * @param result
         */
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            tvSolucion.setText(String.valueOf(Double.parseDouble(respuesta) * Double.parseDouble(edCantidad.getText().toString())));
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

        public void getConversionSOAP() {
            //Create request
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("FromCurrency", moneda1);
            request.addProperty("ToCurrency", moneda2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            //Set output SOAP object
            envelope.setOutputSoapObject(request);
            //Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URLSOAP);

            try {
                //Invole web service
                androidHttpTransport.call(SOAP_ACTION, envelope);
                //Get the response
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                //Assign it to fahren static variable
                respuesta = response.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AsynConversionREST extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getConversionREST();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            tvSolucion.setText(String.valueOf(Double.parseDouble(respuesta) * Double.parseDouble(edCantidad.getText().toString())));
            //Log.i(TAG, respuesta);
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //tv.setText("Calculating...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

        public void getConversionREST() {
            try{
                URL url = new URL("http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?FromCurrency=" + moneda1 + "&ToCurrency=" + moneda2);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Error : codigo de error HTTP  : " + conn.getResponseCode());
                }

                XmlPullParserFactory xpf =  XmlPullParserFactory.newInstance();
                xpf.setNamespaceAware(true);

                XmlPullParser xp = xpf.newPullParser();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                xp.setInput(br);
                xp.nextTag();
                xp.next();

                respuesta = xp.getText();

                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean networkHabilitada(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public void convertirDivisaSOAP(View v){
        if(!edCantidad.getText().toString().equals("")){
            moneda1 = spMoneda1.getSelectedItem().toString();
            moneda2 = spMoneda2.getSelectedItem().toString();

            if(networkHabilitada()){
                conversionSOAP = new AsynConversionSOAP();
                conversionSOAP.execute();
            }
            else{
                Toast.makeText(getApplicationContext(), "No hay conexion a internet, valor obtenido de la base de datos", Toast.LENGTH_LONG).show();
                ConversionMoneda cm = new ConversionMoneda(moneda1, moneda2);
                double valor = abd.obtenerValor(db, cm);
                double resultado = valor*Double.parseDouble(edCantidad.getText().toString());
                tvSolucion.setText(String.valueOf(resultado));
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "No se ha introducido cantidad a convertir", Toast.LENGTH_LONG).show();
        }
    }

    public void convertirDivisaREST(View v){
        if(!edCantidad.getText().toString().equals("")) {
            moneda1 = spMoneda1.getSelectedItem().toString();
            moneda2 = spMoneda2.getSelectedItem().toString();

            if (networkHabilitada()) {
                conversionREST = new AsynConversionREST();
                conversionREST.execute();
            } else {
                Toast.makeText(getApplicationContext(), "No hay conexion a internet, valor obtenido de la base de datos", Toast.LENGTH_LONG).show();
                ConversionMoneda cm = new ConversionMoneda(moneda1, moneda2);
                double valor = abd.obtenerValor(db, cm);
                double resultado = valor * Double.parseDouble(edCantidad.getText().toString());
                tvSolucion.setText(String.valueOf(resultado));
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "No se ha introducido cantidad a convertir", Toast.LENGTH_LONG).show();
        }
    }
}
