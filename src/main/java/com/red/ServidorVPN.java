package com.red;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServidorVPN {
    // Lista de hilos de salida para enviar mensajes a todos los conectados
    private static List<PrintWriter> clientesConectados = new ArrayList<>();

    public void iniciarServidor(int puerto) {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor a la escucha en puerto: " + puerto);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + socket.getInetAddress());
                
                // Hilo para manejar a este cliente específico
                new Thread(() -> manejarCliente(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manejarCliente(Socket socket) {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {
            
            // Registro del cliente para el broadcast
            synchronized (clientesConectados) {
                clientesConectados.add(salida);
            }

            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Recibido y difundiendo: " + mensaje);
                difundirMensaje(mensaje);
            }
        } catch (IOException e) {
            System.out.println("Un cliente se ha desconectado.");
        }
    }

    private void difundirMensaje(String msg) {
        synchronized (clientesConectados) {
            for (PrintWriter cliente : clientesConectados) {
                cliente.println(msg);
            }
        }
    }
}