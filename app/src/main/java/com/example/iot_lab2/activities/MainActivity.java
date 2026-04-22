package com.example.iot_lab2.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_lab2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final String miCodigo = "20212093";
    private final String miNombre = "Paul Munayco Tan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textCodigoEstudiante.setText(miCodigo);
        binding.textNombreEstudiante.setText(miNombre);

        binding.btnIngresar.setOnClickListener(v -> {
            if (tengoInternet()) {
                Intent intent = new Intent(this, ListaEquiposActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No hay conexión a Internet. No es posible continuar.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean tengoInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (capabilities == null) return false;

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}