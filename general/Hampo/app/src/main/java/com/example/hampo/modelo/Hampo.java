package com.example.hampo.modelo;

public class Hampo {
    private String nombre;
    private String uriFoto;
    private String idUser;
    private String raza;
    private String sex;

    public Hampo(){}

    public Hampo(String nombre, String uriFoto,  String idUser, String raza, String sex) {
        this.nombre = nombre;
        this.uriFoto = uriFoto;
        this.idUser = idUser;
        this.raza = raza;
        this.sex = sex;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUriFoto() {
        return uriFoto;
    }

    public void setUriFoto(String uriFoto) {
        this.uriFoto = uriFoto;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
