package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserMtc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_mtc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Regresar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), BtnPanico.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Config(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), conf.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void MetPag(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), MetPag.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Serv(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Serv.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Salir(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);//intent es el nombre del intento
        startActivity(intent);
    }
}