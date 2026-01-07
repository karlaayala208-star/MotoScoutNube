package com.example.motoscout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class conf extends AppCompatActivity {
    TextView txtnomM;
    TextView txtapellM;
    TextView txtfecn;
    TextView txtcoel;
    TextView txtnumM;
    TextView txtContra;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conf);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtnomM = findViewById(R.id.txtnomM);
        txtapellM = findViewById(R.id.txtapellM);
        txtfecn = findViewById(R.id.txtfecnM);
        txtcoel = findViewById(R.id.txtcoel);
        txtnumM = findViewById(R.id.txtnumM);
        txtContra = findViewById(R.id.txtContra);
        Bundle datosM = getIntent().getExtras();
        if (datosM != null) {
            txtnomM.setText("Nombre: " + datosM.getString("nomP", "Sin datos"));
            txtapellM.setText("Apellido: " + datosM.getString("apeM", "Sin datos"));
            txtfecn.setText("Fecha de nacimiento: " + datosM.getString("fechaM", "Sin datos"));
            txtcoel.setText("Correo electronico: " + datosM.getString("numM", "Sin datos"));
            txtnumM.setText("Numero: " + datosM.getString("correoM", "Sin datos"));
            txtContra.setText("Contraseña: " + datosM.getString("contraM", "Sin datos"));
        }
    }
    public void Regresar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), UserMtc.class);//intent es el nombre del intento
        startActivity(intent);
        finish();
    }
    public void EditPer(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), EditPerfil.class);//intent es el nombre del intento
        startActivity(intent);
        finish();
    }
}