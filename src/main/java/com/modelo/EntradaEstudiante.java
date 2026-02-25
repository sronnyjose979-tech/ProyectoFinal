package com.modelo;

public class EntradaEstudiante extends Entrada {

    public EntradaEstudiante(String nombreCliente, Evento evento, double precioFinal, int cantidad, String detalleAsientos) {
        super(nombreCliente, evento, precioFinal, cantidad, detalleAsientos);
    }

    @Override
    public String tipoEntrada() {
        return "ESTUDIANTIL";
    }

    @Override
    public String generarTicket() {
        return super.generarTicket();
    }

    @Override
    public double calcularPrecio() {
        return this.evento.getPrecioBase() * this.cantidadAsientos * 0.80;
    }

    @Override
    public String getDescripcionVenta() {
        return super.getDescripcionVenta();
    }
}
