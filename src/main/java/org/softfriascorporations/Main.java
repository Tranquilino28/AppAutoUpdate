package org.softfriascorporations;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;


public class Main {

    static final String VERSION_LOCAL = "1.0.0";

    // 🔹 URL donde guardaste version.txt en GitHub
    static final String URL_VERSION = "https://github.com/Tranquilino28/AppAutoUpdate/blob/800df050489b08aedfb1a93ec340dd5d2aa09476/MiApp.txt";

    // 🔹 URL del último release de tu app (el JAR)
    static final String URL_APP = "https://github.com/Tranquilino28/AppAutoUpdate/blob/800df050489b08aedfb1a93ec340dd5d2aa09476/DemoUpdate.jar";

    public static void main(String[] args) {
        try {
            System.out.println("Versión local: " + VERSION_LOCAL);

            String versionRemota = obtenerVersionRemota();
            System.out.println("Versión remota: " + versionRemota);

            if (!VERSION_LOCAL.equals(versionRemota)) {
                System.out.println("Nueva versión disponible. Descargando actualización...");
                descargarActualizacion();
                reiniciarApp();
            } else {
                System.out.println("App actualizada. Ejecutando...");
                // Aquí va el resto del código de tu app
            }

        } catch (Exception e) {
            System.err.println("Error al actualizar: " + e.getMessage());
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

        System.out.println("Descarga completada → " + destino.toAbsolutePath());
    }

    private static void reiniciarApp() throws IOException {
        // Lanza el nuevo jar y cierra el actual
        Runtime.getRuntime().exec("java -jar MiApp_nueva.jar");
        System.out.println("Reiniciando aplicación...");
        System.exit(0);
    }
}
