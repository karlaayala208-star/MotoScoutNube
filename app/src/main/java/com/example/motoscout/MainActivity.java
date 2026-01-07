package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Registro(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Perfil.class);//intent es el nombre del intento
        startActivity(intent);
    }
    public void Login(View view) //intentos
    {
        //Intent intent = new Intent(getApplicationContext(),Perfil2.class);//intent es el nombre del intento
        Intent intent = new Intent(getApplicationContext(),Login.class);//intent es el nombre del intento
        startActivity(intent);
    }
}