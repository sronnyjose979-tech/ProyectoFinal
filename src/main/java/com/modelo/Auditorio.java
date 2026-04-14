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

    // --- MÉTODOS DE CLIENTES ---

    public void agregarCliente(Cliente cliente) {
        if (cliente != null) {
            listClientes.add(cliente);
        }
    }

    public boolean usuarioExiste(String nombre) {
        for (Cliente cliente : listClientes) {
            if (cliente.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida si las credenciales coinciden con algún cliente registrado.
     * @return true si es válido, false de lo contrario.
     */
    public boolean validarLogin(String usuario, String contrasena) {
        for (Cliente c : listClientes) {
            if (c.getNombreUsuario().equalsIgnoreCase(usuario) && c.getContra().equals(contrasena)) {
                return true;
            }
        }
        return false;
    }

    public Cliente autenticarCliente(String usuario, String contra) {
        for (Cliente cliente : listClientes) {
            if (cliente.getNombreUsuario().equalsIgnoreCase(usuario) && cliente.getContra().equals(contra)) {
                return cliente;
            }
        }
        return null;
    }

    public ArrayList<Cliente> getClientes() {
        return listClientes;
    }

    public void setClientes(ArrayList<Cliente> clientes) {
        this.listClientes = clientes;
    }

    public void cargarCliente(Cliente cliente) {
        this.modeloCliente = cliente;
    }

    public Cliente getClienteActual() {
        return modeloCliente;
    }

    // --- MÉTODOS DE ADMINISTRACIÓN ---

    public Administrador cargarAdmin() {
        if (admin == null) {
            admin = new Administrador("admin", "admin");
        }
        return admin;
    }

    // --- MÉTODOS DE EVENTOS ---

    public void agregarEvento(Evento evento) {
        listEvento.add(evento);
    }

    public boolean eventoExiste(String nombre) {
        for (Evento e : listEvento) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    public void eliminarEvento(Evento evento) {
        listEvento.remove(evento);
    }

    public void cargarEvento(Evento evento) {
        this.eventoActual = evento;
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

    public ArrayList<Evento> getArregloEventos() {
        return listEvento;
    }

    public void setArregloEventos(ArrayList<Evento> arregloEventos) {
        this.listEvento = arregloEventos;
    }

    public ArrayList<Evento> getEventosEnCartelera() {
        return listEventoEnCartelera;
    }

    // --- MÉTODOS DE UTILIDAD ---

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

    public Evento getEvento() {
        return eventoActual;
    }

    public void setEvento(Evento evento) {
        this.eventoActual = evento;
    }
}