package com.david.bibliotecagrupo3;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DevolucionesActivity extends AppCompatActivity {

    private Spinner spPrestamoDevolucion, spCondicionEjemplar;
    private EditText txtFechaDevolucion, txtObservacionesDevolucion;
    private ListView listaDevoluciones;

    private DevolucionDAO devolucionDAO;

    private ArrayList<Prestamo> listaPrestamos;
    private ArrayList<Devolucion> listaObjetosDevoluciones;
    private ArrayList<String> listaTextoDevoluciones;

    private ArrayAdapter<String> adaptadorDevoluciones;

    private int idSeleccionado = 0;
    private int idDetalleSeleccionado = 0;
    private int idPrestamoSeleccionado = 0;
    private int idEjemplarSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devoluciones);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spPrestamoDevolucion = findViewById(R.id.spPrestamoDevolucion);
        spCondicionEjemplar = findViewById(R.id.spCondicionEjemplar);

        txtFechaDevolucion = findViewById(R.id.txtFechaDevolucion);
        txtObservacionesDevolucion = findViewById(R.id.txtObservacionesDevolucion);

        listaDevoluciones = findViewById(R.id.listaDevoluciones);

        devolucionDAO = new DevolucionDAO(this);

        cargarSpinners(0);
        cargarDevoluciones();

        listaDevoluciones.setOnItemClickListener((parent, view, position, id) -> {

            Devolucion devolucion = listaObjetosDevoluciones.get(position);

            idSeleccionado = devolucion.getIdDevolucion();
            idDetalleSeleccionado = devolucion.getIdDetalle();
            idPrestamoSeleccionado = devolucion.getIdPrestamo();
            idEjemplarSeleccionado = devolucion.getIdEjemplar();

            txtFechaDevolucion.setText(devolucion.getFechaDevolucion());
            txtObservacionesDevolucion.setText(devolucion.getObservaciones());

            cargarSpinners(devolucion.getIdDetalle());
            seleccionarPrestamo(devolucion.getIdDetalle());
            seleccionarCondicion(devolucion.getCondicionEjemplar());

            Toast.makeText(this, "Devolución seleccionada", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarSpinners(int idDetalleActual) {

        listaPrestamos = devolucionDAO.mostrarPrestamosPendientes(idDetalleActual);

        ArrayList<String> prestamosTexto = new ArrayList<>();
        prestamosTexto.add("Seleccione préstamo");

        for (Prestamo p : listaPrestamos) {
            prestamosTexto.add(
                    p.getTituloLibro() + " - " + p.getCodigoInventario()
                            + " | Usuario: " + p.getNombreUsuario()
            );
        }

        ArrayAdapter<String> adaptadorPrestamos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                prestamosTexto
        );

        adaptadorPrestamos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrestamoDevolucion.setAdapter(adaptadorPrestamos);


        ArrayList<String> condiciones = new ArrayList<>();
        condiciones.add("Bueno");
        condiciones.add("Dañado");

        ArrayAdapter<String> adaptadorCondiciones = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                condiciones
        );

        adaptadorCondiciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCondicionEjemplar.setAdapter(adaptadorCondiciones);
    }

    public void guardarDevolucion(View v) {

        String fecha = txtFechaDevolucion.getText().toString();
        String observaciones = txtObservacionesDevolucion.getText().toString();

        if (spPrestamoDevolucion.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un préstamo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Ingrese la fecha de devolución", Toast.LENGTH_SHORT).show();
            return;
        }

        Prestamo prestamo = listaPrestamos.get(spPrestamoDevolucion.getSelectedItemPosition() - 1);

        int idDetalle = prestamo.getIdDetalle();
        int idPrestamo = prestamo.getIdPrestamo();
        int idEjemplar = prestamo.getIdEjemplar();

        String condicion = spCondicionEjemplar.getSelectedItem().toString();

        boolean guardado = devolucionDAO.insertarDevolucion(
                idDetalle,
                idPrestamo,
                idEjemplar,
                fecha,
                condicion,
                observaciones
        );

        if (guardado) {
            Toast.makeText(this, "Devolución guardada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarDevoluciones();
        } else {
            Toast.makeText(this, "No se pudo guardar. Puede que este préstamo ya fue devuelto", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarDevolucion(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione una devolución de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = txtFechaDevolucion.getText().toString();
        String observaciones = txtObservacionesDevolucion.getText().toString();

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Ingrese la fecha de devolución", Toast.LENGTH_SHORT).show();
            return;
        }

        String condicion = spCondicionEjemplar.getSelectedItem().toString();

        boolean actualizado = devolucionDAO.actualizarDevolucion(
                idSeleccionado,
                idEjemplarSeleccionado,
                fecha,
                condicion,
                observaciones
        );

        if (actualizado) {
            Toast.makeText(this, "Devolución actualizada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarDevoluciones();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarDevolucion(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione una devolución de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = devolucionDAO.eliminarDevolucion(
                idSeleccionado,
                idDetalleSeleccionado,
                idPrestamoSeleccionado,
                idEjemplarSeleccionado
        );

        if (eliminado) {
            Toast.makeText(this, "Devolución eliminada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarDevoluciones();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDevoluciones() {

        listaObjetosDevoluciones = devolucionDAO.mostrarDevoluciones();
        listaTextoDevoluciones = new ArrayList<>();

        for (Devolucion d : listaObjetosDevoluciones) {
            listaTextoDevoluciones.add(
                    d.getIdDevolucion() + " - " + d.getTituloLibro() +
                            "\nEjemplar: " + d.getCodigoInventario() +
                            "\nUsuario: " + d.getNombreUsuario() +
                            "\nFecha devolución: " + d.getFechaDevolucion() +
                            "\nCondición: " + d.getCondicionEjemplar()
            );
        }

        adaptadorDevoluciones = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTextoDevoluciones
        );

        listaDevoluciones.setAdapter(adaptadorDevoluciones);
    }

    private void seleccionarPrestamo(int idDetalle) {
        for (int i = 0; i < listaPrestamos.size(); i++) {
            if (listaPrestamos.get(i).getIdDetalle() == idDetalle) {
                spPrestamoDevolucion.setSelection(i + 1);
                return;
            }
        }
    }

    private void seleccionarCondicion(String condicion) {
        if (condicion.equals("Bueno")) {
            spCondicionEjemplar.setSelection(0);
        } else {
            spCondicionEjemplar.setSelection(1);
        }
    }

    public void limpiarCampos(View v) {
        txtFechaDevolucion.setText("");
        txtObservacionesDevolucion.setText("");

        idSeleccionado = 0;
        idDetalleSeleccionado = 0;
        idPrestamoSeleccionado = 0;
        idEjemplarSeleccionado = 0;

        cargarSpinners(0);
        spPrestamoDevolucion.setSelection(0);
        spCondicionEjemplar.setSelection(0);

        txtFechaDevolucion.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}