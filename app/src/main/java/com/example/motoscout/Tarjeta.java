package com.example.motoscout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tarjeta extends AppCompatActivity {
    TextView numt;
    TextView vto;
    TextView cvv;
    TextView pais;
    TextView nombt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tarjeta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        numt = findViewById(R.id.numt);
        vto = findViewById(R.id.vto);
        cvv = findViewById(R.id.cvv);
        pais = findViewById(R.id.pais);
        nombt = findViewById(R.id.nombt);
        Bundle datost = getIntent().getExtras();
        if (datost != null) {
            numt.setText("Número de tarjeta: " + datost.getString("numt", "Sin datos"));
            vto.setText("Fecha de vto.: " + datost.getString("vto", "Sin datos"));
            cvv.setText("CVV: " + datost.getString("cvv", "Sin datos"));
            pais.setText("País: " + datost.getString("pais", "Sin datos"));
            nombt.setText("Nombre del titular: " + datost.getString("nombt", "Sin datos"));
        }
    }
    public void Regresar(View view) {
        Intent intent = new Intent(getApplicationContext(), UserMtc.class);
        startActivity(intent);
        finish(); // Cierra el Activity actual
    }
    public void EditTarj(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Pago.class);//intent es el nombre del intento
        startActivity(intent);
        finish();
    }
}