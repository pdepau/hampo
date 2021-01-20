package org.example.androidthingsraspberry;

public class Lectura {
    private String temperatura;
    private String humedad;
    private String bebedero;
    private String iluminacion;
    private Long fecha;

    public Lectura(String temperatura, String humedad, String bebedero, String iluminacion, Long fecha) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.bebedero = bebedero;
        this.iluminacion = iluminacion;
        this.fecha=fecha;
    }

    public String getTemperatuar() {
        return temperatura;
    }

    public void setTemperatuar(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public String getBebedero() {
        return bebedero;
    }

    public void setBebedero(String bebedero) {
        this.bebedero = bebedero;
    }

    public String getLuminosidad() {
        return iluminacion;
    }

    public void setLuminosidad(String iluminacion) {
        this.iluminacion = iluminacion;
    }

    @Override
    public String toString() {
        return "Lectura{" +
                "temperatura='" + temperatura + '\'' +
                ", humedad='" + humedad + '\'' +
                ", bebedero='" + bebedero + '\'' +
                ", iluminacion='" + iluminacion + '\'' +
                '}';
    }
}
