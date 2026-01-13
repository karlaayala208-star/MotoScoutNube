package com.example.motoscout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importaciones de Google Maps y Localización
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class ServMec extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_serv_mec);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 2. Cargar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // 3. Activar MI ubicación (El cliente esperando)
        activarUbicacionCliente();
    }

    private void activarUbicacionCliente() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Activar punto azul (Aquí estás tú/Cliente)
            mMap.setMyLocationEnabled(true);

            // Obtener coordenadas para centrar la cámara
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                                // Mover cámara a mi posición
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 14f));

                                // 4. Mostrar a Heriberto (El mecánico)
                                // NOTA: Para ver a Heriberto moverse en tiempo real, necesitas Firebase.
                                // Aquí simulamos que Heriberto está cerca de tu ubicación actual.
                                simularUbicacionMecanico(location.getLatitude() + 0.005, location.getLongitude() + 0.005);
                            }
                        }
                    });

        } else {
            // Pedir permiso si no lo tenemos
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Método para poner el marcador del mecánico
    private void simularUbicacionMecanico(double lat, double lng) {
        LatLng posHeriberto = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions()
                .position(posHeriberto)
                .title("Heriberto (Mecánico)")
                .snippet("Llega en 5 min")
                // Le ponemos color AZUL o un ícono de herramienta para diferenciarlo
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    // Respuesta de la petición de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activarUbicacionCliente();
            } else {
                Toast.makeText(this, "Se requiere permiso para ver tu ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void VerPerfil(View view) {
        Intent intent = new Intent(getApplicationContext(), PerfilMec.class);
        startActivity(intent);
    }
    public void Cancelar(View view) {
        Intent intent = new Intent(getApplicationContext(), ServCanc.class);
        startActivity(intent);
    }
    public void Terminar(View view) {
        Intent intent = new Intent(getApplicationContext(), Calf.class);
        startActivity(intent);
    }
}