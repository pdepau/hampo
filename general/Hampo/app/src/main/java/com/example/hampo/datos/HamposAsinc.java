package com.example.hampo.datos;

import com.example.hampo.modelo.Hampo;

public interface HamposAsinc {

    interface EscuchadorElemento{
        void onRespuesta(Hampo hampo);
    }
    interface EscuchadorTamano{
        void onRespuesta(long tamano);
    }
    void elemento(String id, EscuchadorElemento escuchador);
    void anade(Hampo hampo);
    String nuevo();
    void borrar(String id);
    void actualiza(String id, Hampo hampo);
    void tamano(EscuchadorTamano escuchador);
}
