package com.modelo;

public class EntradaGeneral extends Entrada {

    public EntradaGeneral(String nombreCliente, Evento evento, double precioFinal, int cantidad, String detalleAsientos) {
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
        return this.evento.getPrecioBase() * this.cantidadAsientos;
    }

    @Override
    public String getDescripcionVenta() {
        return super.getDescripcionVenta();
    }
}
