package com.example.dam212.cambiodivisas;

/**
 * Created by EmilioCB on 12/02/2017.
 */

public class ConversionMoneda {

    private double valor;
    private String moneda1, moneda2;

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
