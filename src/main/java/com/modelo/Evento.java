package com.modelo;

import java.util.ArrayList;
import java.util.List;

public class Evento {

    private String nombre;
    private String fecha;
    private double precioBase;

    private List<Entrada> entradasVendidas;
    

    public Evento(String nombre, String fecha, double precioBase) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.precioBase = precioBase;
        this.entradasVendidas = new ArrayList<>();
    }

    // ======================
    // GETTERS Y SETTERS
    // ======================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    // ======================
    // REGISTRAR VENTA
    // ======================

    public void agregarEntrada(Entrada entrada) {
        entradasVendidas.add(entrada);
    }

    public List<Entrada> getEntradasVendidas() {
        return entradasVendidas;
    }

    // ======================
    // REPORTE
    // ======================

    public double calcularRecaudacion() {
        double total = 0;

        for (Entrada e : entradasVendidas) {
            total += e.calcularPrecio(); // ← usamos TU método real
        }

        return total;
    }
}
