package com.example.dam212.cambiodivisas;

/**
 * Clase que guarda la informacion de una conversion, los dos tipos de moneda convertidos y el valor.
 * Created by EmilioCB on 12/02/2017.
 */

public class ConversionMoneda {

    private double valor; //Valor de cambio
    private String moneda1, moneda2; //Monedas que se quieren convertir

    /**
     * Constructor que contiene todos los atributos de la clase.
     * @param moneda1 Moneda que se quiere convertir
     * @param moneda2 Moneda a la que se quiere convertir
     * @param valor Valor de cambio
     */
    public ConversionMoneda(String moneda1, String moneda2, double valor){
        this.moneda1 = moneda1;
        this.moneda2 = moneda2;
        this.valor = valor;
    }

    /**
     * Constructor que contiene los dos tipos de moneda String, String
     * @param moneda1 moneda de la que se va a hacer la conversion
     * @param moneda2 moneda a la que se va a hacer la conversion
     */
    public ConversionMoneda(String moneda1, String moneda2){
        this.moneda1 = moneda1;
        this.moneda2 = moneda2;
    }

    //Getters
    public String getMoneda1() {
        return moneda1;
    }

    public double getValor() {
        return valor;
    }

    public String getMoneda2() {
        return moneda2;
    }
}
