package com.example.iot_lab2.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.iot_lab2.models.Equipo;

import java.util.ArrayList;
import java.util.List;

public class EquiposViewModel extends ViewModel {

    private final MutableLiveData<List<Equipo>> listaEquipos = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<Equipo>> getListaEquipos() {
        return listaEquipos;
    }

    public void agregarEquipo(Equipo equipo) {
        List<Equipo> lista = listaEquipos.getValue();
        lista.add(equipo);
        listaEquipos.setValue(lista);
    }

    public void actualizarEquipo(Equipo equipoActualizado) {
        List<Equipo> lista = listaEquipos.getValue();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCodigo().equals(equipoActualizado.getCodigo())) {
                lista.set(i, equipoActualizado);
                break;
            }
        }
        listaEquipos.setValue(lista);
    }

    public void eliminarEquipo(Equipo equipo) {
        List<Equipo> lista = listaEquipos.getValue();
        lista.remove(equipo);
        listaEquipos.setValue(lista);
    }
}