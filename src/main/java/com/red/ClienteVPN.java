package com.red;

import com.modelo.Auditorio;
import com.modelo.Cliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteVPN {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private Auditorio auditorio; // Referencia para actualizar datos

    public void conectar(String ip, int puerto, Auditorio auditorio) {
        this.auditorio = auditorio;
        try {
            this.socket = new Socket(ip, puerto);
            this.salida = new PrintWriter(socket.getOutputStream(), true);
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Hilo que siempre escucha lo que viene del servidor
            iniciarHiloEscucha();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniciarHiloEscucha() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = entrada.readLine()) != null) {
                    if (msg.startsWith("NUEVO_CLIENTE:")) {
                        procesarNuevoCliente(msg);
                    }
                }
            } catch (IOException e) {
                System.out.println("Conexión perdida con el servidor.");
            }
        }).start();
    }

    private void procesarNuevoCliente(String datos) {
        // Formato esperado: "NUEVO_CLIENTE:nombre,contraseña"
        String[] partes = datos.substring(14).split(",");
        if (partes.length == 2) {
            String nombre = partes[0];
            String pass = partes[1];
            
            // Evitar duplicados locales
            if (!auditorio.usuarioExiste(nombre)) {
                auditorio.agregarCliente(new Cliente(nombre, pass));
                System.out.println("Sincronizado: Cliente " + nombre + " agregado por red.");
            }
        }
    }

    public void enviarMensaje(String msg) {
        if (salida != null) salida.println(msg);
    }
}