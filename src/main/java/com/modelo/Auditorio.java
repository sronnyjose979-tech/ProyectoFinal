package com.modelo;

import java.util.ArrayList;

public class Auditorio {

    private ArrayList<ClienteModel> arregloClientes;//arregloClientes
    private ClienteModel modeloCliente;//modeloCliente
    public ArrayList<Evento> arregloEventos;//arregloEventos
    public ArrayList<Evento> arregloEventoEnCartelera;//arregloEventoEnCartelera
    public Evento eventoActual;
    private Administrador admin;

    public Auditorio() {
        arregloClientes = new ArrayList<>();//arregloClientes
        eventoActual = null;
        arregloEventos = new ArrayList<>();//arregloEventos
        arregloEventoEnCartelera = new ArrayList<>();//arregloEventoEnCartelera
        modeloCliente = null;//modeloCliente
    }

    public Evento getEvento() {
        return eventoActual;
    }

    public void setEvento(Evento evento) {
        this.eventoActual = evento;
    }

    // Sección Cliente
    public void cargarCliente(ClienteModel cliente) {
        this.modeloCliente = cliente;//modeloCliente
    }

    public void agregarCliente(ClienteModel cliente) {
        arregloClientes.add(cliente);//arregloClientes
    }

    public void setClientes(ArrayList<ClienteModel> clientes) {
        this.arregloClientes = clientes;//arregloClientes
    }

    public ClienteModel getClienteActual() {
        return modeloCliente;//modeloCliente
    }

    public ArrayList<ClienteModel> getClientes() {
        return arregloClientes;//arregloClientes
    }

    public Administrador cargarAdmin() {
        return admin = new Administrador("a", "1");
    }

    public ClienteModel autenticarCliente(String usuario, String contra) {
        for (ClienteModel c : arregloClientes) {//arregloClientes
            if (c.getNombreUsuario().equalsIgnoreCase(usuario) && c.getContra().equals(contra)) {
                return c;
            }
        }
        return null;
    }

    /*public ClienteModel sinUso(String usuario, String contra) {
        for (int i = 0; i < arregloClientes.size(); i++) {
            ClienteModel cliente = arregloClientes.get(i);
            if(cliente.getNombreUsuario().equalsIgnoreCase(usuario)&&cliente.getContra().equals(contra)){
                return cliente;
            }
        }
        return null;

    }*/

    public boolean usuarioExiste(String nombre) {
        for (ClienteModel c : arregloClientes) {//arregloClientes
            if (c.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // Sección Evento
    public void agregarEvento(Evento evento) {
        arregloEventos.add(evento);//arregloEventos
    }

    public void cargarEvento(Evento evento) {
        this.eventoActual = evento;
    }

    public ArrayList<Evento> getArregloEventos() {
        return arregloEventos;//arregloEventos
    }

    public void setArregloEventos(ArrayList<Evento> arregloEventos) {
        this.arregloEventos = arregloEventos;//arregloEventos
    }

    public void eliminarEvento(Evento evento) {
        arregloEventos.remove(evento);//arregloEventos
    }

    public void cargarEventoEnSala(Evento evento) {
        this.eventoActual = evento;
    }

    public Evento getEventoActual() {
        return eventoActual;
    }

    public void setEventoActual(Evento eventoActual) {
        this.eventoActual = eventoActual;
        if (eventoActual != null && !arregloEventoEnCartelera.contains(eventoActual)) {//arregloEventoEnCartelera
            arregloEventoEnCartelera.add(eventoActual);//arregloEventoEnCartelera
        }
    }

    public ArrayList<Evento> getEventosEnCartelera() {
        return arregloEventoEnCartelera;//arregloEventoEnCartelera
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
        for (Evento evento : arregloEventos) {//arregloEventos
            total += evento.calcularRecaudacion();
        }
        return total;
    }

    public void crearEventoPorDefecto() {
        System.out.println("Creando evento de prueba interno...");
        this.setEvento(new Evento("EVENTO DE PRUEBA", "01/01/2001", 1000));
    }
}
