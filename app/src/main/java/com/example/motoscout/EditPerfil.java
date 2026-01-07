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

public class EditPerfil extends AppCompatActivity {
    EditText nomM;
    EditText apellM;
    EditText fecN;
    EditText numTe;
    EditText CoEl;
    EditText Contra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nomM=findViewById(R.id.nomM);
        apellM=findViewById(R.id.apellM);
        fecN=findViewById(R.id.fecN);
        numTe=findViewById(R.id.numTe);
        CoEl=findViewById(R.id.CoEl);
        Contra=findViewById(R.id.Contra);
    }
    public void GuardarPerf(View view){
        if(nomM.getText().toString().isEmpty() && apellM.getText().toString().isEmpty() && fecN.getText().toString().isEmpty()
                && CoEl.getText().toString().isEmpty() && Contra.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Datos faltantes por favor llene los campos", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(getApplicationContext(),conf.class);
            intent.putExtra("nomP",nomM.getText().toString());
            intent.putExtra("apeM",apellM.getText().toString());
            intent.putExtra("fechaM",fecN.getText().toString());
            intent.putExtra("numM",numTe.getText().toString());
            intent.putExtra("correoM",CoEl.getText().toString());
            intent.putExtra("contraM",Contra.getText().toString());
            startActivity(intent);
            finish();
        }
    }
}