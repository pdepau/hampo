package com.example.hampo.datos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hampo.modelo.Hampo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HamposFirestore implements HamposAsinc {
    private CollectionReference hampos;
    public HamposFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        hampos = db.collection("Hampos");
    }
    public void elemento(String id, final EscuchadorElemento escuchador) {
        hampos.document(id).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Hampo hampo = task.getResult().toObject(Hampo.class);
                            escuchador.onRespuesta(hampo);
                        } else {
                            Log.e("Firebase", "Error al leer", task.getException());
                            escuchador.onRespuesta(null);
                        }
                    }
                });
    }
    public void anade(Hampo hampo) {
        hampos.document().set(hampo); //o lugares.add(lugar);
    }
    public String nuevo() {
        return hampos.document().getId();
    }
    public void borrar(String id) {
        hampos.document(id).delete();
    }
    public void actualiza(String id, Hampo hampo) {
        hampos.document(id).set(hampo);
    }
    public void tamano(final EscuchadorTamano escuchador) {
        hampos.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            escuchador.onRespuesta(task.getResult().size());
                        } else {
                            Log.e("Firebase","Error en tamaño",task.getException());
                            escuchador.onRespuesta(-1);
                        }
                    }
                });
    }
}
