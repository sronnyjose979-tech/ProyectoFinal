package com.modelo;

public abstract class Entrada implements IVendible {

    protected String nombreCliente;
    protected Evento evento;
    protected double precioFinalCalculado;
    protected int cantidadAsientos;
    protected String detalleAsientos;
    protected int idEntrada;
    private static int contadorId = 1;

    public Entrada(String nombreCliente, Evento evento, double precioFinal, int cantidad, String detalleAsientos) {
        this.idEntrada = contadorId++;
        this.nombreCliente = nombreCliente;
        this.evento = evento;
        this.precioFinalCalculado = precioFinal;
        this.cantidadAsientos = cantidad;
        this.detalleAsientos = detalleAsientos;
    }

    public static void setContadorId(int nuevo) {
        contadorId = nuevo;
    }

    public void setIdEntrada(int id) {
        this.idEntrada = id;
    }

    public abstract String tipoEntrada();

    @Override
    public String generarTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("        AUDITORIO - TICKET DE COMPRA       \n");
        sb.append("========================================\n");
        sb.append(" ID Entrada   : ").append(this.idEntrada).append("\n");
        sb.append(" Cliente      : ").append(this.nombreCliente).append("\n");
        sb.append(" Evento       : ").append(this.evento.getNombre()).append("\n");
        sb.append(" Tipo Entrada : ").append(tipoEntrada()).append("\n");
        sb.append(" Cantidad     : ").append(this.cantidadAsientos).append(" asiento(s)\n");
        sb.append("----------------------------------------\n");
        sb.append(this.detalleAsientos).append("\n");
        sb.append(" TOTAL PAGADO : $").append(String.format("%.2f", precioFinalCalculado)).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    @Override
    public double calcularPrecio() {
        return this.precioFinalCalculado;
    }

    @Override
    public String getDescripcionVenta() {
        return "Entrada " + tipoEntrada() + " - " + nombreCliente;
    }

    public int getIdEntrada() {
        return idEntrada;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public Evento getEvento() {
        return evento;
    }

    public int getCantidadAsientos() {
        return cantidadAsientos;
    }

    public String getDetalleAsientos() {
        return detalleAsientos;
    }

    public void setPrecioFinalCalculado(double precioFinalCalculado) {
        this.precioFinalCalculado = precioFinalCalculado;
    }
}
