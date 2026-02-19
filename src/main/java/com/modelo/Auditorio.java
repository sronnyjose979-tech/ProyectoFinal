package com.modelo;

import static UtilsAlertas.mostrarAlerta.mostrarAlerta;
import static com.proyectou.App.auditorio;
import com.proyectou.VentasDeAsientosController;
import java.util.ArrayList;
import javafx.scene.control.Alert;

public class Auditorio {

    private boolean[][] asientos;
    private ArrayList<ClienteModel> Clientes;// se crea un arrayList de cliente en el auditorio
    private ClienteModel clienteActual;
    public ArrayList<Evento> eventoArrayList;
    public ArrayList<Evento> eventosEnCartelera; 
    public Evento eventoActual;
    private Administrador admin;

    public Evento getEvento() {
        return eventoActual;
    }

    public void setEvento(Evento evento) {
        this.eventoActual = evento;
    }

    public Auditorio() {
        asientos = new boolean[10][10]; // matriz obligatoria del proyecto
        Clientes = new ArrayList<>();
        eventoActual = null;
        eventoArrayList = new ArrayList<>();
        eventosEnCartelera = new ArrayList<>();

        clienteActual = null;
    }

    // seccion cliente
    public void agregarCliente(ClienteModel cliente) {// se va agregar el cliente para poder usarse en todas las clases
        Clientes.add(cliente);

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
        return Clientes;
    }

    public void setClientes(ArrayList<ClienteModel> Clientes) {
        this.Clientes = Clientes;
    }

    public ClienteModel autenticarCliente(String usuario, String contra) {
        for (ClienteModel c : Clientes) {
            if (c.getNombreUsuario().equalsIgnoreCase(usuario) && c.getContra().equals(contra)) {
                return c;
            }
        }
        return null;
    }

    public boolean usuarioExiste(String nombre) {
        for (ClienteModel c : Clientes) {
            if (c.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // seccion evento
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

    // seccion asiento
    public void ocuparAsiento(int fila, int columna) {
        asientos[fila][columna] = true;
    }

    public boolean estaOcupado(int fila, int columna) {
        return asientos[fila][columna];
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

        for (Evento evento : eventoArrayList) {
            total += evento.calcularRecaudacion();
        }

        return total;
    }

    public void crearEventoPorDefecto() {
        mostrarAlerta("Aviso", "Se ha creado un evento de prueba!", Alert.AlertType.WARNING);

        auditorio.setEvento(new Evento("EVENTO DE PRUEBA", "01/01/2001", 1000)); // SOLO USAR SI NO HAY EVENTO
    }
}
