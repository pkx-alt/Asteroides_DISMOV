package com.example.asteroides;

public class Puntuacion {
    private int puntos;
    private String nombre;
    private long fecha;

    public Puntuacion(int puntos, String nombre, long fecha) {
        this.puntos = puntos;
        this.nombre = nombre;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
