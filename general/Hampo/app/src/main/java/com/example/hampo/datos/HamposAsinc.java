package com.example.hampo.datos;

import com.example.hampo.modelo.Hampo;

public interface HamposAsinc {

    interface EscuchadorElemento{
        void onRespuesta(Hampo hampo);
    }
    interface EscuchadorTamano{
        void onRespuesta(long tamano);
    }
    void elemento(String id_jaula, EscuchadorElemento escuchador);
    void anade(Hampo hampo);
    String nuevo();
    void borrar(String id_jaula);
    void actualiza(String id_jaula, Hampo hampo);
    void tamano(EscuchadorTamano escuchador);
}
