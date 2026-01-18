package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

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

        // Obtener el nombre del mecánico del Intent
        String nombreMecanico = getIntent().getStringExtra("nombre_mecanico");
        if (nombreMecanico != null) {
            tvMensaje.setText(nombreMecanico + " ha aceptado el servicio");
        }

        // Cambiar la foto aleatoriamente
        cambiarFotoAleatoria();
    }

    private void cambiarFotoAleatoria() {
        int[] fotos = {
                R.mipmap.ftoperfilmec,
                R.mipmap.mecanicouno,
                R.mipmap.mecanicodos,
                R.mipmap.mecanicotres,
                R.mipmap.mecanicocuatro,
                R.mipmap.mecanicocinco
        };

        Random random = new Random();
        int fotoAleatoria = fotos[random.nextInt(fotos.length)];
        ivPerfil.setImageResource(fotoAleatoria);
    }
    public void VerUbi(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), ServMec.class);//intent es el nombre del intento
        startActivity(intent);
    }
}