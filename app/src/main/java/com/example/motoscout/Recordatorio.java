package com.example.motoscout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Recordatorio extends AppCompatActivity {
    EditText txtPlac;
    EditText txtSeg;
    EditText txtMant;
    EditText txtServ;
    EditText txtFecServ;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recordatorio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtPlac=findViewById(R.id.txtPlac);
        txtSeg=findViewById(R.id.txtSeg);
        txtMant=findViewById(R.id.txtMant);
        txtServ=findViewById(R.id.txtServ);
        txtFecServ=findViewById(R.id.txtFecServ);
    }
    public void Regresar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Recordatorios.class);//intent es el nombre del intento
        startActivity(intent);
        finish();
    }
    public void GuardarTar(View view){
        if(txtPlac.getText().toString().isEmpty() && txtSeg.getText().toString().isEmpty() && txtMant.getText().toString().isEmpty()
                && txtServ.getText().toString().isEmpty() && txtFecServ.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Datos faltantes por favor llene los campos", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(getApplicationContext(),Recordatorios.class);
            intent.putExtra("placa",txtPlac.getText().toString());
            intent.putExtra("seguro",txtSeg.getText().toString());
            intent.putExtra("mante",txtMant.getText().toString());
            intent.putExtra("serv",txtServ.getText().toString());
            intent.putExtra("fecserv",txtFecServ.getText().toString());
            startActivity(intent);
            finish();
        }
    }
}