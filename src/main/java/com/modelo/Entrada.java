package com.modelo;

public abstract class Entrada implements IVendible {

    protected String nombreCliente;
    protected Evento evento;
    protected double precioFinalCalculado;
    protected int cantidadAsientos;
    protected String detalleAsientos;
    protected String fechaCompra;
    protected int idEntrada;
    private static int contadorId = 1;

    public Entrada(String nombreCliente, Evento evento, double precioFinal, int cantidad, String detalleAsientos) {
        this.idEntrada = contadorId++;
        this.nombreCliente = nombreCliente;
        this.evento = evento;
        this.precioFinalCalculado = precioFinal;
        this.cantidadAsientos = cantidad;
        this.detalleAsientos = detalleAsientos;
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.fechaCompra = ahora.format(formatter);
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
        sb.append("============================================\n");
        sb.append("        AUDITORIO - TICKET DE ENTRADA       \n");
        sb.append("============================================\n");
        sb.append("  ID Entrada   : ").append(this.idEntrada).append("\n");
        sb.append("  Cliente      : ").append(this.nombreCliente).append("\n");
        sb.append("  Evento       : ").append(this.evento.getNombre()).append("\n");
        sb.append("  Fecha Evento : ").append(this.evento.getFecha()).append("\n");
        sb.append("  Tipo Entrada : ").append(tipoEntrada()).append("\n");
        sb.append("  Asiento      : ").append(this.detalleAsientos.replace("\n", ", ")).append("\n");
        sb.append("  Precio Final : $").append(String.format("%.2f", precioFinalCalculado)).append("\n");
        if (!acceso().isEmpty()) {
        sb.append("  Beneficios   : ").append(acceso()).append("\n");
        }
        sb.append("  Compra       : ").append(this.fechaCompra).append("\n");
        sb.append("============================================\n");
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

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public void setPrecioFinalCalculado(double precioFinalCalculado) {
        this.precioFinalCalculado = precioFinalCalculado;
    }

    public String acceso() {
        return ""; 
    }
}
