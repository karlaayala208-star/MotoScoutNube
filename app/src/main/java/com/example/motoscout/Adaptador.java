package com.example.motoscout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder> {

    private List<Comentario> listaComentarios;

    public Adaptador(List<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsuario, txtComentario;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUsuario = itemView.findViewById(R.id.txtuser);
            txtComentario = itemView.findViewById(R.id.txtcom);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }

    @NonNull
    @Override
    public Adaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cmt, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptador.ViewHolder holder, int position) {
        Comentario c = listaComentarios.get(position);
        holder.txtUsuario.setText(c.getUsuario());
        holder.txtComentario.setText(c.getComentario());
        holder.ratingBar.setRating(c.getCalificacion());
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }
}
