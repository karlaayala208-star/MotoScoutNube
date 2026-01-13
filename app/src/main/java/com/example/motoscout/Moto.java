package com.example.motoscout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Manual extends AppCompatActivity {

    // Componentes de la interfaz
    private ImageView imgMoto;
    private EditText etMarca, etModelo, etAnio, etKilometraje; // Todos los campos

    // Variable para la imagen
    private Uri uriImagenSeleccionada = null;

    // Launchers
    private ActivityResultLauncher<Intent> launcherGaleria;
    private ActivityResultLauncher<Intent> launcherCamara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Vincular TODOS los campos del XML
        imgMoto = findViewById(R.id.img_moto);
        etMarca = findViewById(R.id.etMarca);          // Nuevo
        etModelo = findViewById(R.id.etModelo);        // Nuevo
        etAnio = findViewById(R.id.etAnio);            // Nuevo
        etKilometraje = findViewById(R.id.etKilometraje);
        Button btnGuardar = findViewById(R.id.btn_guardar);

        // 2. Configurar Galería
        launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uriImagenSeleccionada = result.getData().getData();
                        imgMoto.setImageURI(uriImagenSeleccionada);
                    }
                }
        );

        // 3. Configurar Cámara
        launcherCamara = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imgMoto.setImageBitmap(imageBitmap);
                        uriImagenSeleccionada = null; // La cámara básica no da URI
                    }
                }
        );

        // Eventos
        imgMoto.setOnClickListener(v -> mostrarDialogoSeleccion());
        btnGuardar.setOnClickListener(v -> guardarMoto());
    }

    // --- LÓGICA DE IMAGEN --- (Igual que antes)
    private void mostrarDialogoSeleccion() {
        String[] opciones = {"Tomar foto", "Elegir de galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto de la moto");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) verificarPermisoCamara();
            else abrirGaleria();
        });
        builder.show();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcherGaleria.launch(intent);
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            abrirCamara();
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            launcherCamara.launch(intent);
        }
    }

    // --- AQUÍ ESTÁ EL CAMBIO IMPORTANTE: GUARDAR DATOS DEL USUARIO ---
    private void guardarMoto() {
        // 1. Obtener textos que escribió el usuario
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String anioStr = etAnio.getText().toString().trim();
        String kmStr = etKilometraje.getText().toString().trim();

        // 2. Validar que no estén vacíos
        if (marca.isEmpty() || modelo.isEmpty() || anioStr.isEmpty() || kmStr.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Convertir números de forma segura
        int anio = Integer.parseInt(anioStr);
        int kilometraje = Integer.parseInt(kmStr);

        // 4. Preparar la imagen (Uri o Default)
        String rutaImagen = "";
        if (uriImagenSeleccionada != null) {
            rutaImagen = uriImagenSeleccionada.toString();
        } else {
            // Si no eligió foto, guardamos una cadena vacía o "default"
            rutaImagen = "default";
        }

        // 5. CREAR EL OBJETO MOTO CON LOS DATOS REALES
        Moto nuevaMoto = new Moto(
                1,              // ID (Lo generarás en tu BD)
                marca,          // Marca escrita por usuario
                modelo,         // Modelo escrito por usuario
                anio,           // Año escrito por usuario
                kilometraje,    // Km escrito por usuario
                rutaImagen      // Ruta de la foto elegida
        );

        // Confirmación
        Toast.makeText(this, "Moto guardada: " + nuevaMoto.getMarca() + " " + nuevaMoto.getModelo(), Toast.LENGTH_LONG).show();

        // AQUÍ SIGUE TU CÓDIGO para guardar en base de datos o pasar a otra pantalla
        // Ejemplo:
        // Intent intent = new Intent(Manual.this, PantallaPrincipal.class);
        // startActivity(intent);
    }
}