package com.red;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Servidor con ESTADO. Mantiene en memoria todos los datos del sistema
 * y los envía a cada cliente nuevo que se conecte (sincronización inicial).
 * También retransmite cada mensaje recibido a TODOS los clientes.
 */
public class ServidorVPN {

    // Escritores de todos los clientes conectados
    private static final List<PrintWriter> clientes =
            Collections.synchronizedList(new ArrayList<>());

    // ==================== ESTADO INTERNO ====================
    // [nombre, contraseña]
    private static final List<String[]> clientesRegistrados =
            Collections.synchronizedList(new ArrayList<>());

    // [nombre, fecha, precio]
    private static final List<String[]> eventosRegistrados =
            Collections.synchronizedList(new ArrayList<>());

    // clave = nombreEvento ; valor = lista de [fila, col]
    private static final Map<String, List<int[]>> reservasPorEvento =
            new ConcurrentHashMap<>();

    // Evento actualmente activo en la sala
    private static volatile String eventoEnSala = null;
    // =========================================================

    public void iniciarServidor(int puerto) {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("==============================================");
            System.out.println("  SERVIDOR DE AUDITORIO — Puerto " + puerto);
            System.out.println("==============================================");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[+] Cliente conectado: " + socket.getInetAddress());

                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                clientes.add(salida);

                // Enviar estado completo al nuevo cliente antes de empezar
                enviarEstadoInicial(salida);

                new Thread(() -> manejarCliente(socket, salida)).start();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envía al cliente recién conectado todos los datos que el servidor
     * ya tiene en memoria (clientes, eventos, reservas, evento en sala).
     */
    private void enviarEstadoInicial(PrintWriter pw) {
        try {
            System.out.println("  -> Enviando estado inicial...");

            synchronized (clientesRegistrados) {
                for (String[] c : clientesRegistrados) {
                    pw.println("SYNC_CLIENTE:" + c[0] + "," + c[1]);
                }
            }

            synchronized (eventosRegistrados) {
                for (String[] ev : eventosRegistrados) {
                    pw.println("SYNC_EVENTO:" + ev[0] + "," + ev[1] + "," + ev[2]);
                }
            }

            for (Map.Entry<String, List<int[]>> entry : reservasPorEvento.entrySet()) {
                List<int[]> lista = entry.getValue();
                synchronized (lista) {
                    for (int[] rc : lista) {
                        pw.println("SYNC_RESERVA:" + entry.getKey() + "," + rc[0] + "," + rc[1]);
                    }
                }
            }

            if (eventoEnSala != null) {
                pw.println("EVENTO_EN_SALA:" + eventoEnSala);
            }

            // Marca de fin para que el cliente sepa que terminó la sincronización
            pw.println("FIN_ESTADO");
            System.out.println("  -> Estado inicial enviado OK.");
        } catch (Exception e) {
            System.err.println("[ERROR] Al enviar estado inicial: " + e.getMessage());
        }
    }

    private void manejarCliente(Socket socket, PrintWriter miSalida) {
        try (BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {

            String comando;
            while ((comando = entrada.readLine()) != null) {
                System.out.println("[MSG] " + comando);

                // 1. Actualizar estado interno del servidor
                procesarEnServidor(comando);

                // 2. Retransmitir a TODOS los clientes (incluyendo el remitente)
                final String cmd = comando;
                synchronized (clientes) {
                    for (PrintWriter cliente : clientes) {
                        cliente.println(cmd);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[-] Cliente desconectado.");
        } finally {
            clientes.remove(miSalida);
        }
    }

    /**
     * Actualiza el estado interno del servidor según el mensaje recibido.
     */
    private void procesarEnServidor(String comando) {
        try {
            if (comando.startsWith("NUEVO_CLIENTE:")) {
                String datos = comando.substring(14);
                String[] partes = datos.split(",", 2);
                if (partes.length == 2) {
                    synchronized (clientesRegistrados) {
                        boolean existe = false;
                        for (String[] c : clientesRegistrados) {
                            if (c[0].equalsIgnoreCase(partes[0])) {
                                existe = true;
                                break;
                            }
                        }
                        if (!existe) {
                            clientesRegistrados.add(new String[]{partes[0], partes[1]});
                            System.out.println("  [Estado] Cliente guardado: " + partes[0]);
                        }
                    }
                }

            } else if (comando.startsWith("NUEVO_EVENTO:")) {
                String datos = comando.substring(13);
                String[] partes = datos.split(",", 3);
                if (partes.length == 3) {
                    synchronized (eventosRegistrados) {
                        boolean existe = false;
                        for (String[] ev : eventosRegistrados) {
                            if (ev[0].equalsIgnoreCase(partes[0])) {
                                existe = true;
                                break;
                            }
                        }
                        if (!existe) {
                            eventosRegistrados.add(new String[]{partes[0], partes[1], partes[2]});
                            System.out.println("  [Estado] Evento guardado: " + partes[0]);
                        }
                    }
                }

            } else if (comando.startsWith("RESERVAR_ASIENTO:")) {
                String datos = comando.substring(17);
                String[] partes = datos.split(",", 3);
                if (partes.length == 3) {
                    String evName = partes[0];
                    int fila = Integer.parseInt(partes[1]);
                    int col  = Integer.parseInt(partes[2]);
                    reservasPorEvento
                            .computeIfAbsent(evName, k ->
                                    Collections.synchronizedList(new ArrayList<>()))
                            .add(new int[]{fila, col});
                    System.out.println("  [Estado] Reserva: " + evName
                            + " [" + fila + "," + col + "]");
                }

            } else if (comando.startsWith("CARGAR_EVENTO:")) {
                eventoEnSala = comando.substring(14).trim();
                System.out.println("  [Estado] Evento en sala: " + eventoEnSala);
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Procesando en servidor: " + e.getMessage());
        }
    }
}