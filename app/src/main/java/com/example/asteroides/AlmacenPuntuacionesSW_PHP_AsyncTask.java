package com.example.asteroides;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.asteroides.presentacion.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AlmacenPuntuacionesSW_PHP_AsyncTask implements AlmacenPuntuaciones {

    private Context contexto;
    public AlmacenPuntuacionesSW_PHP_AsyncTask(Context contexto) {
        this.contexto = contexto;
    }

    public List<String> listaPuntuaciones(int cantidad) {
        try {
            TareaLista tarea = new TareaLista();
            tarea.execute(cantidad);
            return tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(contexto, "Tiempo excedido al conectar", Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(contexto, "Error al conectar con servidor", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(contexto, "Error con tarea asíncrona", Toast.LENGTH_LONG).show();
        }
        return new ArrayList<String>();
    }

    private class TareaLista extends AsyncTask<Integer, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Integer... cantidad) {
            // Este es el código que antes estaba en listaPuntuaciones():
            List<String> result = new ArrayList<String>();
            HttpURLConnection conexion = null;

            try {
                URL url = new URL("http://samuelmoralesont.atwebpages.com/puntuaciones/lista.php" +"?max="+cantidad[0]);

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
                    cancel(true);
                }
            } catch (Exception e) {
                Log.e("Asteroides", e.getMessage(), e);
                cancel(true);
            } finally {
                if (conexion != null) conexion.disconnect();
            }
            return result;
        }
    }


    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        try {
            TareaGuardar tarea = new TareaGuardar();
            tarea.execute(String.valueOf(puntos), nombre, String.valueOf(fecha));
            tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(contexto, "Tiempo excedido al conectar", Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(contexto, "Error al conectar con servidor", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(contexto, "Error con tarea asíncrona", Toast.LENGTH_LONG).show();
        }
    }

    private class TareaGuardar extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            HttpURLConnection conexion = null;
            try {

                URL url = new URL("http://samuelmoralesont.atwebpages.com/puntuaciones/nueva.php"
                        + "?puntos=" + param[0]
                        + "&nombre=" + URLEncoder.encode(param[1], "UTF-8")
                        + "&fecha=" + param[2]);
                conexion = (HttpURLConnection) url.openConnection();
                if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conexion.getInputStream()));
                    String linea = reader.readLine();
                    if (!linea.equals("OK")) {
                        Log.e("Asteroides", "Error en servicio Web nueva");
                        cancel(true);
                    }
                    reader.close();
                } else {
                    Log.e("Asteroides", conexion.getResponseMessage());
                    cancel(true);
                }

            } catch (Exception e) {
                Log.e("Asteroides", e.getMessage(), e);
                cancel(true);
            } finally {
                if (conexion != null) conexion.disconnect();
            }
            return null;
        }
    }

}

