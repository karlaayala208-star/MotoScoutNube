package com.example.motoscout;

import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Agregarmoto extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 100;

    private ImageView imgMoto;
    private EditText etMarca, etModelo, etAnio, etKilometraje;
    private Button btnSeleccionarImagen, btnGuardar;

    private Uri selectedImageUri = null;
    private int idUsuario = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregarmoto);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgMoto = findViewById(R.id.imgPreview);
        etMarca = findViewById(R.id.etMarca);
        etModelo = findViewById(R.id.etModelo);
        etAnio = findViewById(R.id.etAnio);
        etKilometraje = findViewById(R.id.etKilometraje);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = findViewById(R.id.btnGuardar);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(this, "Sesión no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnGuardar.setOnClickListener(v -> guardarMoto());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgMoto.setImageURI(selectedImageUri);
        }
    }

    private void guardarMoto() {
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String anioStr = etAnio.getText().toString().trim();
        String kmStr = etKilometraje.getText().toString().trim();

        if (marca.isEmpty() || modelo.isEmpty() || anioStr.isEmpty() || kmStr.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        int anio, kilometraje;
        try {
            anio = Integer.parseInt(anioStr);
            kilometraje = Integer.parseInt(kmStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Año y kilometraje deben ser números válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // Copiar contenido URI a archivo temporal interno
                File tempFile = new File(getCacheDir(), "temp_image.jpg");
                try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                     FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }

                MediaType mediaType = MediaType.parse("image/jpeg");

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id_usuario", String.valueOf(idUsuario))
                        .addFormDataPart("marca", marca)
                        .addFormDataPart("modelo", modelo)
                        .addFormDataPart("anio", String.valueOf(anio))
                        .addFormDataPart("kilometraje", String.valueOf(kilometraje))
                        .addFormDataPart("imagen", tempFile.getName(), RequestBody.create(tempFile, mediaType))
                        .build();

                Request request = new Request.Builder()
                        .url(Constantes.SERVER_URL + "upload_moto.php")
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Moto registrada correctamente", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);  // <- Devuelve resultado OK
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al registrar moto", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
