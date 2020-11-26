package org.example.androidthingsraspberry;

import com.fasterxml.jackson.annotation.JsonProperty;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */
public class SensorsData {
    @JsonProperty("Temperatura")
    public String temperatura;
    @JsonProperty("Humedad")
    public String humedad;
    @JsonProperty("Movimiento")
    public String movimiento;
    @JsonProperty("Luminosidad")
    public String luminosidad;
    @JsonProperty("BebederoCm")
    public String bebederoCm;
    @JsonProperty("ComederoCm")
    public String comederoCm;

    public SensorsData(String temperatura, String humedad, String movimiento, String luminosidad, String bebederoCm, String comederoCm) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.movimiento = movimiento;
        this.luminosidad = luminosidad;
        this.bebederoCm = bebederoCm;
        this.comederoCm = comederoCm;
    }

    public SensorsData(){

    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public String getLuminosidad() {
        return luminosidad;
    }

    public void setLuminosidad(String luminosidad) {
        this.luminosidad = luminosidad;
    }

    public String getBebederoCm() {
        return bebederoCm;
    }

    public void setBebederoCm(String bebederoCm) {
        this.bebederoCm = bebederoCm;
    }

    public String getComederoCm() {
        return comederoCm;
    }

    public void setComederoCm(String comederoCm) {
        this.comederoCm = comederoCm;
    }

    @Override
    public String toString() {
        return "Datos{" +
                "temperatura='" + temperatura + '\'' +
                ", humedad='" + humedad + '\'' +
                ", movimiento='" + movimiento + '\'' +
                ", luminosidad='" + luminosidad + '\'' +
                ", bebederoCm='" + bebederoCm + '\'' +
                ", comederoCm='" + comederoCm + '\'' +
                '}';
    }
}


