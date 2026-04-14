package com.red;

import com.controlador.AdminPanelController;
import com.controlador.VentasDeAsientosController;
import com.modelo.Auditorio;
import com.modelo.Cliente;
import com.modelo.Evento;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;

public class ClienteVPN {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private Auditorio auditorio;

    public void conectar(String ip, int puerto, Auditorio auditorio) {
        this.auditorio = auditorio;
        new Thread(() -> {
            try {
                this.socket = new Socket(ip, puerto);
                this.salida = new PrintWriter(socket.getOutputStream(), true);
                this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                iniciarHiloEscucha();
            } catch (IOException e) {
                System.out.println("No se pudo conectar al servidor en " + ip + ":" + puerto);
            }
        }).start();
    }

    private void iniciarHiloEscucha() {
        try {
            String msg;
            while ((msg = entrada.readLine()) != null) {
                final String mensaje = msg;
                Platform.runLater(() -> procesarMensaje(mensaje));
            }
        } catch (IOException e) {
            System.out.println("Conexión perdida con el servidor.");
        }
    }

    private void procesarMensaje(String msg) {
        if (msg.startsWith("NUEVO_CLIENTE:")) {
            procesarNuevoCliente(msg);
        } else if (msg.startsWith("NUEVO_EVENTO:")) {
            procesarNuevoEvento(msg);
        } else if (msg.startsWith("RESERVAR_ASIENTO:")) {
            procesarReservaAsiento(msg);
        } else if (msg.equals("ACTUALIZAR_TODO")) {
            VentasDeAsientosController.refrescarBotones();
        }
    }

    private void procesarNuevoCliente(String datos) {
        String[] partes = datos.substring(14).split(",");
        if (partes.length == 2) {
            String nombre = partes[0];
            String pass = partes[1];
            if (!auditorio.usuarioExiste(nombre)) {
                auditorio.agregarCliente(new Cliente(nombre, pass));
                System.out.println("Sincronizado: Cliente " + nombre + " agregado.");
            }
        }
    }

    private void procesarNuevoEvento(String datos) {
        // Formato: "NUEVO_EVENTO:nombre,fecha,precio"
        String[] partes = datos.substring(13).split(",");
        if (partes.length == 3) {
            String nombre = partes[0];
            String fecha = partes[1];
            double precio = Double.parseDouble(partes[2]);
            
            if (!auditorio.eventoExiste(nombre)) {
                Evento nuevo = new Evento(nombre, fecha, precio);
                auditorio.agregarEvento(nuevo);
                auditorio.getEventosEnCartelera().add(nuevo); // Asegurar que aparezca en cartelera
                System.out.println("Sincronizado: Evento " + nombre + " agregado.");
                VentasDeAsientosController.refrescarBotones();
            }
        }
    }

    private void procesarReservaAsiento(String datos) {
        // Formato: "RESERVAR_ASIENTO:nombreEvento,fila,col"
        String[] partes = datos.substring(17).split(",");
        if (partes.length == 3) {
            String nombreEvento = partes[0];
            int fila = Integer.parseInt(partes[1]);
            int col = Integer.parseInt(partes[2]);

            for (Evento ev : auditorio.getEventosEnCartelera()) {
                if (ev.getNombre().equals(nombreEvento)) {
                    ev.getMatrizAsientos()[fila][col] = 2; // RESERVADA
                    VentasDeAsientosController.refrescarBotones();
                    break;
                }
            }
        }
    }

    public void enviarMensaje(String msg) {
        new Thread(() -> {
            if (salida != null) {
                salida.println(msg);
            }
        }).start();
    }
}