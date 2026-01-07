package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserMec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_mec);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Regresar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Mecanico.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Serv(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), ServReal.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Salir(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Editar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), ConfMec.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Calificacion(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), CalfMec.class);//intent es el nombre del intento
        startActivity(intent);
    }
}