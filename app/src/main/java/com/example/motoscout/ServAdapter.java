package com.example.motoscout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServAdapter extends RecyclerView.Adapter<ServAdapter.ServicioViewHolder> {

    private List<serv_item> listaServicios;
    private Context context;

    public ServAdapter(List<serv_item> listaServicios, Context context) {
        this.listaServicios = listaServicios;
        this.context = context;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.serv_item, parent, false);
        return new ServicioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        serv_item servicio = listaServicios.get(position);
        holder.imgServicio.setImageResource(servicio.getImagenResId());
        holder.txtUbicacion.setText(servicio.getUbicacion());
        holder.txtFecha.setText(servicio.getFecha());
        holder.txtServicio.setText(servicio.getServicio());
        holder.txtPrecio.setText(servicio.getPrecio());
    }

    @Override
    public int getItemCount() {
        return listaServicios.size(); // Asegúrate que no está retornando 1 fijo
    }

    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgServicio;
        TextView txtUbicacion, txtFecha, txtServicio, txtPrecio;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            imgServicio = itemView.findViewById(R.id.img_servc);
            txtUbicacion = itemView.findViewById(R.id.txt_ubi);
            txtFecha = itemView.findViewById(R.id.txt_fecha);
            txtServicio = itemView.findViewById(R.id.txt_svc);
            txtPrecio = itemView.findViewById(R.id.txt_prc);
        }
    }
}
