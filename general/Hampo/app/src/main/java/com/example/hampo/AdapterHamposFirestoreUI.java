package com.example.hampo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.hampo.modelo.Hampo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdapterHamposFirestoreUI extends
        FirestoreRecyclerAdapter<Hampo, AdapterHampos.ViewHolderHampos> {
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
        holder.foto.setImageResource(R.drawable.pp);
        holder.foto.setScaleType(ImageView.ScaleType.FIT_START);


    }

}