package com.example.motoscout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Aceptar extends AppCompatActivity {

    private TextView tvMensaje;
    private ImageView ivPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aceptar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvMensaje = findViewById(R.id.textView28);
        ivPerfil = findViewById(R.id.imageView22);

        // Obtener datos del mecánico real del Intent
        String nombreMecanico = getIntent().getStringExtra("nombre_mecanico");
        String fotoBase64 = getIntent().getStringExtra("foto_mecanico");

        if (nombreMecanico != null && !nombreMecanico.isEmpty()) {
            tvMensaje.setText(nombreMecanico + " ha aceptado el servicio");
        } else {
            tvMensaje.setText("Un mecánico ha aceptado el servicio");
        }

        // Mostrar la foto real si existe, si no, poner la de defecto
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(fotoBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivPerfil.setImageBitmap(decodedByte);
            } catch (Exception e) {
                ivPerfil.setImageResource(R.mipmap.ftoperfilmec);
            }
        } else {
            ivPerfil.setImageResource(R.mipmap.ftoperfilmec);
        }
    }

    public void VerUbi(View view) {
        Intent intent = new Intent(getApplicationContext(), ServMec.class);
        startActivity(intent);
    }
}