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
    public Evento evento;
    private Administrador admin;
    VentasDeAsientosController butacas;

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Auditorio() {
        asientos = new boolean[10][10]; // matriz obligatoria del proyecto
        Clientes = new ArrayList<>();
        clienteActual = null;
    }

    public void agregarCliente(ClienteModel cliente) {// se va agregar el cliente para poder usarse en todas las clases
        Clientes.add(cliente);

    }

    public Administrador cargarAdmin() {
        return admin = new Administrador("a", "1");

    }

    public void cargarCliente(ClienteModel cliente) {
        this.clienteActual = cliente;

    }

    public ClienteModel getClienteActual() {
        return clienteActual;
    }

    public void ocuparAsiento(int fila, int columna) {
        asientos[fila][columna] = true;
    }

    public boolean estaOcupado(int fila, int columna) {
        return asientos[fila][columna];
    }

    public ArrayList<ClienteModel> getClientes() {
        return Clientes;
    }

    public void setClientes(ArrayList<ClienteModel> Clientes) {
        this.Clientes = Clientes;
    }

    public void reiniciarAsientos() {

      butacas.reiniciarButacas();
    }
    
     public void crearEventoPorDefecto() {
        mostrarAlerta("Aviso", "Se ha creado un evento de prueba!", Alert.AlertType.WARNING);

        auditorio.setEvento(new Evento("EVENTO DE PRUEBA", "01/01/2001", 1000)); //SOLO USAR SI NO HAY EVENTO
    }
}
