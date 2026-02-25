package com.modelo;

public class EntradaVip extends Entrada {

    public EntradaVip(String nombreCliente, Evento evento, double precioFinal, int cantidad, String detalleAsientos) {
        super(nombreCliente, evento, precioFinal, cantidad, detalleAsientos);
    }

    @Override
    public String tipoEntrada() {
        return "VIP";
    }

    public String acceso() {
        return "El cliente tiene acceso a lounge";
    }

    @Override
    public String generarTicket() {
        return super.generarTicket();
    }

    @Override
    public double calcularPrecio() {
        return this.evento.getPrecioBase() * this.cantidadAsientos * 1.50;
    }

    @Override
    public String getDescripcionVenta() {
        return super.getDescripcionVenta();
    }
}
