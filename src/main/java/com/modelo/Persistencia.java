package com.modelo;

import java.io.*;

public class Persistencia {

    private static final String ARCHIVO_EVENTOS = "eventos.txt";
    private static final String ARCHIVO_CLIENTES = "clientes.txt";
    private static final String ARCHIVO_ENTRADAS = "entradas.txt";

    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;

    public static void guardarDatos(Auditorio auditorio) {
        guardarEventos(auditorio);
        guardarClientes(auditorio);
        guardarEntradas(auditorio);
        System.out.println("[Persistencia] Datos guardados correctamente.");
    }

    private static void guardarEventos(Auditorio auditorio) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            if (auditorio.getEventoArrayList() != null) {
                for (Evento evento : auditorio.getEventoArrayList()) {
                    pw.println("EVENTO|" + evento.getNombre() + "|" + evento.getFecha()
                            + "|" + evento.getPrecioBase());

                    int[][] matriz = evento.getMatrizAsientos();
                    for (int i = 0; i < FILAS; i++) {
                        StringBuilder fila = new StringBuilder();
                        for (int j = 0; j < COLUMNAS; j++) {
                            fila.append(matriz[i][j]);
                            if (j < COLUMNAS - 1) {
                                fila.append(",");
                            }
                        }
                        pw.println(fila.toString());
                    }
                    pw.println("---");
                }
            }
        } catch (IOException e) {
            System.err.println("[Persistencia] Error al guardar eventos: " + e.getMessage());
        }
    }

    private static void guardarClientes(Auditorio auditorio) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES))) {
            if (auditorio.getClientes() != null) {
                for (ClienteModel cliente : auditorio.getClientes()) {
                    pw.println(cliente.getNombreUsuario() + "|" + cliente.getContra());
                }
            }
        } catch (IOException e) {
            System.err.println("[Persistencia] Error al guardar clientes: " + e.getMessage());
        }
    }

    private static void guardarEntradas(Auditorio auditorio) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_ENTRADAS))) {
            if (auditorio.getClientes() != null) {
                for (ClienteModel cliente : auditorio.getClientes()) {
                    if (cliente.getEntradas() != null) {
                        for (Entrada entrada : cliente.getEntradas()) {
                            // TIPO|ID|NOMBRE_CLIENTE|NOMBRE_EVENTO|PRECIO|CANTIDAD|DETALLE
                            pw.println(
                                    entrada.tipoEntrada() + "|" +
                                            entrada.getIdEntrada() + "|" +
                                            entrada.getNombreCliente() + "|" +
                                            entrada.getEvento().getNombre() + "|" +
                                            entrada.calcularPrecio() + "|" +
                                            entrada.getCantidadAsientos() + "|" +
                                            entrada.getDetalleAsientos().replace("\n", "\\n"));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Persistencia] Error al guardar entradas: " + e.getMessage());
        }
    }

    public static void cargarDatos(Auditorio auditorio) {
        cargarClientes(auditorio);
        cargarEventos(auditorio);
        cargarEntradas(auditorio);
        System.out.println("[Persistencia] Datos cargados correctamente.");
    }

    private static void cargarClientes(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty())
                    continue;
                String[] partes = linea.split("\\|");
                if (partes.length == 2) {
                    ClienteModel cliente = new ClienteModel(partes[0], partes[1]);
                    auditorio.agregarCliente(cliente);
                }
            }
        } catch (IOException e) {
            System.err.println("[Persistencia] Error al cargar clientes: " + e.getMessage());
        }
    }

    private static void cargarEventos(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_EVENTOS);
        if (!archivo.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            Evento eventoActual = null;
            int filaMatriz = 0;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty())
                    continue;

                if (linea.startsWith("EVENTO|")) {
                    String[] partes = linea.split("\\|");
                    if (partes.length == 4) {
                        String nombre = partes[1];
                        String fecha = partes[2];
                        double precio = Double.parseDouble(partes[3]);
                        eventoActual = new Evento(nombre, fecha, precio);
                        filaMatriz = 0;
                        auditorio.agregarEvento(eventoActual);
                    }
                } else if (linea.equals("---")) {
                    eventoActual = null;
                    filaMatriz = 0;
                } else if (eventoActual != null && filaMatriz < FILAS) {
                    String[] celdas = linea.split(",");
                    for (int j = 0; j < Math.min(celdas.length, COLUMNAS); j++) {
                        eventoActual.getMatrizAsientos()[filaMatriz][j] = Integer.parseInt(celdas[j].trim());
                    }
                    filaMatriz++;
                }
            }
        } catch (IOException e) {
            System.err.println("[Persistencia] Error al cargar eventos: " + e.getMessage());
        }
    }

    private static void cargarEntradas(Auditorio auditorio) {
        File archivo = new File(ARCHIVO_ENTRADAS);
        if (!archivo.exists())
            return;

        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty())
                    continue;

                String[] partes = linea.split("\\|", 7);
                if (partes.length < 7)
                    continue;

                String tipo = partes[0];
                int idEntrada = Integer.parseInt(partes[1]);
                String nombreCliente = partes[2];
                String nombreEvento = partes[3];
                double precio = Double.parseDouble(partes[4]);
                int cantidad = Integer.parseInt(partes[5]);
                String detalle = partes[6].replace("\\n", "\n");

                Evento evento = null;
                for (Evento e : auditorio.getEventoArrayList()) {
                    if (e.getNombre().equals(nombreEvento)) {
                        evento = e;
                        break;
                    }
                }
                if (evento == null)
                    continue; // Evento no encontrado, saltar entrada

                Entrada entrada;
                switch (tipo) {
                    case "VIP":
                        entrada = new EntradaVip(nombreCliente, evento, precio, cantidad, detalle);
                        break;
                    case "ESTUDIANTIL":
                        entrada = new EntradaEstudiante(nombreCliente, evento, precio, cantidad, detalle);
                        break;
                    default:
                        entrada = new EntradaGeneral(nombreCliente, evento, precio, cantidad, detalle);
                        break;
                }

                // Establecer ID real desde archivo
                entrada.setIdEntrada(idEntrada);
                entrada.setPrecioFinalCalculado(precio);

                if (idEntrada > maxId) {
                    maxId = idEntrada;
                }

                evento.agregarEntrada(entrada);
                for (ClienteModel cliente : auditorio.getClientes()) {
                    if (cliente.getNombreUsuario().equals(nombreCliente)) {
                        cliente.agregarEntrada(entrada);
                        break;
                    }
                }
            }

            // Actualizar contador estático para futuras entradas
            Entrada.setContadorId(maxId + 1);

        } catch (IOException e) {
            System.err.println("[Persistencia] Error al cargar entradas: " + e.getMessage());
        }
    }
}
