package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ServReal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_serv_real);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Regresar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), UserMec.class);//intent es el nombre del intento
        startActivity(intent);
        finish();
    }
    public void Recibo(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), PagoServicios.class);//intent es el nombre del intento
        startActivity(intent);
    }
}