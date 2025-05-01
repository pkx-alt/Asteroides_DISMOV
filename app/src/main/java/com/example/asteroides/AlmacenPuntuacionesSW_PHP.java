package com.example.asteroides;

import android.util.Log;

import com.example.asteroides.presentacion.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesSW_PHP implements AlmacenPuntuaciones {
    public AlmacenPuntuacionesSW_PHP(MainActivity mainActivity) {
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        HttpURLConnection conexion = null;

        try {
            URL url = new URL("http://samuelmoralesont.atwebpages.com/puntuaciones/lista.php" );

            Log.d("URL", url.toString());

            conexion = (HttpURLConnection) url.openConnection();

            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                while (!linea.equals("")) {
                    result.add(linea);
                    linea = reader.readLine();
                }
                reader.close();
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        } finally {
            if (conexion != null) conexion.disconnect();
        }
        return result;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        HttpURLConnection conexion = null;
        try {
            URL url = new URL("http://samuelmoralesont.atwebpages.com/puntuaciones/nueva.php?"
                    + "puntos=" + puntos
                    + "&nombre=" + URLEncoder.encode(nombre, "UTF-8")
                    + "&fecha=" + fecha);
            conexion = (HttpURLConnection) url.openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                if (!linea.equals("OK")) {
                    Log.e("Asteroides", "Error en servicio Web nueva");
                }
                reader.close();
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }

        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        } finally {
            if (conexion != null) conexion.disconnect();
        }
    }

}

