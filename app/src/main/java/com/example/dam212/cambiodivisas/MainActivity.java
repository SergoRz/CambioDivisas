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
         * @param result Resultado de la operacion doInBackground
         */
        @Override
        protected void onPostExecute(Void result) {
            tvSolucion.setText(String.valueOf(Double.parseDouble(respuesta) *
                    Double.parseDouble(edCantidad.getText().toString())));
        }

        /**
         * Metodo que se ejecuta antes del metodo doInBackground
         * En este caso no realiza ninguna accion
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * Meotodo que se ejecuta durante el doInBackground
         * En este caso no realiza ninguna accion
         * @param values Valores que indican el progreso
         */
        @Override
        protected void onProgressUpdate(Void... values) {
        }

        /**
         * Metodo que se encarga de acceder al servicio web mediante un SoapObject,
         * a este objeto de le pasan los parametros. A continuacion se crea un contenedor, el cual contiene
         * el SoapObject, a continuacion se crea un httpTransportSE, para acceder al servicio web e indicarle
         * la pagina web y los parametros. Una vez realizada la peticion, esta devuelve la respuesta y se guarda
         * en la variable respuesta.
         */
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

    /**
     * Clase AsynConversionREST
     * La clase AsynConversionREST se encarga de acceder al servicio web mediante la
     * tecnologia REST, recoger el valor de cambio y aplicarlo a la cantidad introducida.
     */
    private class AsynConversionREST extends AsyncTask<String, Void, Void> {
        /**
         * Método principal de la claseAsynConversionRest es un método sobreescrito de la clase
         * AsyncTask, este método se ejecutara cuando se llame al metodo execute() de esta misma clase.
         * @param params parametros que puede recibir el AsynTask
         * @return devuelve null
         */
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getConversionREST();
            return null;
        }

        /**
         * Metodo que se ejecuta despues de que el AsynTask finalice
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

        /**
         * Metodo que mediante la clase HttpURLConnection y URL se conecta a un servicio web soliciando la accion GET, establecida en el
         * metodo setRequestMethod, si el codigo que retorna esta conexion es 200 se notifica que ha habido un error de conexion y se
         * desconecta. En el caso de que la conexion retorne 200 significa que se ha podido establecer la conexion correctamente, en ese caso
         * se procede a leer el XML que devuelve el servicio web, para esto se utiliza las clases XmlPullParserFactory y la clase XmlPullParser,
         * mediante el input de esta ultima clase se obtendra el dato que queremos obtener, el cambio de divisas.
         */
        public void getConversionREST() {
            try{
                //Se establece la direccion del servicio web incluyendo los parametros
                URL url = new URL("http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?FromCurrency=" + moneda1 + "&ToCurrency=" + moneda2);
                //Se conecta a la url indicada anteriormente y se obtiene el objeto HttpURLConnection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //Se establece la accion que se desea ejecutar, en nuestro caso GET, para obtener el  xml que devuelve el servicio web
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                //Si la conexion no es correcta, se informa al usuario mediante un toast indicando el error
                if (conn.getResponseCode() != 200) {
                    Toast.makeText(getApplicationContext(), "Error : codigo de error HTTP  : " + conn.getResponseCode(), Toast.LENGTH_LONG).show();
                }//Si la conexion es correcta
                else{
                    //Se crea el XmlPullParserFactory que nos permitira crear el XmlPullParser
                    XmlPullParserFactory xpf =  XmlPullParserFactory.newInstance();
                    //Se indica que el que el parser que produzca este XmlPullParserFactory soporta un espacion de nombres de XML
                    xpf.setNamespaceAware(true);
                    //Se crea el XmlPullParser partiendo del XmlPullParserFactory
                    XmlPullParser xp = xpf.newPullParser();

                    //Se crea un BufferedReader con el inputstream de la conexion
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Se le pasa el BufferedReader al XmlPullParser para recorrer el XML mediante los metodos de la clase XmlPullParser
                    xp.setInput(br);
                    //Se posiciona en la etiqueta double
                    xp.nextTag();
                    //Se posiciona en el texto de esa etiqueta
                    xp.next();
                    //Se obtiene el valor de la etiqueta double
                    respuesta = xp.getText();
                }

                //Se desconecta
                conn.disconnect();

            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), "Error al conectarse al servidor", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Error al conectarse al servidor", Toast.LENGTH_LONG).show();
            } catch (XmlPullParserException e) {
                Toast.makeText(getApplicationContext(), "Error al conectarse al servidor", Toast.LENGTH_LONG).show();
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
