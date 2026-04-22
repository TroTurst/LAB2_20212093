package com.example.iot_lab2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.iot_lab2.R;
import com.example.iot_lab2.adapters.EquipoAdapter;
import com.example.iot_lab2.databinding.ActivityListaEquiposBinding;
import com.example.iot_lab2.models.Equipo;
import com.example.iot_lab2.viewmodels.EquiposViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListaEquiposActivity extends AppCompatActivity {

    private ActivityListaEquiposBinding binding;
    private EquiposViewModel viewModel;
    private EquipoAdapter equipoAdapter;
    private List<Equipo> listaFiltrada;
    private Equipo equipoSeleccionado;

    private ActivityResultLauncher<Intent> formularioLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaEquiposBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(EquiposViewModel.class);
        listaFiltrada = new ArrayList<>();
        equipoAdapter = new EquipoAdapter(this, listaFiltrada);
        binding.listViewEquipos.setAdapter(equipoAdapter);

        registerForContextMenu(binding.listViewEquipos);
        configurarLauncher();
        configurarSpinnersFiltro();
        configurarFAB();
        observarViewModel();
    }

    private void configurarLauncher() {
        formularioLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Equipo equipoResultado = (Equipo) result.getData().getSerializableExtra(FormularioEquipoActivity.EXTRA_EQUIPO_RESULTADO);
                        String modo = result.getData().getStringExtra(FormularioEquipoActivity.EXTRA_MODO);
                        if (FormularioEquipoActivity.MODO_CREAR.equals(modo)) {
                            viewModel.agregarEquipo(equipoResultado);
                        } else if (FormularioEquipoActivity.MODO_EDITAR.equals(modo)) {
                            viewModel.actualizarEquipo(equipoResultado);
                        }
                    }
                }
        );
    }

    private void configurarSpinnersFiltro() {
        String[] tiposConTodos = prepararArrayConTodos(getResources().getStringArray(R.array.tipos_equipo));
        String[] estadosConTodos = prepararArrayConTodos(getResources().getStringArray(R.array.estados_equipo));

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposConTodos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFiltroTipo.setAdapter(adapterTipo);

        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estadosConTodos);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFiltroEstado.setAdapter(adapterEstado);

        AdapterView.OnItemSelectedListener listenerFiltro = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltros();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        binding.spinnerFiltroTipo.setOnItemSelectedListener(listenerFiltro);
        binding.spinnerFiltroEstado.setOnItemSelectedListener(listenerFiltro);
    }

    private String[] prepararArrayConTodos(String[] array) {
        String[] resultado = new String[array.length + 1];
        resultado[0] = "Todos";
        System.arraycopy(array, 0, resultado, 1, array.length);
        return resultado;
    }

    private void aplicarFiltros() {
        String tipoSeleccionado = binding.spinnerFiltroTipo.getSelectedItem().toString();
        String estadoSeleccionado = binding.spinnerFiltroEstado.getSelectedItem().toString();
        List<Equipo> listaCompleta = viewModel.getListaEquipos().getValue();

        if (listaCompleta == null) {
            listaFiltrada.clear();
            equipoAdapter.notifyDataSetChanged();
            actualizarVisibilidadLista();
            return;
        }

        listaFiltrada.clear();
        for (Equipo equipo : listaCompleta) {
            boolean coincideTipo = tipoSeleccionado.equals("Todos") || equipo.getTipoEquipo().equals(tipoSeleccionado);
            boolean coincideEstado = estadoSeleccionado.equals("Todos") || equipo.getEstado().equals(estadoSeleccionado);
            if (coincideTipo && coincideEstado) {
                listaFiltrada.add(equipo);
            }
        }

        equipoAdapter.notifyDataSetChanged();
        actualizarVisibilidadLista();
    }

    private void actualizarVisibilidadLista() {
        if (listaFiltrada.isEmpty()) {
            binding.textSinRegistros.setVisibility(View.VISIBLE);
            binding.listViewEquipos.setVisibility(View.GONE);
        } else {
            binding.textSinRegistros.setVisibility(View.GONE);
            binding.listViewEquipos.setVisibility(View.VISIBLE);
        }
    }

    private void configurarFAB() {
        binding.fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormularioEquipoActivity.class);
            intent.putExtra(FormularioEquipoActivity.EXTRA_MODO, FormularioEquipoActivity.MODO_CREAR);
            formularioLauncher.launch(intent);
        });
    }

    private void observarViewModel() {
        viewModel.getListaEquipos().observe(this, equipos -> aplicarFiltros());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        equipoSeleccionado = listaFiltrada.get(info.position);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contextEditar) {
            Intent intent = new Intent(this, FormularioEquipoActivity.class);
            intent.putExtra(FormularioEquipoActivity.EXTRA_MODO, FormularioEquipoActivity.MODO_EDITAR);
            intent.putExtra(FormularioEquipoActivity.EXTRA_EQUIPO, equipoSeleccionado);
            formularioLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.contextEliminar) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("¿Está seguro que desea eliminar?");
            alertDialog.setPositiveButton("ACEPTAR", (dialog, which) -> viewModel.eliminarEquipo(equipoSeleccionado));
            alertDialog.setNegativeButton("CANCELAR", null);
            alertDialog.show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionRefresh) {
            binding.spinnerFiltroTipo.setSelection(0);
            binding.spinnerFiltroEstado.setSelection(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}