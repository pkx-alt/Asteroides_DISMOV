package com.example.asteroides;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesFicheroExterno implements AlmacenPuntuaciones {
    private static String FICHERO = "puntuaciones.txt";
    private Context context;

    public AlmacenPuntuacionesFicheroExterno(Context context) {
        this.context = context;
    }

    // Método para verificar si hay una SD física
    private File obtenerDirectorioSDReal() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String path = prefs.getString("directorioSD", null);
        if (path != null) {
            File dir = new File(path);
            if (dir.exists() && dir.canWrite()) {
                return dir;
            }
        }

        // Si no hay selección guardada, se usa la detección automática como respaldo
        File[] dirs = context.getExternalFilesDirs(null);
        for (File dir : dirs) {
            if (dir != null && Environment.isExternalStorageRemovable(dir) &&
                    Environment.getExternalStorageState(dir).equals(Environment.MEDIA_MOUNTED)) {
                return dir;
            }
        }
        return null;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        File sdDir = obtenerDirectorioSDReal();
        if (sdDir == null) {
            Toast.makeText(context, "No hay tarjeta SD conectada", Toast.LENGTH_LONG).show();
            return;
        }

        String estadoSD = Environment.getExternalStorageState(sdDir);
        if (!estadoSD.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "No puedo escribir en la tarjeta SD", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            File file = new File(sdDir, FICHERO);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream f = new FileOutputStream(file, true); // Modo append
            String texto = puntos + " " + nombre + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", "Error al guardar puntuación", e);
        }
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<>();
        File sdDir = obtenerDirectorioSDReal();
        if (sdDir == null) {
            Toast.makeText(context, "No hay tarjeta SD conectada", Toast.LENGTH_LONG).show();
            return result;
        }

        String estadoSD = Environment.getExternalStorageState(sdDir);
        if (!estadoSD.equals(Environment.MEDIA_MOUNTED) &&
                !estadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, "No puedo leer desde la tarjeta SD", Toast.LENGTH_LONG).show();
            return result;
        }

        try {
            File file = new File(sdDir, FICHERO);
            if (!file.exists()) {
                Log.e("Asteroides", "El archivo no existe, no se pueden leer puntuaciones.");
                return result;
            }

            FileInputStream f = new FileInputStream(file);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(f));

            int n = 0;
            String linea;
            while (n < cantidad && (linea = entrada.readLine()) != null) {
                result.add(linea);
                n++;
            }
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", "Error al leer puntuaciones", e);
        }
        return result;
    }
}
