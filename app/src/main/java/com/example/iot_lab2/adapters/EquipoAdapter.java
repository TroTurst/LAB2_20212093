package com.example.iot_lab2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.iot_lab2.R;
import com.example.iot_lab2.models.Equipo;

import java.util.List;

public class EquipoAdapter extends ArrayAdapter<Equipo> {

    public EquipoAdapter(Context context, List<Equipo> equipos) {
        super(context, 0, equipos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_equipo, parent, false);
        }

        Equipo equipo = getItem(position);

        TextView tvCodigo = convertView.findViewById(R.id.tvCodigo);
        TextView tvNombre = convertView.findViewById(R.id.tvNombre);
        TextView tvTipoEquipo = convertView.findViewById(R.id.tvTipoEquipo);
        TextView tvEstado = convertView.findViewById(R.id.tvEstado);

        tvCodigo.setText("Código: " + equipo.getCodigo());
        tvNombre.setText("Nombre del equipo: " + equipo.getNombre());
        tvTipoEquipo.setText("Tipo de equipo: " + equipo.getTipoEquipo());
        tvEstado.setText("Estado: " + equipo.getEstado());


        return convertView;
    }
}