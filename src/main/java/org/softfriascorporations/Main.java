package org.softfriascorporations;


import java.io.*;
import java.net.URL;
import java.nio.file.*;

import javax.swing.*;
import java.awt.*;



public class Main extends JFrame {

    private static final String VERSION_LOCAL = "1.0.0";
    private static final String URL_VERSION = "https://raw.githubusercontent.com/Tranquilino28/AppAutoUpdate/refs/heads/master/MiApp.txt";
    private static final String URL_APP = "https://github.com/Tranquilino28/AppAutoUpdate/releases/download/v1.1.0/DemoUpdate.jar";

    private final JTextArea statusArea;

    public Main() {
        super("Actualizador de MiApp");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);

        setVisible(true);

        // Ejecuta la verificación en otro hilo para no congelar la UI
        new Thread(this::iniciarProceso).start();
    }

    private void log(String mensaje) {
        SwingUtilities.invokeLater(() -> statusArea.append(mensaje + "\n"));
    }

    private void iniciarProceso() {
        try {
            log("Versión local: " + VERSION_LOCAL);
            String versionRemota = obtenerVersionRemota();
            log("Versión remota: " + versionRemota);

            if (!VERSION_LOCAL.equals(versionRemota)) {
                log("Nueva versión disponible. Descargando...");
                descargarActualizacion();
                log("Descarga completada. Reiniciando aplicación...");
                reiniciarApp();
            } else {
                log("Aplicación actualizada. Ejecutando...");
                ejecutarApp();
            }
        } catch (Exception e) {
            log("Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String obtenerVersionRemota() throws IOException {
        URL url = new URL(URL_VERSION);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.readLine().trim();
        }
    }

    private void descargarActualizacion() throws IOException {
        URL url = new URL(URL_APP);
        Path destinoTemp = Paths.get("MiApp_temp.jar");
        Path destinoFinal = Paths.get("MiApp.jar");

        log("Descargando nueva versión...");

        try (InputStream in = url.openStream();
             OutputStream out = Files.newOutputStream(destinoTemp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[8192];
            int bytesLeidos;
            while ((bytesLeidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesLeidos);
            }
        }

        log("Reemplazando versión antigua...");
        Files.move(destinoTemp, destinoFinal, StandardCopyOption.REPLACE_EXISTING);

        log("Actualización completada correctamente.");
    }

    private void reiniciarApp() throws IOException {
        log("Reiniciando aplicación...");
        String comando = "java -jar MiApp.jar";
        Runtime.getRuntime().exec(comando);
        System.exit(0);
    }

    private void ejecutarApp() {
        log("Aquí iría tu aplicación principal (abrir menú, login, etc.)");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
