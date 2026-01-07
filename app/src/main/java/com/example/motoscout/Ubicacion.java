package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Ubicacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ubicacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Buscar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Aceptar.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Moto(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Manual.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void BtnPan(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Perfil(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), UserMtc.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Recordatorio(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Recordatorios.class);//intent es el nombre del intento
        startActivity(intent);
    }
}