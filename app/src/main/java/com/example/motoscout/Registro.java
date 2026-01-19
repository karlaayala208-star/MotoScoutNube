package com.example.motoscout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Registro extends AppCompatActivity {

    EditText TxtNomb, TxtApell, TxtEm, TxtNumb, TxtPass, TxtDate, TxtExperiencia, TxtUbicacion;
    TextView tvCoordenadas;
    Button btnSeleccionarUbicacion;
    ImageView ivFotoPerfil;
    
    private Executor executor;
    private Handler handler;
    private String tipoUsuario;
    private String fotoBase64 = "";
    
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAP_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tipoUsuario = getIntent().getStringExtra("tipo_usuario");

        TxtNomb = findViewById(R.id.TxtNomb);
        TxtApell = findViewById(R.id.TxtApell);
        TxtEm = findViewById(R.id.TxtEm);
        TxtNumb = findViewById(R.id.TxtNumb);
        TxtPass = findViewById(R.id.TxtPass);
        TxtDate = findViewById(R.id.TxtDate);
        TxtExperiencia = findViewById(R.id.TxtExperiencia);
        TxtUbicacion = findViewById(R.id.TxtUbicacion);
        tvCoordenadas = findViewById(R.id.tvCoordenadas);
        btnSeleccionarUbicacion = findViewById(R.id.btnSeleccionarUbicacion);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Aseguramos que los campos se vean si es mecánico
        if ("mecanico".equals(tipoUsuario)) {
            TxtExperiencia.setVisibility(View.VISIBLE);
            btnSeleccionarUbicacion.setVisibility(View.VISIBLE);
            tvCoordenadas.setVisibility(View.VISIBLE);
        }

        TxtDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(Registro.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String fechaFormateada = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        TxtDate.setText(fechaFormateada);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    public void seleccionarImagen(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void abrirSelectorMapa(View view) {
        // Abrimos la nueva actividad del mapa
        Intent intent = new Intent(this, SeleccionarUbicacion.class);
        startActivityForResult(intent, MAP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Resultado de la Galería
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivFotoPerfil.setImageBitmap(bitmap);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                fotoBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Resultado del Mapa
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            double lat = data.getDoubleExtra("latitud", 0);
            double lng = data.getDoubleExtra("longitud", 0);
            String coords = lat + "," + lng;
            TxtUbicacion.setText(coords);
            tvCoordenadas.setText("Ubicación: " + coords);
            Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show();
        }
    }

    public void Inicio(View view) {
        executor.execute(() -> {
            String nombre = TxtNomb.getText().toString().trim();
            String apellido = TxtApell.getText().toString().trim();
            String email = TxtEm.getText().toString().trim();
            String telefono = TxtNumb.getText().toString().trim();
            String password = TxtPass.getText().toString().trim();
            String fecha = TxtDate.getText().toString().trim();
            String experiencia = TxtExperiencia.getText().toString().trim();
            String ubicacion = TxtUbicacion.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                handler.post(() -> Toast.makeText(getApplicationContext(), "Campos obligatorios vacíos", Toast.LENGTH_SHORT).show());
                return;
            }
            
            if ("mecanico".equals(tipoUsuario) && ubicacion.isEmpty()) {
                handler.post(() -> Toast.makeText(getApplicationContext(), "Por favor selecciona la ubicación de tu taller", Toast.LENGTH_SHORT).show());
                return;
            }

            try {
                URL url = new URL(Constantes.SERVER_URL + "registro_usuario.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "nombre=" + URLEncoder.encode(nombre, "UTF-8")
                        + "&apellido=" + URLEncoder.encode(apellido, "UTF-8")
                        + "&email=" + URLEncoder.encode(email, "UTF-8")
                        + "&telefono=" + URLEncoder.encode(telefono, "UTF-8")
                        + "&contrasena=" + URLEncoder.encode(password, "UTF-8")
                        + "&fecha_nacimiento=" + URLEncoder.encode(fecha, "UTF-8")
                        + "&tipo_usuario=" + URLEncoder.encode(tipoUsuario, "UTF-8")
                        + "&experiencia=" + URLEncoder.encode(experiencia, "UTF-8")
                        + "&ubicacion=" + URLEncoder.encode(ubicacion, "UTF-8")
                        + "&foto=" + URLEncoder.encode(fotoBase64, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    handler.post(() -> {
                        Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}