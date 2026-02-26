package com.modelo;

import java.io.*;
import java.nio.file.*;

public class Persistencia {

    private static final String CARPETA_DATOS = "datos";
    private static final String ARCHIVO_EVENTOS = CARPETA_DATOS + "/eventos.csv";
    private static final String ARCHIVO_CLIENTES = CARPETA_DATOS + "/clientes.csv";
    private static final String ARCHIVO_ENTRADAS = CARPETA_DATOS + "/entradas.csv";

    private static void asegurarCarpeta() {
        try {
            Files.createDirectories(Paths.get(CARPETA_DATOS));
        } catch (IOException e) {
            System.err.println("No se pudo crear la carpeta de datos: " + e.getMessage());
        }
    }

    public static void guardarDatos(Auditorio auditorio) {
        asegurarCarpeta();
        guardarEventos(auditorio);
        guardarClientes(auditorio);
        guardarEntradas(auditorio);
        System.out.println("Datos guardados en carpeta " + CARPETA_DATOS + ".");
    }

    public static void cargarDatos(Auditorio auditorio) {
        asegurarCarpeta();
        cargarClientes(auditorio);
        cargarEventos(auditorio);
        cargarEntradas(auditorio);
    }

    public static void exportarTicketATxt(Entrada entrada) {
        asegurarCarpeta();
        String nombreArchivo = CARPETA_DATOS + "/Entrada_" + entrada.getIdEntrada() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.print(entrada.generarTicket());
        } catch (IOException e) {

        }
    }

    private static void guardarEventos(Auditorio auditorio) {
        try (PrintWriter guardar = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            if (auditorio.getArregloEventos() != null) {
                guardar.println("NOMBRE,FECHA,PRECIO");
                for (Evento evento : auditorio.getArregloEventos()) {
                    guardar.println(evento.getNombre() + "," + evento.getFecha() + "," + evento.getPrecioBase());
                }
            }
        } catch (IOException e) {

        }
    }

    private static void cargarEventos(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_EVENTOS);
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader cargar = new BufferedReader(new FileReader(archivo))) {
            cargar.readLine();
            String linea;
            while ((linea = cargar.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    auditorio.agregarEvento(new Evento(partes[0], partes[1], Double.parseDouble(partes[2])));
                }
            }
        } catch (IOException e) {

        }
    }

    private static void guardarClientes(Auditorio auditorio) {
        try (PrintWriter guardar = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES))) {
            if (auditorio.getClientes() != null) {
                guardar.println("USUARIO,CONTRASEÑA");
                for (Cliente cliente : auditorio.getClientes()) {
                    guardar.println(cliente.getNombreUsuario() + "," + cliente.getContra());
                }
            }
        } catch (IOException e) {

        }
    }

    private static void cargarClientes(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader cargar = new BufferedReader(new FileReader(archivo))) {
            cargar.readLine();
            String linea;
            while ((linea = cargar.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    auditorio.agregarCliente(new Cliente(partes[0], partes[1]));
                }
            }
        } catch (IOException e) {

        }
    }

    private static void guardarEntradas(Auditorio auditorio) {
        try (PrintWriter guardar = new PrintWriter(new FileWriter(ARCHIVO_ENTRADAS))) {
            if (auditorio.getClientes() != null) {
                guardar.println("TIPO,ID,CLIENTE,EVENTO,PRECIO,CANTIDAD,ASIENTOS,FECHA_COMPRA");
                for (Cliente cliente : auditorio.getClientes()) {
                    if (cliente.getEntradas() != null) {
                        for (Entrada entrada : cliente.getEntradas()) {
                            guardar.println(entrada.tipoEntrada() + "," + entrada.getIdEntrada() + "," + entrada.getNombreCliente() + "," + entrada.getEvento().getNombre() + +entrada.calcularPrecio() + "," + entrada.getCantidadAsientos() + "," + "\"" + entrada.getDetalleAsientos().replace("\n", "; ") + "\"," + entrada.getFechaCompra());
                        }
                    }
                }
            }
        } catch (IOException e) {

        }
    }

    private static void cargarEntradas(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_ENTRADAS);
        if (!archivo.exists()) {
            return;
        }

        int maxId = 0;
        try (BufferedReader cargar = new BufferedReader(new FileReader(archivo))) {
            cargar.readLine();
            String linea;
            while ((linea = cargar.readLine()) != null) {
                String[] partes = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (partes.length < 8) {
                    continue;
                }

                String tipo = partes[0];
                int id = Integer.parseInt(partes[1]);
                String cliente = partes[2];
                String nombreEv = partes[3];
                double precio = Double.parseDouble(partes[4]);
                int cant = Integer.parseInt(partes[5]);
                String detalle = partes[6].replace("\"", "");
                String fecha = partes[7];

                Evento evento = null;
                for (Evento event : auditorio.getArregloEventos()) {
                    if (event.getNombre().equals(nombreEv)) {
                        evento = event;
                        break;
                    }
                }
                if (evento == null) {
                    continue;
                }

                String[] ocupados = detalle.split("; ");
                for (String silla : ocupados) {
                    try {
                        String[] coords = silla.replaceAll("[^0-9 ]", "").trim().split(" +");
                        if (coords.length >= 2) {
                            int f = Integer.parseInt(coords[0]) - 1;
                            int c = Integer.parseInt(coords[1]) - 1;
                            if (f >= 0 && f < 10 && c >= 0 && c < 10) {
                                evento.getMatrizAsientos()[f][c] = 2;
                            }
                        }
                    } catch (Exception ex) {

                    }
                }

                Entrada entrada;
                if (tipo.equals("VIP")) {
                    entrada = new EntradaVip(cliente, evento, precio, cant, detalle.replace("; ", "\n"));
                } else if (tipo.equals("ESTUDIANTIL")) {
                    entrada = new EntradaEstudiante(cliente, evento, precio, cant, detalle.replace("; ", "\n"));
                } else {
                    entrada = new EntradaGeneral(cliente, evento, precio, cant, detalle.replace("; ", "\n"));
                }

                entrada.setIdEntrada(id);
                entrada.setPrecioFinalCalculado(precio);
                entrada.setFechaCompra(fecha);
                if (id > maxId) {
                    maxId = id;
                }

                evento.agregarEntrada(entrada);
                for (Cliente c : auditorio.getClientes()) {
                    if (c.getNombreUsuario().equals(cliente)) {
                        c.agregarEntrada(entrada);
                        break;
                    }
                }
            }
            Entrada.setContadorId(maxId + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
