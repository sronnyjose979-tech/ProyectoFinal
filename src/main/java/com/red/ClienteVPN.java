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

/**
 * Cliente de red. Envía mensajes al servidor y procesa los mensajes
 * recibidos (incluida la sincronización inicial SYNC_*).
 */
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
                this.entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                System.out.println("[RED] Conectado al servidor " + ip + ":" + puerto);
                iniciarHiloEscucha();
            } catch (IOException e) {
                System.out.println("[RED] Sin conexión con servidor en "
                        + ip + ":" + puerto + " — Modo sin red activo.");
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
            System.out.println("[RED] Conexión perdida con el servidor.");
        }
    }

    // ===================== PROCESADOR DE MENSAJES =====================
    private void procesarMensaje(String msg) {
        if (msg.startsWith("NUEVO_CLIENTE:")) {
            procesarCliente(msg.substring(14));

        } else if (msg.startsWith("SYNC_CLIENTE:")) {
            // Mismo formato, misma lógica
            procesarCliente(msg.substring(13));

        } else if (msg.startsWith("NUEVO_EVENTO:")) {
            procesarEvento(msg.substring(13));

        } else if (msg.startsWith("SYNC_EVENTO:")) {
            procesarEvento(msg.substring(12));

        } else if (msg.startsWith("RESERVAR_ASIENTO:")) {
            procesarReserva(msg.substring(17));

        } else if (msg.startsWith("SYNC_RESERVA:")) {
            procesarReserva(msg.substring(13));

        } else if (msg.startsWith("EVENTO_EN_SALA:")) {
            procesarEventoEnSala(msg.substring(15));

        } else if (msg.startsWith("CARGAR_EVENTO:")) {
            // El admin cargó un evento — actualizar en todos los clientes
            procesarEventoEnSala(msg.substring(14));

        } else if (msg.equals("ACTUALIZAR_TODO")) {
            VentasDeAsientosController.refrescarBotones();
            AdminPanelController.refrescarTablaEventos();

        }
        // FIN_ESTADO: solo marca de fin de sincronización, sin acción
    }

    /** Registra un cliente si no existe ya en el auditorio local. */
    private void procesarCliente(String datos) {
        String[] partes = datos.split(",", 2);
        if (partes.length == 2) {
            String nombre = partes[0].trim();
            String pass   = partes[1].trim();
            if (!auditorio.usuarioExiste(nombre)) {
                auditorio.agregarCliente(new Cliente(nombre, pass));
                System.out.println("[SYNC] Cliente: " + nombre);
            }
        }
    }

    /** Registra un evento si no existe ya en el auditorio local. */
    private void procesarEvento(String datos) {
        String[] partes = datos.split(",", 3);
        if (partes.length == 3) {
            String nombre = partes[0].trim();
            String fecha  = partes[1].trim();
            try {
                double precio = Double.parseDouble(partes[2].trim());
                if (!auditorio.eventoExiste(nombre)) {
                    Evento nuevo = new Evento(nombre, fecha, precio);
                    auditorio.agregarEvento(nuevo);
                    System.out.println("[SYNC] Evento: " + nombre);
                }
            } catch (NumberFormatException e) {
                System.err.println("[RED] Precio inválido en evento: " + datos);
            }
            // Refrescar UI en ambos paneles
            VentasDeAsientosController.refrescarBotones();
            AdminPanelController.refrescarTablaEventos();
        }
    }

    /** Marca un asiento como RESERVADO en el evento correspondiente. */
    private void procesarReserva(String datos) {
        String[] partes = datos.split(",", 3);
        if (partes.length == 3) {
            String nombreEvento = partes[0].trim();
            try {
                int fila = Integer.parseInt(partes[1].trim());
                int col  = Integer.parseInt(partes[2].trim());

                for (Evento ev : auditorio.getArregloEventos()) {
                    if (ev.getNombre().equals(nombreEvento)) {
                        int[][] matriz = ev.getMatrizAsientos();
                        if (fila >= 0 && fila < matriz.length
                                && col >= 0 && col < matriz[0].length) {
                            matriz[fila][col] = 2; // RESERVADA
                        }
                        VentasDeAsientosController.refrescarBotones();
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("[RED] Coordenada inválida en reserva: " + datos);
            }
        }
    }

    /**
     * Actualiza el evento activo en el auditorio local y refresca la UI.
     * Llamado cuando el admin hace "Cargar en Sala".
     */
    private void procesarEventoEnSala(String nombreEvento) {
        nombreEvento = nombreEvento.trim();
        for (Evento ev : auditorio.getArregloEventos()) {
            if (ev.getNombre().equals(nombreEvento)) {
                auditorio.setEventoActual(ev);
                System.out.println("[SYNC] Evento en sala: " + nombreEvento);
                VentasDeAsientosController.refrescarBotones();
                AdminPanelController.refrescarTablaEventos();
                break;
            }
        }
    }

    // ===================== ENVÍO =====================
    public void enviarMensaje(String msg) {
        new Thread(() -> {
            if (salida != null) {
                salida.println(msg);
            } else {
                System.out.println("[RED] Sin conexión — mensaje no enviado: " + msg);
            }
        }).start();
    }
}