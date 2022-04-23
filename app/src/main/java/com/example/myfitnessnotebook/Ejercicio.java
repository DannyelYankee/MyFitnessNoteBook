package com.example.myfitnessnotebook;

public class Ejercicio {
    private String nombre, rutina, usuario;
    private int numRepes, numSeries, peso;

    public Ejercicio(String pNombre, String pRutina, String pUsuario, int pNumRepes, int pNumSeries, int pPeso) {
        this.nombre = pNombre;
        this.rutina = pRutina;
        this.usuario = pUsuario;
        this.numRepes = pNumRepes;
        this.numSeries = pNumSeries;
        this.peso = pPeso;
    }

    public int getNumSeries() {
        return numSeries;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutina() {
        return rutina;
    }

    public String getUsuario() {
        return usuario;
    }

    public int getPeso() {
        return peso;
    }

    public int getNumRepes() {
        return numRepes;
    }
}
