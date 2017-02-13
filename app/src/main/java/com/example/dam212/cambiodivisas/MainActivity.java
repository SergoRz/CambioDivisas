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
 *
 * @author SergioSR y EmilioCB
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edCantidad = (EditText) findViewById(R.id.edCantidad);
        spMoneda1 = (Spinner) findViewById(R.id.spMoneda1);
        spMoneda2 = (Spinner) findViewById(R.id.spMoneda2);
        tvSolucion = (TextView) findViewById(R.id.tvSolucion);

        spinner_adapter = ArrayAdapter.createFromResource( this, R.array.monedas , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoneda1.setAdapter(spinner_adapter);
        spMoneda2.setAdapter(spinner_adapter);

        abd = new AccesoBD(this);
        db = abd.getWritableDatabase();
    }

    private class AsynConversionSOAP extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getConversionSOAP(moneda1, moneda2);
            return null;
        }

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

        public void getConversionSOAP(String moneda1, String moneda2) {
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

    /**
     *
     */
    private class AsynConversionREST extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getConversionREST(moneda1, moneda2);
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

        public void getConversionREST(String moneda1, String moneda2) {
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

    /**
     * Método que se encargar de comprobar si hay conexion a internet o no, utilizando las clases
     * ConnectivityManager y NetworkInfo.
     * @return booleano que indica si hay conexion o no.
     */
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

    /**
     * Metodo que se ejecuta al pulsar el boton ConvertirREST, obtiene los datos de los spinner que se corresponde
     * con la moneda de origen (moneda1) y la moneda a la que se quiere convertir (moneda2), después comprueba si
     * hay conexion a internet, en caso afirmativo ejecuta el Asyntask AsynConversionREST para llamar al servicio
     * web de conversion de monedas mediante tecnologia REST, en caso de no haber conexion a internet,
     * ejecuta la conversion de moneda leyendo los datos de la BD.
     * @param v View que se corresponde con el botón que se ha pulsado
     */
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
