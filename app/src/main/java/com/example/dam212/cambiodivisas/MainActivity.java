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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.HttpURLConnection;

/*
Monedas:
- Dolar Estadounidense
- Euro
- Libra esterlina
- Yen Japones
- Peso Mexicano*/

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends Activity {

    private final String NAMESPACE = "http://www.webserviceX.NET/";
    private final String URL = "http://www.webservicex.net/CurrencyConvertor.asmx";
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
    private Button btnConvertir;
    private ArrayAdapter spinner_adapter;
    private AsynConversiones task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edCantidad = (EditText) findViewById(R.id.edCantidad);
        spMoneda1 = (Spinner) findViewById(R.id.spMoneda1);
        spMoneda2 = (Spinner) findViewById(R.id.spMoneda2);
        btnConvertir = (Button) findViewById(R.id.btnConvertir);
        tvSolucion = (TextView) findViewById(R.id.tvSolucion);

        spinner_adapter = ArrayAdapter.createFromResource( this, R.array.monedas , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoneda1.setAdapter(spinner_adapter);
        spMoneda2.setAdapter(spinner_adapter);
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
            tvSolucion.setText(String.valueOf(Double.parseDouble(respuesta) * Double.parseDouble(edCantidad.getText().toString())));
            Log.i(TAG, respuesta);
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


    public void getConversionREST(String moneda1, String moneda2){
        HttpURLConnection httpConnection =
        HttpClient httpClient = new DefaultHttpClient();

        String id = txtId.getText().toString();

        HttpGet del = new HttpGet("http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?FromCurrency=EUR&ToCurrency=EUR");

        del.setHeader("content-type", "application/json");

        try
        {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());

            JSONObject respJSON = new JSONObject(respStr);

            int idCli = respJSON.getInt("Id");
            String nombCli = respJSON.getString("Nombre");
            int telefCli = respJSON.getInt("Telefono");

            lblResultado.setText("" + idCli + "-" + nombCli + "-" + telefCli);
        }
        catch(Exception ex)
        {
            Log.e("ServicioRest","Error!", ex);
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

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void convertirDivisa(View v){

        moneda1 = spMoneda1.getSelectedItem().toString();
        Log.i(TAG, moneda1);
        moneda2 = spMoneda2.getSelectedItem().toString();
        Log.i(TAG, moneda2);
        task = new AsynConversiones();
        task.execute();
        /*
        if(networkHabilitada()){
            if(accesoInternet()){

            }
            else{
                Toast.makeText(getApplicationContext(), "No tienes conexión a Internet", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "No tienes Internet habilitado", Toast.LENGTH_LONG).show();
        }*/
    }
}
