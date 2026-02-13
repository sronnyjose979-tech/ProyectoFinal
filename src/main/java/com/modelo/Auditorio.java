package com.modelo;

import java.util.ArrayList;

public class Auditorio {

    private boolean[][] asientos;
    private ArrayList<ClienteModel> Clientes;// se crea un arrayList de cliente en el auditorio
    private ClienteModel clienteActual;
    public Evento evento;

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
    public void cargarCliente(ClienteModel cliente){
        this.clienteActual=cliente;
        
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
        for (int i = 0; i < asientos.length; i++) {
            for (int j = 0; j < asientos[i].length; j++) {
                asientos[i][j] = false;
            }
        }
    }
}
