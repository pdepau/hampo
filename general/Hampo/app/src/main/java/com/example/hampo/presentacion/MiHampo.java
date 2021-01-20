package com.example.hampo.presentacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.hampo.Aplicacion;
import com.example.hampo.R;
import com.example.hampo.casos_uso.CasosUsoHampo;
import com.example.hampo.casos_uso.CasosUsoMQTT;
import com.example.hampo.datos.EscuchadorHampo;
import com.example.hampo.datos.HamposFirestore;
import com.example.hampo.modelo.Hampo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.comun.MQTT;

import java.util.ArrayList;
import java.util.HashMap;

import static org.example.comun.MQTT.topicRoot;


public class MiHampo extends AppCompatActivity {


    private String id;
    private String idJaula;

    private CollectionReference hampo;
    private DocumentReference jaula;

    private TextView nombre;
    private TextView raza;

    private String TAG = "Coso";

    private TextView aux;

    private TextView viewDistancia;
    private TextView progresoActividad;
    private TextView progresoBebida;
    private TextView tipoIluminacion;
    private TextView sexoView;
    private ConstraintLayout totalActividad;
    private ConstraintLayout totalBebida;

    private CardView botonEditar;
    private CardView botonBorrar;
    private CardView botonLuz;

    private ImageView fotoBombilla;
    private ImageView imagenHampo;

    private int iluminacion = 1;


    private StorageReference mStorageRef;
    private HamposFirestore hampoDb;
    private Hampo h = new Hampo();
    private CasosUsoHampo cuh;
    private CasosUsoMQTT mqttController;

    public static MqttClient client = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_hampo);

        Bundle extras = getIntent().getExtras();
        idJaula = extras.getString("id");
        id = ((Aplicacion) getApplication()).id;
        hampoDb = new HamposFirestore(id);


        nombre = findViewById(R.id.nombreHampo);
        raza = findViewById(R.id.razaHampo);
        imagenHampo = findViewById(R.id.imagenHampo);

        cuh = new CasosUsoHampo(this, ((Aplicacion) getApplication()).hampos);
        mqttController = new CasosUsoMQTT();
        mqttController.crearConexionMQTT("1234548612");

        progresoActividad = findViewById(R.id.progresoActividad);
        progresoBebida = findViewById(R.id.progresoBebida);
        totalActividad = findViewById(R.id.fondoActividad);
        totalBebida = findViewById(R.id.fondoBebida);
        viewDistancia = findViewById(R.id.viewDistancia);
        tipoIluminacion = findViewById(R.id.tipoIluminacion);
        sexoView = findViewById(R.id.sexoView);

        botonEditar = findViewById(R.id.botonEditar);
        botonBorrar = findViewById(R.id.botonBorrar);
        botonLuz = findViewById(R.id.botonLuz);

        fotoBombilla = findViewById(R.id.fotoBombilla);

        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarEditar(v);
            }
        });

        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmacionBorrado();
            }
        });


        botonLuz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (iluminacion) {
                    case 0:
                        iluminacion++;
                        fotoBombilla.setImageResource(R.drawable.ic_light_bulb);
                        tipoIluminacion.setText("Apagadas");
                        enviarMensajeMQTT("Off", "luz/off");
                        break;
                    case 1:
                        iluminacion = 0;
                        fotoBombilla.setImageResource(R.drawable.ic_light_bulb_on);
                        tipoIluminacion.setText("Ambas");
                        enviarMensajeMQTT("On", "luz/on");
                        break;
                }
            }
        });

        crearConexionMQTT();
        actualizarDatosHampo();
        actualizarDatosLectura();
    }

    public void enviarMensajeMQTT(String data, String subTopic) {
        try {
            Log.i(MQTT.TAG, "Publicando mensaje: " + data);
            MqttMessage message = new MqttMessage(data.getBytes());
            message.setQos(MQTT.qos);
            message.setRetained(false);
            client.publish(topicRoot + subTopic, message);
        } catch (
                MqttException e) {
            Log.e(MQTT.TAG, "Error al publicar.", e);
        }
    }

    private void actualizarDatosHampo() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        hampoDb.elemento(idJaula, new EscuchadorHampo() {
            @Override
            // Callback encargado de cargar los datos recibidos de la bdd en el layout
            public void onRespuesta(Hampo hampo) {
                Log.d("Coso", "hampo uri: " + hampo.getFoto());
                h = hampo;

                Glide.with(MiHampo.this)
                        .load(hampo.getFoto())
                        .into(imagenHampo);

                // Cargo el nombre del hampo
                nombre.setText(hampo.getNombre());

                // Cargo la raza del hampo
                getRazas(hampo.getRaza());

                // Cargo el sexo
                if (hampo.getSexo().equals("male")) {
                    sexoView.setText("♂️");
                } else if (hampo.getSexo().equals("female")) {
                    sexoView.setText("♀️");
                } else {
                    sexoView.setText("Otro");
                }
            }
        });

    }

    private void actualizarDatosLectura() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query lecturas = db.collection(id).document(idJaula).collection("Lecturas").orderBy("Fecha").limit(1);

        lecturas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {

                        aux = findViewById(R.id.porcentajeIluminacion);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Iluminacion").toString() + "%");

                        aux = findViewById(R.id.temperatura);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Temperatura").toString() + "ºC");

                        aux = findViewById(R.id.porcentajeHumedad);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Humedad").toString() + "%");

                        aux = findViewById(R.id.porcentajeBebida);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Bebedero").toString() + "%");

                        aux = findViewById(R.id.porcentajeActividad);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Actividad").toString() + "%");

                        aux = findViewById(R.id.viewDistancia);
                        aux.setText(task.getResult().getDocuments().get(0).getData().get("Distancia").toString() + "cm");

                        actualizarBarras(task.getResult().getDocuments().get(0).getData().get("Bebedero").toString(), task.getResult().getDocuments().get(0).getData().get("Actividad").toString());
                    }
                } else {
                    Log.e("Firebase", "Error al leer", task.getException());
                }
            }
        });


    }

    private void actualizarBarras(String b, String a) {

        int totalB = totalBebida.getWidth();
        int totalA = totalActividad.getWidth();
        int porcentajeB = totalB * Integer.parseInt(b) / 100;
        int porcentajeA = totalA * Integer.parseInt(a) / 100;


        ConstraintLayout.LayoutParams lpA = (ConstraintLayout.LayoutParams) progresoActividad.getLayoutParams();
        lpA.width = porcentajeA;
        progresoActividad.setLayoutParams(lpA);

        ConstraintLayout.LayoutParams lpB = (ConstraintLayout.LayoutParams) progresoBebida.getLayoutParams();
        lpB.width = porcentajeB;
        progresoBebida.setLayoutParams(lpB);

    }


    public void getRazas(final String nombreRaza) {
        HamposFirestore db = new HamposFirestore(id);
        db.db.collection("raza").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> arrayListRazas = new ArrayList<>();
                final HashMap<String, String> hashMapComida = new HashMap<String, String>();
                final HashMap<String, String> hashMapBebida = new HashMap<String, String>();

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arrayListRazas.add(document.getId());
                    }
                    String[] arrayRazas = new String[arrayListRazas.size()];
                    arrayRazas = arrayListRazas.toArray(arrayRazas);
                    Log.d("Coso", arrayRazas[Integer.parseInt(nombreRaza)]);
                    raza.setText(arrayRazas[Integer.parseInt(nombreRaza)]);

                } else {
                    Log.e("Razas", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void lanzarEditar(View view) {
        Intent i = new Intent(view.getContext(), EditHampoActivity.class);
        i.putExtra("idJaula", idJaula);
        startActivity(i);

    }

    private void lanzarBorrar() {
        cuh.borrar(idJaula);
    }
    public void confirmacionBorrado(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Borrado de hampo");

        builder.setMessage("¿Estás seguro que quieres eliminar el hamop?");

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                lanzarBorrar();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatosHampo();
        actualizarDatosLectura();

    }

    public void crearConexionMQTT() {
        try {
            Log.i(TAG, "Conectando al broker " + MQTT.broker);
            client = new MqttClient(MQTT.broker, MQTT.clientId,
                    new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "WillTopic", "App desconectada".getBytes(), MQTT.qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar.", e);
        }
    }

}
