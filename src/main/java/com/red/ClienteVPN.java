package com.red;

import java.io.*;
import java.net.*;

public class ClienteVPN {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;

    public void conectar(String ipTailscale, int puerto) {
        try {
            this.socket = new Socket(ipTailscale, puerto);
            this.salida = new PrintWriter(socket.getOutputStream(), true);
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Conectado exitosamente al servidor VPN");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String msg) {
        if (salida != null) salida.println(msg);
    }
}