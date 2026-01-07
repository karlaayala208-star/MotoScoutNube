package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Perfil extends AppCompatActivity {
    Button button_mot;
    Button button_mec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        button_mot = findViewById(R.id.button_mot);
        button_mec = findViewById(R.id.button_mec);
        button_mot.setOnClickListener(v -> abrirFormulario("motociclista"));
        button_mec.setOnClickListener(v -> abrirFormulario("mecanico"));
    }
    private void abrirFormulario(String tipoUsuario) {
        Intent intent = new Intent(this, Registro.class);
        intent.putExtra("tipo_usuario", tipoUsuario);
        startActivity(intent);
    }
    }