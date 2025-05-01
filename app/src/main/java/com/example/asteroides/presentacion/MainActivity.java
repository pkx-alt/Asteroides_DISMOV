package com.example.asteroides.presentacion;

import static com.example.asteroides.casos_uso.CasosUsoActividades.ACTIV_JUEGO;
import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ReturnThis;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.asteroides.AlmacenPuntuaciones;
import com.example.asteroides.AlmacenPuntuacionesArray;
import com.example.asteroides.AlmacenPuntuacionesFicheroExtApl;
import com.example.asteroides.AlmacenPuntuacionesFicheroExterno;
import com.example.asteroides.AlmacenPuntuacionesFicheroInterno;
import com.example.asteroides.AlmacenPuntuacionesGSon;
import com.example.asteroides.AlmacenPuntuacionesJSON;
import com.example.asteroides.AlmacenPuntuacionesPreferencias;
import com.example.asteroides.AlmacenPuntuacionesRecursoAssets;
import com.example.asteroides.AlmacenPuntuacionesRecursoRaw;
import com.example.asteroides.AlmacenPuntuacionesSQLite;
import com.example.asteroides.AlmacenPuntuacionesSQLiteRel;
import com.example.asteroides.AlmacenPuntuacionesSW_PHP;
import com.example.asteroides.AlmacenPuntuacionesSW_PHP_AsyncTask;
import com.example.asteroides.AlmacenPuntuacionesSocket;
import com.example.asteroides.AlmacenPuntuacionesXML_SAX;
import com.example.asteroides.PreferenciasFragment;
import com.example.asteroides.R;
import com.example.asteroides.ServicioMusica;
import com.example.asteroides.casos_uso.CasosUsoActividades;

public class MainActivity extends AppCompatActivity {

    public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();
    private CasosUsoActividades usoActividades;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private SharedPreferences prefs;




    MediaPlayer mp;
    private boolean reproduciendoMusica = false;

    public static final int REQUEST_CODE_PREFERENCIAS = 2;

    private static final int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 0;


    //VOLLEY
    public static RequestQueue colaPeticiones;
    public static ImageLoader lectorImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        mp = MediaPlayer.create(this, R.raw.audio);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        colaPeticiones = Volley.newRequestQueue(this);
        lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
                public void putBitmap(String url, Bitmap bitmap){
                    cache.put(url, bitmap);
                }
                public Bitmap getBitmap(String url){
                    return cache.get(url);
                }

        });

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("musica", true)){
            startService(new Intent(MainActivity.this, ServicioMusica.class));
//            mp.start();

            reproduciendoMusica = true;
        }
        if (prefs.getString("almacenamiento", "0").equals("0")){
            almacen = new AlmacenPuntuacionesArray();
        }
        else if (prefs.getString("almacenamiento", "0").equals("1")){
            almacen = new AlmacenPuntuacionesPreferencias(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("2")){
            almacen = new AlmacenPuntuacionesFicheroInterno(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("3")){
            almacen = new AlmacenPuntuacionesFicheroExterno(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("4")){
            almacen = new AlmacenPuntuacionesFicheroExtApl(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("5")){
            almacen = new AlmacenPuntuacionesRecursoRaw(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("6")){
            almacen = new AlmacenPuntuacionesRecursoAssets(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("7")) {
            almacen = new AlmacenPuntuacionesXML_SAX(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("8")) {
            almacen = new AlmacenPuntuacionesGSon(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("9")) {
            almacen = new AlmacenPuntuacionesJSON(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("10")) {
            almacen = new AlmacenPuntuacionesSQLite(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("11")) {
            almacen = new AlmacenPuntuacionesSQLiteRel(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("12")) {
            almacen = new AlmacenPuntuacionesSocket(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("13")) {
            almacen = new AlmacenPuntuacionesSW_PHP(this);
        }
        else if (prefs.getString("almacenamiento", "0").equals("14")) {
            almacen = new AlmacenPuntuacionesSW_PHP_AsyncTask(this);
        }




        usoActividades = new CasosUsoActividades(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Button btnPreferencias = findViewById(R.id.preferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoActividades.lanzarPreferencias(v);
            }
        });

        Button btnPuntuaciones = findViewById(R.id.puntuaciones);
        btnPuntuaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoActividades.lanzarPuntuaciones(v);
            }
        });

        Button btnJugar = findViewById(R.id.jugar);
        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ServicioMusica.class));
//                mp.pause();
                usoActividades.lanzarJuego(v);
            }
        });

        TextView texto = findViewById(R.id.titulo);
        Animation animacion = AnimationUtils.loadAnimation(this,R.anim.giro_con_zoom);
        texto.startAnimation(animacion);

        Animation animacionAparecer = AnimationUtils.loadAnimation(this,R.anim.aparecer);
        btnJugar.startAnimation(animacionAparecer);

        Animation animacionDesplazamientoDer = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_derecha);
        btnPreferencias.startAnimation(animacionDesplazamientoDer);

        Button btnAcercaDe = findViewById(R.id.acercaDe);
        Animation animacionAparecerGirando = AnimationUtils.loadAnimation(this,R.anim.aparecer_girando);
        btnAcercaDe.startAnimation(animacionAparecerGirando);

        Animation animacionCaer = AnimationUtils.loadAnimation(this,R.anim.caer);
        btnPuntuaciones.startAnimation(animacionCaer);

        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAcercaDe.startAnimation(animacion);
                usoActividades.lanzarAcercaDe(v);
            }
        });




        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("musica")) {
                    boolean isMusicOn = sharedPreferences.getBoolean("musica", true);
                    if (isMusicOn) {
//                        mp.start();
                        startService(new Intent(MainActivity.this, ServicioMusica.class));
                        reproduciendoMusica = true;
                    } else {
//                        mp.pause();
                        stopService(new Intent(MainActivity.this, ServicioMusica.class));
                        reproduciendoMusica = false;
                    }
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            usoActividades.lanzarPreferencias(null);
            return true;
        }

//        if (id == R.id.action_settings) {
//            lanzarPreferencias(null);
//            return true;
//        }
//        if (id == R.id. acercaDe) {
//            lanzarAcercaDe(null);
//            return true;
//        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (reproduciendoMusica){
//            mp.start();
            startService(new Intent(MainActivity.this, ServicioMusica.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
//        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    @Override
    protected void onStop() {
//        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        stopService(new Intent(MainActivity.this, ServicioMusica.class));
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado) {
        super.onSaveInstanceState(estadoGuardado);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            estadoGuardado.putInt("posicion", pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado) {
        super.onRestoreInstanceState(estadoGuardado);
        if (estadoGuardado != null && mp != null) {
            int pos = estadoGuardado.getInt("posicion");
            mp.seekTo(pos);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIV_JUEGO && resultCode == RESULT_OK && data != null) {
            int puntuacion = data.getExtras().getInt("puntuacion");
            String nombre = "Yo";
            almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());

            usoActividades.lanzarPuntuaciones(null);
        }

        // Manejar cambios en preferencias
        if (requestCode == REQUEST_CODE_PREFERENCIAS) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getString("almacenamiento", "0").equals("0")) {
                almacen = new AlmacenPuntuacionesArray();
            } else if (prefs.getString("almacenamiento", "0").equals("1")) {
                almacen = new AlmacenPuntuacionesPreferencias(this);
            } else if (prefs.getString("almacenamiento", "0").equals("2")) {
                almacen = new AlmacenPuntuacionesFicheroInterno(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("3")) {
                almacen = new AlmacenPuntuacionesFicheroExterno(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("4")) {
                almacen = new AlmacenPuntuacionesFicheroExtApl(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("5")) {
                almacen = new AlmacenPuntuacionesRecursoRaw(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("6")) {
                almacen = new AlmacenPuntuacionesRecursoAssets(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("7")) {
                almacen = new AlmacenPuntuacionesXML_SAX(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("8")) {
                almacen = new AlmacenPuntuacionesGSon(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("9")) {
                almacen = new AlmacenPuntuacionesJSON(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("10")) {
                almacen = new AlmacenPuntuacionesSQLite(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("11")) {
                almacen = new AlmacenPuntuacionesSQLiteRel(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("12")) {
                almacen = new AlmacenPuntuacionesSocket(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("13")) {
                almacen = new AlmacenPuntuacionesSW_PHP(this);
            }
            else if (prefs.getString("almacenamiento", "0").equals("14")) {
                almacen = new AlmacenPuntuacionesSW_PHP_AsyncTask(this);
            }
        }
    }
}