/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.modelo;

/**
 *
 * @author sronn
 */
public class EntradaVip extends Entrada {

    public EntradaVip(String nombreCliente, int fila, int columna, Evento evento) {
        super(nombreCliente, fila, columna, evento);
    }

    @Override
    public String tipoEntrada() {
        return "VIP";
    }

    @Override
    public String generarTicket() {
        return super.generarTicket(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public double calcularPrecio() {
        return this.evento.getPrecioBase()* 1.50; 
    }

    @Override
    public String getDescripcionVenta() {
        return super.getDescripcionVenta(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

}
