package com.red;

import java.io.*;
import java.net.*;

public class ServidorVPN {
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
            
            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Recibido: " + mensaje);
                salida.println("Servidor recibió: " + mensaje);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        }
    }
}