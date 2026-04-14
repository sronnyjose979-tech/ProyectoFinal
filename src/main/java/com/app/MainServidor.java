package com.app;

import com.red.ServidorVPN;

public class MainServidor {
    public static void main(String[] args) {
        ServidorVPN servidor = new ServidorVPN();
        
        // El puerto debe ser el mismo que configures en los clientes
        int puerto = 5000; 
        
        System.out.println("--- INICIANDO SISTEMA DE RED VPN ---");
        servidor.iniciarServidor(puerto);
    }
}