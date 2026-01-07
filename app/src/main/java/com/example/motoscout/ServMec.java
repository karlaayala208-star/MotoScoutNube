package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ServMec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_serv_mec);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void VerPerfil(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), PerfilMec.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Cancelar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), ServCanc.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Terminar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Calf.class);//intent es el nombre del intento
        startActivity(intent);
    }
}