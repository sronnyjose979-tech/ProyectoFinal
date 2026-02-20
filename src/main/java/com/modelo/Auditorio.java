package com.modelo;

import java.util.ArrayList;

public class Auditorio {

    private ArrayList<ClienteModel> clientes;
    private ClienteModel clienteActual;
    public ArrayList<Evento> eventoArrayList;
    public ArrayList<Evento> eventosEnCartelera;
    public Evento eventoActual;
    private Administrador admin;

    public Auditorio() {
        clientes = new ArrayList<>();
        eventoActual = null;
        eventoArrayList = new ArrayList<>();
        eventosEnCartelera = new ArrayList<>();
        clienteActual = null;
    }

    public Evento getEvento() {
        return eventoActual;
    }

    public void setEvento(Evento evento) {
        this.eventoActual = evento;
    }

    // Sección Cliente
    public void agregarCliente(ClienteModel cliente) {
        clientes.add(cliente);
    }

    public void cargarCliente(ClienteModel cliente) {
        this.clienteActual = cliente;
    }

    public ClienteModel getClienteActual() {
        return clienteActual;
    }

    public Administrador cargarAdmin() {
        return admin = new Administrador("a", "1");
    }

    public ArrayList<ClienteModel> getClientes() {
        return clientes;
    }

    public void setClientes(ArrayList<ClienteModel> clientes) {
        this.clientes = clientes;
    }

    public ClienteModel autenticarCliente(String usuario, String contra) {
        for (ClienteModel c : clientes) {
            if (c.getNombreUsuario().equalsIgnoreCase(usuario) && c.getContra().equals(contra)) {
                return c;
            }
        }
        return null;
    }

    public boolean usuarioExiste(String nombre) {
        for (ClienteModel c : clientes) {
            if (c.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // Sección Evento
    public void agregarEvento(Evento evento) {
        eventoArrayList.add(evento);
    }

    public void cargarEvento(Evento evento) {
        this.eventoActual = evento;
    }

    public ArrayList<Evento> getEventoArrayList() {
        return eventoArrayList;
    }

    public void setEventoArrayList(ArrayList<Evento> eventoArrayList) {
        this.eventoArrayList = eventoArrayList;
    }

    public void eliminarEvento(Evento evento) {
        eventoArrayList.remove(evento);
    }

    public void cargarEventoEnSala(Evento evento) {
        this.eventoActual = evento;
    }

    public Evento getEventoActual() {
        return eventoActual;
    }

    public void setEventoActual(Evento eventoActual) {
        this.eventoActual = eventoActual;
        if (eventoActual != null && !eventosEnCartelera.contains(eventoActual)) {
            eventosEnCartelera.add(eventoActual);
        }
    }

    public ArrayList<Evento> getEventosEnCartelera() {
        return eventosEnCartelera;
    }

    // Sección Asiento
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
        for (Evento evento : eventoArrayList) {
            total += evento.calcularRecaudacion();
        }
        return total;
    }

    public void crearEventoPorDefecto() {
        System.out.println("Creando evento de prueba interno...");
        this.setEvento(new Evento("EVENTO DE PRUEBA", "01/01/2001", 1000));
    }
}
