package com.example.motoscout;

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

public class Pago extends AppCompatActivity {
    EditText txtNumT;
    EditText txtVto;
    EditText txtCvv;
    EditText txtPais;
    EditText txtNombT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pago);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtNumT=findViewById(R.id.txtNumT);
        txtVto=findViewById(R.id.txtVto);
        txtCvv=findViewById(R.id.txtCvv);
        txtPais=findViewById(R.id.txtPais);
        txtNombT=findViewById(R.id.txtNombT);
    }
    public void Regresar(View view) //intentos
    {
        Intent intent = new Intent(getApplicationContext(), Tarjeta.class);//intent es el nombre del intento
        startActivity(intent);
        finish();
    }
    public void GuardarTar(View view){
        if(txtNumT.getText().toString().isEmpty() && txtVto.getText().toString().isEmpty() && txtCvv.getText().toString().isEmpty()
                && txtPais.getText().toString().isEmpty() && txtNombT.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Datos faltantes por favor llene los campos", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(getApplicationContext(),Tarjeta.class);
            intent.putExtra("numt",txtNumT.getText().toString());
            intent.putExtra("vto",txtVto.getText().toString());
            intent.putExtra("cvv",txtCvv.getText().toString());
            intent.putExtra("pais",txtPais.getText().toString());
            intent.putExtra("nomt",txtNombT.getText().toString());
            startActivity(intent);
            finish();
        }
    }
}