package com.example.lab2_20206156;
// nuestras variables del resultado es cubierto por si ganaste o no , o si cancelaste ,
// el tiempo que demora , el numero de intentos por iterativa y tema elegido
public class ResultActivity {
    private boolean ganado;
    private boolean cancelado;
    private long tiempo;
    private int intentos;
    private String tema;

    public ResultActivity(boolean ganado, long tiempo, int intentos, String tema) {
        this.ganado = ganado;
        this.tiempo = tiempo;
        this.intentos = intentos;
        this.tema = tema;
        this.cancelado = false;
    }

    public boolean isGanado() {
        return ganado;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public long getTiempo() {
        return tiempo;
    }

    public int getIntentos() {
        return intentos;
    }

    public String getTema() {
        return tema;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }
}