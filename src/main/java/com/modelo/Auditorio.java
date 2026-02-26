package com.modelo;

import java.util.ArrayList;

public class Auditorio {

    private ArrayList<Cliente> listClientes;
    private Cliente modeloCliente;
    public ArrayList<Evento> listEvento;
    public ArrayList<Evento> listEventoEnCartelera;
    public Evento eventoActual;
    private Administrador admin;

    public Auditorio() {
        listClientes = new ArrayList<>();
        eventoActual = null;
        listEvento = new ArrayList<>();
        listEventoEnCartelera = new ArrayList<>();
        modeloCliente = null;
    }

    public Evento getEvento() {
        return eventoActual;
    }

    public void setEvento(Evento evento) {
        this.eventoActual = evento;
    }

    public void cargarCliente(Cliente cliente) {
        this.modeloCliente = cliente;
    }

    public void agregarCliente(Cliente cliente) {
        listClientes.add(cliente);
    }

    public void setClientes(ArrayList<Cliente> clientes) {
        this.listClientes = clientes;
    }

    public Cliente getClienteActual() {
        return modeloCliente;
    }

    public ArrayList<Cliente> getClientes() {
        return listClientes;
    }

    public Administrador cargarAdmin() {
        if (admin == null) {
            admin = new Administrador("a", "1");

        }
        return admin;

    }

    public Cliente autenticarCliente(String usuario, String contra) {
        for (Cliente cliente : listClientes) {
            if (cliente.getNombreUsuario().equalsIgnoreCase(usuario) && cliente.getContra().equals(contra)) {
                return cliente;
            }
        }
        return null;
    }

    public boolean usuarioExiste(String nombre) {
        for (Cliente cliente : listClientes) {
            if (cliente.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    public void agregarEvento(Evento evento) {
        listEvento.add(evento);
    }

    public void cargarEvento(Evento evento) {
        this.eventoActual = evento;
    }

    public ArrayList<Evento> getArregloEventos() {
        return listEvento;
    }

    public void setArregloEventos(ArrayList<Evento> arregloEventos) {
        this.listEvento = arregloEventos;
    }

    public void eliminarEvento(Evento evento) {
        listEvento.remove(evento);
    }

    public void cargarEventoEnSala(Evento evento) {
        this.eventoActual = evento;
    }

    public Evento getEventoActual() {
        return eventoActual;
    }

    public void setEventoActual(Evento eventoActual) {
        this.eventoActual = eventoActual;
        if (eventoActual != null && !listEventoEnCartelera.contains(eventoActual)) {
            listEventoEnCartelera.add(eventoActual);
        }
    }

    public ArrayList<Evento> getEventosEnCartelera() {
        return listEventoEnCartelera;
    }

    public void reiniciarAsientos() {
        if (eventoActual != null) {
            int[][] matriz = eventoActual.getMatrizAsientos();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    matriz[i][j] = 0;
                }
            }
        }
    }

    public double getRecaudacionGlobal() {
        double total = 0;
        for (Evento evento : listEvento) {
            total += evento.calcularRecaudacion();
        }
        return total;
    }

}
