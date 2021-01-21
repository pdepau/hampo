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
        this.fecha = fecha;
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

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getIluminacion() {
        return iluminacion;
    }

    public void setIluminacion(String iluminacion) {
        this.iluminacion = iluminacion;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Lectura{" +
                "temperatura='" + temperatura + '\'' +
                ", humedad='" + humedad + '\'' +
                ", bebedero='" + bebedero + '\'' +
                ", iluminacion='" + iluminacion + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
