package org.example.androidthingsraspberry;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseDataController {
    //Cuando se realiza la vinculación recibo por MQTT el id del usuario asociado a la jaula
    private String idJaula = "taDVh1M7JolXGXCDziQn";
    private String idUser = "zL8YlNGe51MZyX8VbNMM0vzyI7l1";
    private FirebaseFirestore db;
    private DocumentReference jaula;

    public FirebaseDataController() {
        this.db = FirebaseFirestore.getInstance();
        this.jaula = db.collection(idUser).document(idJaula);
    }

    public void sendSensorsData(SensorsData sensorsData) {
        jaula.collection("datosSensores").add(sensorsData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("sensorsData", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("sensorsData", "Error adding document", e);
                    }
                });
    }

    //Obtiene los datos de la bdd ordenados ascendentemente por fecha
    public void getSensorsData(SensorsData sensorsData) {
        jaula.collection("datosSensores").orderBy("timemilis", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("sensorsData", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("sensorsData", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
