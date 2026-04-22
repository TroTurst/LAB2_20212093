package com.example.iot_lab2.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_lab2.R;
import com.example.iot_lab2.databinding.ActivityFormularioEquipoBinding;
import com.example.iot_lab2.models.Equipo;

public class FormularioEquipoActivity extends AppCompatActivity {

    public static final String EXTRA_MODO = "EXTRA_MODO";
    public static final String EXTRA_EQUIPO = "EXTRA_EQUIPO";
    public static final String MODO_CREAR = "CREAR";
    public static final String MODO_EDITAR = "EDITAR";
    public static final String EXTRA_EQUIPO_RESULTADO = "EXTRA_EQUIPO_RESULTADO";

    private ActivityFormularioEquipoBinding binding;
    private String modoActual;
    private Equipo equipoRecibido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioEquipoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        modoActual = getIntent().getStringExtra(EXTRA_MODO);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            equipoRecibido = getIntent().getSerializableExtra(EXTRA_EQUIPO, Equipo.class);
        } else {
            equipoRecibido = (Equipo) getIntent().getSerializableExtra(EXTRA_EQUIPO);
        }

        configurarToolbar();
        configurarSpinner();
        binding.btnGuardar.setOnClickListener(v -> guardarEquipo());
        configurarModo();
    }

    private void configurarToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }

        if (MODO_CREAR.equals(modoActual)) {
            getSupportActionBar().setTitle("Registrar equipo");
        } else {
            getSupportActionBar().setTitle("Actualizar equipo");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_equipo,
                android.R.layout.simple_spinner_item
        );
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipoEquipo.setAdapter(adapterTipo);
    }

    private void configurarModo() {
        if (MODO_EDITAR.equals(modoActual) && equipoRecibido != null) {
            binding.editCodigo.setText(equipoRecibido.getCodigo());
            binding.editNombre.setText(equipoRecibido.getNombre());
            binding.editObservaciones.setText(equipoRecibido.getObservaciones());

            binding.editCodigo.setEnabled(false);
            binding.spinnerTipoEquipo.setEnabled(false);

            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) binding.spinnerTipoEquipo.getAdapter();
            if (adapter != null) {
                int posicionTipo = adapter.getPosition(equipoRecibido.getTipoEquipo());
                if (posicionTipo >= 0) {
                    binding.spinnerTipoEquipo.setSelection(posicionTipo);
                }
            }

            switch (equipoRecibido.getEstado()) {
                case "Operativo":
                    binding.radioGroupEstado.check(R.id.radioOperativo);
                    break;
                case "En mantenimiento":
                    binding.radioGroupEstado.check(R.id.radioEnMantenimiento);
                    break;
                case "Fuera de servicio":
                    binding.radioGroupEstado.check(R.id.radioFueraDeServicio);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionGuardar) {
            guardarEquipo();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void guardarEquipo() {
        String codigo = binding.editCodigo.getText().toString().trim();
        String nombre = binding.editNombre.getText().toString().trim();
        String tipoEquipo = binding.spinnerTipoEquipo.getSelectedItem().toString();
        String observaciones = binding.editObservaciones.getText().toString().trim();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        int radioSeleccionado = binding.radioGroupEstado.getCheckedRadioButtonId();
        if (radioSeleccionado == -1) {
            Toast.makeText(this, "Seleccione el estado del equipo.", Toast.LENGTH_SHORT).show();
            return;
        }

        String estado;
        if (radioSeleccionado == R.id.radioOperativo) {
            estado = "Operativo";
        } else if (radioSeleccionado == R.id.radioEnMantenimiento) {
            estado = "En mantenimiento";
        } else {
            estado = "Fuera de servicio";
        }

        String mensajeConfirmacion = modoActual.equals(MODO_CREAR)
                ? "¿Está seguro que desea registrar?"
                : "¿Está seguro que desea actualizar?";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(mensajeConfirmacion);
        alertDialog.setPositiveButton("ACEPTAR", (dialog, which) -> {
            Equipo equipoResultado = new Equipo(codigo, nombre, tipoEquipo, estado, observaciones);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_EQUIPO_RESULTADO, equipoResultado);
            resultIntent.putExtra(EXTRA_MODO, modoActual);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        alertDialog.setNegativeButton("CANCELAR", null);
        alertDialog.show();
    }
}