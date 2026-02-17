package com.modelo;

public class EntradaGeneral extends Entrada {

    public EntradaGeneral(String nombreCliente, Evento evento, double precioFinal, int cantidad, String detalleAsientos) {
        // Enviamos los 5 parámetros a la clase madre Entrada
        super(nombreCliente, evento, precioFinal, cantidad, detalleAsientos);
    }

    @Override
    public String tipoEntrada() {
        return "GENERAL";
    }

    @Override
    public String generarTicket() {
        return super.generarTicket(); 
    }

    @Override
    public double calcularPrecio() {
        return this.precioFinalCalculado; 
    }

    @Override
    public String getDescripcionVenta() {
        return super.getDescripcionVenta();
    }
}