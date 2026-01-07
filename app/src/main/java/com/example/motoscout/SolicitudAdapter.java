package com.example.motoscout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(SolicitudServicio solicitud);
    }

    private List<SolicitudServicio> solicitudes;
    private OnItemClickListener listener;

    public SolicitudAdapter(List<SolicitudServicio> solicitudes, OnItemClickListener listener) {
        this.solicitudes = solicitudes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SolicitudAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudAdapter.ViewHolder holder, int position) {
        SolicitudServicio solicitud = solicitudes.get(position);
        holder.tvMoto.setText("Moto: " + solicitud.getMoto());
        holder.tvTipoServicio.setText("Servicio: " + solicitud.getTipoServicio());
        holder.tvFechaSolicitud.setText("Fecha: " + solicitud.getFechaSolicitud());
        holder.tvEstado.setText("Estado: " + solicitud.getEstado());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(solicitud));
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMoto, tvTipoServicio, tvFechaSolicitud, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMoto = itemView.findViewById(R.id.tvMoto);
            tvTipoServicio = itemView.findViewById(R.id.tvTipoServicio);
            tvFechaSolicitud = itemView.findViewById(R.id.tvFechaSolicitud);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
