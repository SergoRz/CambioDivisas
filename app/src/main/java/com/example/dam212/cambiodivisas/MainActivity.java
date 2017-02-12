package com.example.dam212.cambiodivisas;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/*Dolar Estadounidense
Euro
Libra esterlina
Yen Japones
Peso Mexicano*/

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends Activity {

    private final String NAMESPACE = "http://www.webserviceX.NET/";
    private final String URL = "http://www.webservicex.net/CurrencyConvertor.asmx";
    private final String SOAP_ACTION = "http://www.webserviceX.NET/ConversionRate";
    private final String METHOD_NAME = "ConversionRate";
    private String TAG = "Accion";
    private static String moneda1 = "EUR";
    private static String moneda2 = "USD";
    private static String respuesta;
    private TextView tvSolucion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edCantidad = (EditText) findViewById(R.id.edCantidad);
        Spinner spMoneda1 = (Spinner) findViewById(R.id.spMoneda1);
        Spinner spMoneda2 = (Spinner) findViewById(R.id.spMoneda2);
        Button btnConvertir = (Button) findViewById(R.id.btnConvertir);
        tvSolucion = (TextView) findViewById(R.id.tvSolucion);

        AsynConversiones task = new AsynConversiones();
        //Call execute
        task.execute();

    }

    private class AsynConversiones extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getConversion(moneda1, moneda2);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            tvSolucion.setText(respuesta);
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

        public void getConversion(String moneda1, String moneda2) {
            //Create request
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("FromCurrency", moneda1);
            request.addProperty("ToCurrency", moneda2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            //Set output SOAP object
            envelope.setOutputSoapObject(request);
            //Create HTTP call object
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

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

    public boolean networkHabilitada(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public Boolean accesoInternet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public double convertirDivisa(View v){
        double cantidad = 0;

        if(networkHabilitada()){
            if(accesoInternet()){

            }
            else{
                Toast.makeText(getApplicationContext(), "No tienes conexion a Internet", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "No tienes Internet habilitado", Toast.LENGTH_LONG).show();
        }

        return cantidad;
    }
}
