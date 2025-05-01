package com.example.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.PreferenceManager;

import com.example.asteroides.presentacion.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class VistaJuego extends View implements SensorEventListener {

    // //// NAVE //////
    private Grafico nave; // Gráfico de la nave

    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // Aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20;
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    // ASTEROIDES
    private List<Grafico> asteroides; // Lista con los asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide

    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;

    // ///// MISIL //////
    private static int PASO_VELOCIDAD_MISIL = 12;
    private ArrayList<Grafico> misiles;
    private Drawable dMisilPref;
    private ArrayList<Integer> tiempoMisiles;

    // ///// SENSORES ////////
    private SensorManager mSensorManager;
    List<Sensor> listSensors2;
    List<Sensor> listSensors;

    private boolean opcionSensores = false;

    private boolean reproduciendoEfectos = false;

    public boolean isOpcionSensores() {
        return opcionSensores;
    }


    // ///// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;
    private ShapeDrawable dNave = null;


    //PUNTUACIÓN
    private int puntuacion = 0;

    private Drawable drawableAsteroide[] = new Drawable[3];
    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);


        mSensorManager = (SensorManager) context.getSystemService(
                Context.SENSOR_SERVICE);

       listSensors2 = mSensorManager.getSensorList(
                Sensor.TYPE_ORIENTATION);

       listSensors = mSensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER);

        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(getContext());


        numFragmentos = Integer.parseInt(pref.getString("fragmentos", "3"));

        if (pref.getString("entrada", "1").equals("2"))
        {
            activarSensores();
            opcionSensores = true;
        }

        if (pref.getBoolean("efectos", true))
        {
            reproduciendoEfectos = true;
        }


        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);

        Drawable drawableNave, drawableMisil;


        if (pref.getString("graficos", "1").equals("0")) {
            Path pathAsteroide = new Path();
            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;

            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.6, (float) 1.0);
            pathAsteroide.lineTo((float) 0.5, (float) 0.8);
            pathAsteroide.lineTo((float) 0.2, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.2, (float) 0.4);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);

            for (int i=0; i<3; i++)
            {
                ShapeDrawable dAsteroide = new ShapeDrawable(
                        new PathShape(pathAsteroide, 1, 1));

                dAsteroide.getPaint().setColor(Color.WHITE);
                dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
                dAsteroide.setIntrinsicWidth(50 - i*14);
                dAsteroide.setIntrinsicHeight(50 -i*14);

                drawableAsteroide[i] = dAsteroide;
            }


            setBackgroundColor(Color.BLACK);

            crearNave();
            drawableNave = dNave;
            nave = new Grafico(this, drawableNave);

        } else if (pref.getString("graficos", "1").equals(("1"))) {
            drawableAsteroide[0] = AppCompatResources.getDrawable(context, R.drawable.asteroide1);
            drawableAsteroide[1] = AppCompatResources.getDrawable(context, R.drawable.asteroide2);
            drawableAsteroide[2] = AppCompatResources.getDrawable(context, R.drawable.asteroide3);
            drawableMisil = AppCompatResources.getDrawable(context, R.drawable.misil1);

            drawableNave = AppCompatResources.getDrawable(context, R.drawable.nave);
            nave = new Grafico(this, drawableNave);
            nave.setAlto(120);
            nave.setAncho(120);
            nave.setRadioColision(40);
        }

        else {
            drawableAsteroide[0] = AppCompatResources.getDrawable(context, R.drawable.asteroide1);
            drawableAsteroide[1] = AppCompatResources.getDrawable(context, R.drawable.asteroide2);
            drawableAsteroide[2] = AppCompatResources.getDrawable(context, R.drawable.asteroide3);
            drawableNave = AppCompatResources.getDrawable(context, R.drawable.nave_vector);
            nave = new Grafico(this, drawableNave);
            nave.setAlto(120);
            nave.setAncho(120);
            nave.setRadioColision(40);

            drawableMisil = AppCompatResources.getDrawable(context, R.drawable.misil1);
        }

        dMisilPref = drawableMisil;

        asteroides = new ArrayList<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide[0]);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }
    }

    @Override
    protected void onSizeChanged(int ancho, int alto,
                                 int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vez que conocemos nuestro ancho y alto.
        for (Grafico asteroide : asteroides) {
            do {
                asteroide.setCenX((int) (Math.random() * ancho));
                asteroide.setCenY((int) (Math.random() * alto));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }

        nave.setCenX(ancho / 2);
        nave.setCenY(alto / 2);
        ultimoProceso = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (asteroides)
        {
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }
        nave.dibujaGrafico(canvas);

        if (misiles != null)
        {
            for (Grafico misil: misiles) {
                misil.dibujaGrafico(canvas);
            }
        }
    }

    public void crearNave() {
        Path pathNave = new Path();
        pathNave.moveTo(0, 0);
        pathNave.lineTo(1, 0.5f);
        pathNave.moveTo(1, 0.5f);
        pathNave.lineTo(0, 1);
        pathNave.moveTo(0, 1);
        pathNave.lineTo(0, 0);


        dNave = new ShapeDrawable(new PathShape(pathNave, 1, 1));
        dNave.getPaint().setColor(Color.WHITE);
        dNave.getPaint().setStyle(Paint.Style.STROKE);
        dNave.setIntrinsicWidth(20);
        dNave.setIntrinsicHeight(15);
    }

     protected void actualizaFisica(){
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return; // Salir si el período de proceso no se ha cumplido.
        }

        // Para una ejecución en tiempo real calculamos el factor de movimiento
        double factorMov = (ahora - ultimoProceso) / (double) PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez

        // Actualizamos velocidad y dirección de la nave a partir de
        // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * factorMov));

        double nIncX = nave.getIncX() + aceleracionNave *
                Math.cos(Math.toRadians(nave.getAngulo())) * factorMov;
        double nIncY = nave.getIncY() + aceleracionNave *
                Math.sin(Math.toRadians(nave.getAngulo())) * factorMov;

        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX, nIncY) <= MAX_VELOCIDAD_NAVE) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }

        nave.incrementaPos(factorMov); // Actualizamos posición

        for (Grafico asteroide : asteroides) {
            asteroide.incrementaPos(factorMov);
        }

         // Actualizamos posición de misil
         //VAMOS A UTILIZAR UN CICLO FOR AQUÍ

         if (misiles != null) {
             for (int m = misiles.size() - 1; m >= 0; m--) {  // Recorrer de atrás hacia adelante para evitar errores al eliminar
                 misiles.get(m).incrementaPos(factorMov);
                 tiempoMisiles.set(m, (int) (tiempoMisiles.get(m) - factorMov));

                 // Eliminar el misil si su tiempo de vida ha terminado
                 if (tiempoMisiles.get(m) < 0) {
                     misiles.remove(m);
                     tiempoMisiles.remove(m);
                     continue;
                 }

                 // Verificar colisión con asteroides
                 for (int i = 0; i < asteroides.size(); i++) {
                     if (misiles.get(m).verificaColision(asteroides.get(i))) {
                         destruyeAsteroide(i);
                         misiles.remove(m);
                         tiempoMisiles.remove(m);
                         break; // Romper el ciclo ya que el misil fue eliminado
                     }
                 }
             }
         }

//         if (misilActivo) {
//             misil.incrementaPos(factorMov);
//             tiempoMisil -= factorMov;
//             if (tiempoMisil < 0) {
//                 misilActivo = false;
//             } else {
//                 for (int i = 0; i < asteroides.size(); i++) {
//                     if (misil.verificaColision(asteroides.get(i))) {
//                         destruyeAsteroide(i);
//                         break;
//                     }
//                 }
//             }
//         }

         for (Grafico asteroide: asteroides){
             if (asteroide.verificaColision(nave)){
                 Log.d("Colision", "Colisión entre nave y asteroide");
                 salir();
             }
         }

     }

//    class ThreadJuego extends Thread {
//        @Override
//        public void run() {
//            while (true) {
//                actualizaFisica();
//            }
//        }
//    }

    class ThreadJuego extends Thread {
        private boolean pausa, corriendo;

        public synchronized void pausar() {
            pausa = true;
        }

        public synchronized void reanudar() {
            pausa = false;
            notify();
        }

        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }

        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }




    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        SharedPreferences preferencia = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        if (preferencia.getString("entrada","1").equals("0"))
        {
            super.onKeyDown(codigoTecla, evento);
            // Suponemos que vamos a procesar la pulsación
            boolean procesada = true;
            switch (codigoTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    aceleracionNave = +PASO_ACELERACION_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    giroNave = -PASO_GIRO_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    giroNave = +PASO_GIRO_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                activaMisil();
                    break;
                default:
                    // Si estamos aquí, no hay pulsación que nos interese
                    procesada = false;
                    break;
            }
            return procesada;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        SharedPreferences preferencia = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        if (preferencia.getString("entrada","1").equals("0"))
        {
            super.onKeyUp(codigoTecla, evento);
            // Suponemos que vamos a procesar la pulsación
            boolean procesada = true;
            switch (codigoTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    aceleracionNave = 0;

                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    giroNave = 0;
                    break;
                default:
                    // Si estamos aquí, no hay pulsación que nos interese
                    procesada = false;
                    break;
            }
            return procesada;
        }
        return false;
    }

    private float mX=0, mY=0;
    private boolean disparo=false;

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        SharedPreferences preferencia = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        if (preferencia.getString("entrada","1").equals("1"))
        {
            super.onTouchEvent(event);
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    disparo=true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dy < 6 && dx > 6) {
                        giroNave = Math.round((x - mX) / 2);
                        disparo = false;
                    } else if (dx < 6 && dy > 6) {
                        aceleracionNave = Math.round((mY - y) / 25);
                        disparo = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    giroNave = 0;
                    aceleracionNave = 0;
                    if (disparo) {
                    activaMisil();
                    }
                    break;
            }
            mX = x;
            mY = y;
            return true;
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private boolean hayValorInicial = false;
    private float valorInicial;

    private float umbralInclinacion = 7000;

    @Override
    public void onSensorChanged(SensorEvent event) {
        SharedPreferences preferencia = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        if (preferencia.getString("entrada","1").equals("2"))
        {
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
            {
                float valor = event.values[1];

                if (!hayValorInicial) {
                    valorInicial = valor;
                    hayValorInicial = true;
                }
                giroNave = (int) (valorInicial - valor) / 3;
            }

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float yActual = event.values[2] * 1000;

                if (Math.abs(yActual) > umbralInclinacion) {
                    aceleracionNave = PASO_ACELERACION_NAVE;
                } else {
                    aceleracionNave = 0;
                }
            }


        }
    }

    private void destruyeAsteroide(int i) {
        int tam=0;
        if(asteroides.get(i).getDrawable() != drawableAsteroide[2]) {
            if(asteroides.get(i).getDrawable() == drawableAsteroide[1]) {
                tam = 2;
            } else {
                tam = 1;
            }
        }

        for(int n = 0; n < numFragmentos; n++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide[tam]);
            asteroide.setCenX(asteroides.get(i).getCenX());
            asteroide.setCenY(asteroides.get(i).getCenY());
            asteroide.setIncX(Math.random() * 7 - 2 - tam);
            asteroide.setIncY(Math.random() * 7 - 2 - tam);
            asteroide.setAngulo((int)(Math.random() * 360));
            asteroide.setRotacion((int)(Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }

        synchronized (asteroides)
        {
            asteroides.remove(i);
            if (reproduciendoEfectos){
                soundPool.play(idExplosion, 1, 1, 1, 0, 1);
            }

            puntuacion+=1000;
        }

        if (asteroides.isEmpty())
        {
            salir();
        }
        this.postInvalidate();
    }

    private void activaMisil() {
        if (misiles == null) {
            misiles = new ArrayList<>();
            tiempoMisiles = new ArrayList<>();
        }
        Drawable drawableMisil = dMisilPref;
        Grafico nuevoMisil = new Grafico(this, drawableMisil);
        nuevoMisil.setCenX(nave.getCenX());
        nuevoMisil.setCenY(nave.getCenY());
        nuevoMisil.setAngulo(nave.getAngulo());
        nuevoMisil.setIncX(Math.cos(Math.toRadians(nuevoMisil.getAngulo())) * PASO_VELOCIDAD_MISIL);
        nuevoMisil.setIncY(Math.sin(Math.toRadians(nuevoMisil.getAngulo())) * PASO_VELOCIDAD_MISIL);

        int tiempo = (int) Math.min(
                this.getWidth() / Math.abs(nuevoMisil.getIncX()),
                this.getHeight() / Math.abs(nuevoMisil.getIncY())
        ) - 2;
        if (reproduciendoEfectos){
            soundPool.play(idDisparo, 1, 1, 1, 0, 2);
        }
        misiles.add(nuevoMisil);
        tiempoMisiles.add(tiempo);
    }

    public ThreadJuego getThread() {
        return thread;
    }

    protected void activarSensores()
    {
        if (!listSensors.isEmpty()) {
            Sensor accelerometer = listSensors.get(0);
            mSensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (!listSensors2.isEmpty()) {
            Sensor orientationSensor = listSensors2.get(0);
            mSensorManager.registerListener(this, orientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        Log.d("Sensores", "Sensores activados");
    }

    protected void desactivarSensores()
    {
        mSensorManager.unregisterListener(this);
        Log.d("Sensores", "Sensores desactivados");
    }

    private Activity padre;
    public void setPadre(Activity padre){
        this.padre = padre;
    }
    private void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }

}
