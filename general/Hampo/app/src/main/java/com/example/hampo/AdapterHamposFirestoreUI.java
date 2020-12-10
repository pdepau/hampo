package com.example.hampo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.hampo.modelo.Hampo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AdapterHamposFirestoreUI extends
        FirestoreRecyclerAdapter<Hampo, AdapterHampos.ViewHolderHampos> {

    private String id;
    public FirebaseAuth auth;
    private SharedPreferences pref;

    protected View.OnClickListener onClickListener;
    protected Context context;
    public AdapterHamposFirestoreUI(
            @NonNull FirestoreRecyclerOptions<Hampo> options, Context context){
        super(options);
        this.context = context;
    }


    @Override public AdapterHampos.ViewHolderHampos onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hampo_lista, parent, false);
        view.setOnClickListener(onClickListener);
        return new AdapterHampos.ViewHolderHampos(view);
    }
    @Override protected void onBindViewHolder(@NonNull AdapterHampos.ViewHolderHampos holder, int position, @NonNull Hampo hampo) {
        personalizaVista(holder, hampo);
        holder.itemView.setTag(new Integer(position));//para obtener posición
    }
    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }
    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }
    public int getPos(String id) {
        int pos = 0;
        while (pos < getItemCount()){
            if (getKey(pos).equals(id)) return pos;
            pos++;
        }
        return -1;
    }
    // Personalizamos un ViewHolder a partir de un lugar
    public void personalizaVista(AdapterHampos.ViewHolderHampos holder,
                                 Hampo hampo) {


        holder.nombre.setText(hampo.getNombre());
        Glide.with(context)
                .load(hampo.getUriFoto())
                .into(holder.foto);
        holder.foto.setScaleType(ImageView.ScaleType.FIT_START);
        personalizaNotificacion(holder, hampo);

    }

    public void personalizaNotificacion(final AdapterHampos.ViewHolderHampos holder,
                                        Hampo hampo){
        pref = PreferenceManager.getDefaultSharedPreferences(holder.foto.getContext());
        holder.notificacion.setBackgroundResource(R.drawable.not_green);

        if(pref.getBoolean("notificaciones", true)){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            id = auth.getUid();
            Query lecturas = db.collection(id).document(getKey(0)).collection("Lecturas").orderBy("Fecha").limit(1);
            lecturas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 1) {
                            task.getResult().getDocuments().get(0).getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w("TAG", "Listen failed.", e);
                                        return;
                                    }

                                    if (snapshot != null && snapshot.exists()) {
                                        if(Integer.parseInt(snapshot.getData().get("Temperatura").toString()) < 10 ||
                                                Integer.parseInt(snapshot.getData().get("Temperatura").toString()) > 30 ||
                                                Integer.parseInt(snapshot.getData().get("Comedero").toString()) < 30 ||
                                                Integer.parseInt(snapshot.getData().get("Bebedero").toString()) < 30){
                                            holder.notificacion.setBackgroundResource(R.drawable.not_yellow);
                                        }
                                        if(Integer.parseInt(snapshot.getData().get("Temperatura").toString()) < 0 ||
                                                Integer.parseInt(snapshot.getData().get("Temperatura").toString()) > 40 ||
                                                Integer.parseInt(snapshot.getData().get("Comedero").toString()) < 10 ||
                                                Integer.parseInt(snapshot.getData().get("Bebedero").toString()) < 10) {
                                            holder.notificacion.setBackgroundResource(R.drawable.not_red);
                                        }
                                        if(Integer.parseInt(snapshot.getData().get("Temperatura").toString()) >= 10 &&
                                                Integer.parseInt(snapshot.getData().get("Temperatura").toString()) <= 30 &&
                                                Integer.parseInt(snapshot.getData().get("Comedero").toString()) >= 30 &&
                                                Integer.parseInt(snapshot.getData().get("Bebedero").toString()) >= 30) {
                                            holder.notificacion.setBackgroundResource(R.drawable.not_green);
                                        }
                                        //Log.d("TAG", "Current data: " + snapshot.getData());
                                    } else {
                                        Log.d("TAG", "Current data: null");
                                    }
                                }
                            });

                        }
                    } else {
                        Log.e("Firebase", "Error al leer", task.getException());
                    }
                }
            });
        }
    }
}
