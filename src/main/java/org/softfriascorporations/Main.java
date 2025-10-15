package org.softfriascorporations;


import java.io.*;
import java.net.URL;
import java.nio.file.*;

import javax.swing.*;
import java.awt.*;


public class Main {

    static final String VERSION_LOCAL = "1.0.0";
    static final String URL_VERSION = "https://raw.githubusercontent.com/Tranquilino28/AppAutoUpdate/refs/tags/v1.1.0/MiApp.txt";
    static final String URL_APP = "https://github.com/Tranquilino28/AppAutoUpdate/releases/download/v1.1.0/DemoUpdate.jar";

    // Referencia al área de texto del JFrame
    private static JTextArea logArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::crearVentana);
        new Thread(Main::iniciarProceso).start(); // ejecuta la lógica sin bloquear el GUI
    }

    private static void crearVentana() {
        JFrame frame = new JFrame("Actualizador de MiApp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(logArea);
        frame.add(scroll);

        frame.setVisible(true);
        agregarLog("Iniciando verificación de actualizaciones...\n");
    }

    private static void iniciarProceso() {
        try {
            agregarLog("Versión local: " + VERSION_LOCAL);

            String versionRemota = obtenerVersionRemota();
            agregarLog("Versión remota: " + versionRemota);

            if (!VERSION_LOCAL.equals(versionRemota)) {
                agregarLog("Nueva versión disponible.\nDescargando actualización...");
                descargarActualizacion();
                agregarLog("Descarga completada.");
                agregarLog("Reiniciando aplicación...");
                reiniciarApp();
            } else {
                agregarLog("La aplicación ya está actualizada.");
                agregarLog("Ejecutando la app normalmente...");
                ejecutarApp();
            }

        } catch (Exception e) {
            agregarLog("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String obtenerVersionRemota() throws IOException {
        URL url = new URL(URL_VERSION);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.readLine().trim();
        }
    }

    private static void descargarActualizacion() throws IOException {
        URL url = new URL(URL_APP);
        Path destino = Paths.get("MiApp_nueva.jar");

        try (InputStream in = url.openStream();
             OutputStream out = Files.newOutputStream(destino, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[8192];
            int bytesLeidos;
            while ((bytesLeidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesLeidos);
            }
        }
    }

    private static void reiniciarApp() throws IOException {
        Runtime.getRuntime().exec("java -jar MiApp_nueva.jar");
        agregarLog("Reinicio completado, cerrando actualizador...");
        System.exit(0);
    }

    private static void ejecutarApp() {
        agregarLog("Aquí puedes lanzar tu JFrame principal real...");
        // Ejemplo: new VentanaPrincipal().setVisible(true);
    }

    private static void agregarLog(String texto) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(texto + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
