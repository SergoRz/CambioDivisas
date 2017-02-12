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

    public String getMoneda1() {
        return moneda1;
    }

    public double getValor() {
        return valor;
    }

    public String getMoneda2() {
        return moneda2;
    }

    public void setMoneda1(String moneda1) {
        this.moneda1 = moneda1;
    }

    public void setMoneda2(String moneda2) {
        this.moneda2 = moneda2;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
