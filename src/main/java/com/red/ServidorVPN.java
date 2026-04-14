package com.red;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServidorVPN {
    // Lista para guardar a todos los conectados
    private static List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());

    public void iniciarServidor(int puerto) {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            while (true) {
                Socket socket = serverSocket.accept();
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                clientes.add(salida); // Agregamos al cliente a la lista
                
                new Thread(() -> manejarCliente(socket, salida)).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void manejarCliente(Socket socket, PrintWriter miSalida) {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String comando;
            while ((comando = entrada.readLine()) != null) {
                // Reenviar el comando a TODOS los demás
                for (PrintWriter cliente : clientes) {
                    cliente.println(comando);
                }
            }
        } catch (IOException e) { 
            clientes.remove(miSalida);
        }
    }
}