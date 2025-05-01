package com.example.asteroides;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesFicheroExtApl implements AlmacenPuntuaciones {
    private static final String CARPETA = "Puntuaciones";
    private static final String FICHERO = "puntuaciones.txt";
    private final Context context;

    public AlmacenPuntuacionesFicheroExtApl(Context context) {
        this.context = context;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        if (!isExternalStorageWritable()) {
            Log.e("Asteroides", "Almacenamiento externo no disponible para escribir.");
            return;
        }

        File directorio = new File(context.getExternalFilesDir(null), CARPETA); // Carpeta personalizada
        if (!directorio.exists() && !directorio.mkdirs()) {
            Log.e("Asteroides", "No se pudo crear la carpeta de puntuaciones.");
            return;
        }

        File file = new File(directorio, FICHERO);
        try (FileOutputStream f = new FileOutputStream(file, true);
             OutputStreamWriter osw = new OutputStreamWriter(f);
             BufferedWriter writer = new BufferedWriter(osw)) {

            String texto = puntos + " " + nombre + "\n";
            writer.write(texto);

        } catch (IOException e) {
            Log.e("Asteroides", "Error al guardar puntuación", e);
        }
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<>();

        if (!isExternalStorageReadable()) {
            Log.e("Asteroides", "Almacenamiento externo no disponible para leer.");
            return result;
        }

        File directorio = new File(context.getExternalFilesDir(null), CARPETA); // Carpeta personalizada
        File file = new File(directorio, FICHERO);

        if (!file.exists()) {
            Log.e("Asteroides", "El archivo de puntuaciones no existe.");
            return result;
        }

        try (FileInputStream f = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(f);
             BufferedReader reader = new BufferedReader(isr)) {

            String linea;
            int n = 0;
            while ((n < cantidad) && ((linea = reader.readLine()) != null)) {
                result.add(linea);
                n++;
            }

        } catch (IOException e) {
            Log.e("Asteroides", "Error al leer puntuaciones", e);
        }

        return result;
    }

    // Comprueba si el almacenamiento externo está disponible para escritura
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Comprueba si el almacenamiento externo está disponible para lectura
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY));
    }
}
