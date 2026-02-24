package com.modelo;

import java.io.*;
import java.nio.file.*;

public class Persistencia {

    private static final String CARPETA_DATA = "data";
    private static final String ARCHIVO_EVENTOS = CARPETA_DATA + "/eventos.csv";
    private static final String ARCHIVO_CLIENTES = CARPETA_DATA + "/clientes.csv";
    private static final String ARCHIVO_ENTRADAS = CARPETA_DATA + "/entradas.csv";

    // Método para asegurar que la carpeta de persistencia exista
    private static void asegurarCarpeta() {
        try {
            Files.createDirectories(Paths.get(CARPETA_DATA));
        } catch (IOException e) {
            System.err.println("[Persistencia] No se pudo crear la carpeta de datos: " + e.getMessage());
        }
    }

    public static void guardarDatos(Auditorio auditorio) {
        asegurarCarpeta();
        guardarEventos(auditorio);
        guardarClientes(auditorio);
        guardarEntradas(auditorio);
        System.out.println("[Persistencia] Datos guardados en carpeta '" + CARPETA_DATA + "'.");
    }

    public static void exportarTicketATxt(Entrada entrada) {
        asegurarCarpeta();
        String nombreArchivo = CARPETA_DATA + "/Entrada_" + entrada.getIdEntrada() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.print(entrada.generarTicket());
        } catch (IOException e) {
            /**/}
    }

    private static void guardarEventos(Auditorio auditorio) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            if (auditorio.getArregloEventos()!= null) {
                pw.println("NOMBRE,FECHA,PRECIO");
                for (Evento evento : auditorio.getArregloEventos()) {
                    pw.println(evento.getNombre() + "," + evento.getFecha() + "," + evento.getPrecioBase());
                }
            }
        } catch (IOException e) {
            /**/}
    }

    private static void guardarClientes(Auditorio auditorio) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES))) {
            if (auditorio.getClientes() != null) {
                pw.println("USUARIO,CONTRASEÑA");
                for (Cliente cliente : auditorio.getClientes()) {
                    pw.println(cliente.getNombreUsuario() + "," + cliente.getContra());
                }
            }
        } catch (IOException e) {
            /**/}
    }

    private static void guardarEntradas(Auditorio auditorio) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_ENTRADAS))) {
            if (auditorio.getClientes() != null) {
                pw.println("TIPO,ID,CLIENTE,EVENTO,PRECIO,CANTIDAD,ASIENTOS,FECHA_COMPRA");
                for (Cliente cliente : auditorio.getClientes()) {
                    if (cliente.getEntradas() != null) {
                        for (Entrada entrada : cliente.getEntradas()) {
                            pw.println(
                                    entrada.tipoEntrada() + "," +
                                            entrada.getIdEntrada() + "," +
                                            entrada.getNombreCliente() + "," +
                                            entrada.getEvento().getNombre() + "," +
                                            entrada.calcularPrecio() + "," +
                                            entrada.getCantidadAsientos() + "," +
                                            "\"" + entrada.getDetalleAsientos().replace("\n", "; ") + "\"," +
                                            entrada.getFechaCompra());
                        }
                    }
                }
            }
        } catch (IOException e) {
            /**/}
    }

    public static void cargarDatos(Auditorio auditorio) {
        asegurarCarpeta();
        cargarClientes(auditorio);
        cargarEventos(auditorio);
        cargarEntradas(auditorio);
    }

    private static void cargarClientes(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2)
                    auditorio.agregarCliente(new Cliente(partes[0], partes[1]));
            }
        } catch (IOException e) {
            /**/}
    }

    private static void cargarEventos(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_EVENTOS);
        if (!archivo.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    auditorio.agregarEvento(new Evento(partes[0], partes[1], Double.parseDouble(partes[2])));
                }
            }
        } catch (IOException e) {
            /**/}
    }

    private static void cargarEntradas(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_ENTRADAS);
        if (!archivo.exists())
            return;

        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (partes.length < 8)
                    continue;

                String tipo = partes[0];
                int id = Integer.parseInt(partes[1]);
                String cliente = partes[2];
                String nombreEv = partes[3];
                double precio = Double.parseDouble(partes[4]);
                int cant = Integer.parseInt(partes[5]);
                String detalle = partes[6].replace("\"", "");
                String fecha = partes[7];

                Evento ev = null;
                for (Evento e : auditorio.getArregloEventos()) {
                    if (e.getNombre().equals(nombreEv)) {
                        ev = e;
                        break;
                    }
                }
                if (ev == null)
                    continue;

                String[] ocupados = detalle.split("; ");
                for (String silla : ocupados) {
                    try {
                        String[] coords = silla.replaceAll("[^0-9 ]", "").trim().split(" +");
                        if (coords.length >= 2) {
                            int f = Integer.parseInt(coords[0]) - 1;
                            int c = Integer.parseInt(coords[1]) - 1;
                            if (f >= 0 && f < 10 && c >= 0 && c < 10) {
                                ev.getMatrizAsientos()[f][c] = 2;
                            }
                        }
                    } catch (Exception ex) {
                        /**/}
                }

                Entrada en;
                if (tipo.equals("VIP"))
                    en = new EntradaVip(cliente, ev, precio, cant, detalle.replace("; ", "\n"));
                else if (tipo.equals("ESTUDIANTIL"))
                    en = new EntradaEstudiante(cliente, ev, precio, cant, detalle.replace("; ", "\n"));
                else
                    en = new EntradaGeneral(cliente, ev, precio, cant, detalle.replace("; ", "\n"));

                en.setIdEntrada(id);
                en.setPrecioFinalCalculado(precio);
                en.setFechaCompra(fecha);
                if (id > maxId)
                    maxId = id;

                ev.agregarEntrada(en);
                for (Cliente c : auditorio.getClientes()) {
                    if (c.getNombreUsuario().equals(cliente)) {
                        c.agregarEntrada(en);
                        break;
                    }
                }
            }
            Entrada.setContadorId(maxId + 1);
        } catch (IOException e) {
            /**/}
    }
}
