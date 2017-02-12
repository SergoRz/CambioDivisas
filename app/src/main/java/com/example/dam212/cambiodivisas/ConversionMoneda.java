package com.example.dam212.cambiodivisas;

/**
 * Created by EmilioCB on 12/02/2017.
 */

public class ConversionMoneda {

    private double moneda1, moneda2, valor;

    public ConversionMoneda(double moneda1, double moneda2, double valor){
        this.moneda1 = moneda1;
        this.moneda2 = moneda2;
        this.valor = valor;
    }

    public double getMoneda1() {
        return moneda1;
    }

    public double getValor() {
        return valor;
    }

    public double getMoneda2() {
        return moneda2;
    }

    public void setMoneda1(double moneda1) {
        this.moneda1 = moneda1;
    }

    public void setMoneda2(double moneda2) {
        this.moneda2 = moneda2;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
