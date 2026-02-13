/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.modelo;

import com.proyectou.VentasDeAsientosController;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author reich
 */
public abstract class Entrada implements IVendible {

    // Declaramos los atributos que el ticket va a imprimir
    protected String nombreCliente;
    protected int fila;
    protected int columna;
    protected Evento evento;

    public Entrada(String nombreCliente, int fila, int columna, Evento evento) {
        this.nombreCliente = nombreCliente;
        this.fila = fila;
        this.columna = columna;
        this.evento = evento;
    }

    public abstract String tipoEntrada();

    @Override
    public String generarTicket() {
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();

        sb.append("==========================================\n");
        sb.append("        AUDITORIO - TICKET DE ENTRADA       \n");
        sb.append("==========================================\n");

        // Citas a errores corregidos:
        sb.append(" Cliente      : ").append(this.nombreCliente).append("\n"); // Soluciona
        sb.append(" Evento       : ").append(this.evento.getNombre()).append("\n"); // Soluciona
        sb.append(" Fecha Evento : ").append(this.evento.getFecha()).append("\n");
        sb.append(" Asiento      : Fila ").append(fila + 1).append(" - Col ").append(columna + 1).append("\n");
        sb.append(" Precio Final : $").append(String.format("%.2f", calcularPrecio())).append("\n");
        sb.append("==========================================\n");

        return sb.toString();
    }

    @Override
    public abstract double calcularPrecio();

    @Override
    public String getDescripcionVenta() {
        return "Entrada " + tipoEntrada() + " - " + nombreCliente;
    }
}
