package com.example.hampo;

public class Hampo {
    private String nombre;
    private int imagenId;
    private String comida;
    private String bebida;
    private String temperatura;

    public Hampo(String nombre, int imagenId, String comida, String bebida, String temperatura) {
        this.nombre = nombre;
        this.imagenId = imagenId;
        this.comida = comida;
        this.bebida = bebida;
        this.temperatura = temperatura;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getImagenId() {
        return imagenId;
    }

    public void setImagenId(int imagenId) {
        this.imagenId = imagenId;
    }

    public String getComida() {
        return comida;
    }

    public void setComida(String comida) {
        this.comida = comida;
    }

    public String getBebida() {
        return bebida;
    }

    public void setBebida(String bebida) {
        this.bebida = bebida;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }
}
