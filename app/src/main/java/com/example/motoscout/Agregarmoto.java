package com.example.motoscout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int PERMISSION_CAMERA_CODE = 200;

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
        etMarca = findViewById(R.id.tvMarca);
        etModelo = findViewById(R.id.tvModelo);
        etAnio = findViewById(R.id.tvAnio);
        etKilometraje = findViewById(R.id.tvKilometraje);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = findViewById(R.id.btnGuardar);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(this, "Sesión no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSeleccionarImagen.setOnClickListener(v -> mostrarDialogoImagen());
        btnGuardar.setOnClickListener(v -> guardarMoto());
    }

    private void mostrarDialogoImagen() {
        String[] opciones = {"Elegir de Galería", "Tomar Foto"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Imagen");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                abrirGaleria();
            } else {
                verificarPermisosCamara();
            }
        });
        builder.show();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CODE);
        } else {
            abrirCamara();
        }
    }

    private void abrirCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Foto Moto");
        selectedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                selectedImageUri = data.getData();
            }
            // En REQUEST_IMAGE_CAPTURE, selectedImageUri ya contiene la URI de la foto
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
                        .url("http://192.168.100.99/motoscout/upload_moto.php")
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Moto registrada correctamente", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
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
