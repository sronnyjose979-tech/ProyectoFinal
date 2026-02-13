/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.modelo;

/**
 *
 * @author sronn
 */
public class EntradaGeneral extends Entrada {

    public EntradaGeneral(String nombreCliente, int fila, int columna, Evento evento) {
        super(nombreCliente, fila, columna, evento);
    }

    /*  public EntradaGeneral(String nombreCliente, Evento evento, int fila, int columna) {
        super(nombreCliente, evento, fila, columna);
    }
     */
    @Override
    public String tipoEntrada() {

        return "GENERAL";
    }

    @Override
    public String generarTicket() {
        return super.generarTicket(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public double calcularPrecio() {
        return this.evento.getPrecioBase(); //Solo taje el precio
    }

        @Override
        public String getDescripcionVenta
        
            () {
        return super.getDescripcionVenta(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

    }
