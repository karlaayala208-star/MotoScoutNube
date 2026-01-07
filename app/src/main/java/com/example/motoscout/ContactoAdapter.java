package com.example.motoscout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    private List<ContactoEmergencia> contactos;

    public ContactoAdapter(List<ContactoEmergencia> contactos) {
        this.contactos = contactos;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contac, parent, false);
        return new ContactoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        ContactoEmergencia contacto = contactos.get(position);
        holder.tvNombreContacto.setText(contacto.getNombreContacto());
        holder.tvTelefonoContacto.setText(contacto.getTelefonoContacto());
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreContacto, tvTelefonoContacto;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreContacto = itemView.findViewById(R.id.tvNombreContacto);
            tvTelefonoContacto = itemView.findViewById(R.id.tvTelefonoContacto);
        }
    }
}
