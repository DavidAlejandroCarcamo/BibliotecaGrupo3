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

public class PrestamosActivity extends AppCompatActivity {

    private Spinner spUsuarioPrestamo, spEjemplarPrestamo, spEstadoPrestamo;
    private EditText txtFechaPrestamo, txtFechaVencimiento, txtObservacionesPrestamo;
    private ListView listaPrestamos;

    private UsuarioDAO usuarioDAO;
    private PrestamoDAO prestamoDAO;

    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<Ejemplar> listaEjemplares;
    private ArrayList<Prestamo> listaObjetosPrestamos;
    private ArrayList<String> listaTextoPrestamos;

    private ArrayAdapter<String> adaptadorPrestamos;

    private int idSeleccionado = 0;
    private int idDetalleSeleccionado = 0;
    private int idEjemplarAnterior = 0;
    private int idBibliotecario = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prestamos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spUsuarioPrestamo = findViewById(R.id.spUsuarioPrestamo);
        spEjemplarPrestamo = findViewById(R.id.spEjemplarPrestamo);
        spEstadoPrestamo = findViewById(R.id.spEstadoPrestamo);

        txtFechaPrestamo = findViewById(R.id.txtFechaPrestamo);
        txtFechaVencimiento = findViewById(R.id.txtFechaVencimiento);
        txtObservacionesPrestamo = findViewById(R.id.txtObservacionesPrestamo);

        listaPrestamos = findViewById(R.id.listaPrestamos);

        usuarioDAO = new UsuarioDAO(this);
        prestamoDAO = new PrestamoDAO(this);

        String usuarioLogin = getIntent().getStringExtra("usuario");

        if (usuarioLogin != null) {
            int idTemp = usuarioDAO.obtenerIdUsuario(usuarioLogin);

            if (idTemp != 0) {
                idBibliotecario = idTemp;
            }
        }

        cargarSpinners(0);
        cargarPrestamos();

        listaPrestamos.setOnItemClickListener((parent, view, position, id) -> {

            Prestamo prestamo = listaObjetosPrestamos.get(position);

            idSeleccionado = prestamo.getIdPrestamo();
            idDetalleSeleccionado = prestamo.getIdDetalle();
            idEjemplarAnterior = prestamo.getIdEjemplar();

            txtFechaPrestamo.setText(prestamo.getFechaPrestamo());
            txtFechaVencimiento.setText(prestamo.getFechaVencimiento());
            txtObservacionesPrestamo.setText(prestamo.getObservaciones());

            cargarSpinners(prestamo.getIdEjemplar());

            seleccionarUsuario(prestamo.getIdUsuario());
            seleccionarEjemplar(prestamo.getIdEjemplar());
            seleccionarEstado(prestamo.getEstado());

            Toast.makeText(this, "Préstamo seleccionado", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarSpinners(int idEjemplarActual) {

        ArrayList<Usuario> todosUsuarios = usuarioDAO.mostrarUsuarios();
        listaUsuarios = new ArrayList<>();

        ArrayList<String> usuariosTexto = new ArrayList<>();
        usuariosTexto.add("Seleccione usuario");

        for (Usuario u : todosUsuarios) {
            if (u.getEstado().equals("Activo")) {
                listaUsuarios.add(u);
                usuariosTexto.add(u.getNombres() + " " + u.getApellidos() + " - " + u.getUsuario());
            }
        }

        ArrayAdapter<String> adaptadorUsuarios = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                usuariosTexto
        );

        adaptadorUsuarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUsuarioPrestamo.setAdapter(adaptadorUsuarios);


        listaEjemplares = prestamoDAO.mostrarEjemplaresDisponibles(idEjemplarActual);

        ArrayList<String> ejemplaresTexto = new ArrayList<>();
        ejemplaresTexto.add("Seleccione ejemplar");

        for (Ejemplar e : listaEjemplares) {
            ejemplaresTexto.add(e.getTituloLibro() + " - " + e.getCodigoInventario());
        }

        ArrayAdapter<String> adaptadorEjemplares = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ejemplaresTexto
        );

        adaptadorEjemplares.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEjemplarPrestamo.setAdapter(adaptadorEjemplares);


        ArrayList<String> estados = new ArrayList<>();
        estados.add("Activo");
        estados.add("Cancelado");

        ArrayAdapter<String> adaptadorEstados = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );

        adaptadorEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstadoPrestamo.setAdapter(adaptadorEstados);
    }

    public void guardarPrestamo(View v) {

        String fechaPrestamo = txtFechaPrestamo.getText().toString();
        String fechaVencimiento = txtFechaVencimiento.getText().toString();
        String observaciones = txtObservacionesPrestamo.getText().toString();

        if (spUsuarioPrestamo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spEjemplarPrestamo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un ejemplar disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaPrestamo.isEmpty() || fechaVencimiento.isEmpty()) {
            Toast.makeText(this, "Ingrese las fechas del préstamo", Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario = listaUsuarios.get(spUsuarioPrestamo.getSelectedItemPosition() - 1).getIdUsuario();
        int idEjemplar = listaEjemplares.get(spEjemplarPrestamo.getSelectedItemPosition() - 1).getIdEjemplar();
        String estado = spEstadoPrestamo.getSelectedItem().toString();

        boolean guardado = prestamoDAO.insertarPrestamo(
                idUsuario,
                idBibliotecario,
                idEjemplar,
                fechaPrestamo,
                fechaVencimiento,
                estado,
                observaciones
        );

        if (guardado) {
            Toast.makeText(this, "Préstamo guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarPrestamos();
        } else {
            Toast.makeText(this, "No se pudo guardar el préstamo", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarPrestamo(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un préstamo de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaPrestamo = txtFechaPrestamo.getText().toString();
        String fechaVencimiento = txtFechaVencimiento.getText().toString();
        String observaciones = txtObservacionesPrestamo.getText().toString();

        if (spUsuarioPrestamo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spEjemplarPrestamo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un ejemplar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaPrestamo.isEmpty() || fechaVencimiento.isEmpty()) {
            Toast.makeText(this, "Ingrese las fechas del préstamo", Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario = listaUsuarios.get(spUsuarioPrestamo.getSelectedItemPosition() - 1).getIdUsuario();
        int idEjemplarNuevo = listaEjemplares.get(spEjemplarPrestamo.getSelectedItemPosition() - 1).getIdEjemplar();
        String estado = spEstadoPrestamo.getSelectedItem().toString();

        boolean actualizado = prestamoDAO.actualizarPrestamo(
                idSeleccionado,
                idDetalleSeleccionado,
                idUsuario,
                idBibliotecario,
                idEjemplarNuevo,
                idEjemplarAnterior,
                fechaPrestamo,
                fechaVencimiento,
                estado,
                observaciones
        );

        if (actualizado) {
            Toast.makeText(this, "Préstamo actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarPrestamos();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarPrestamo(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un préstamo de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = prestamoDAO.eliminarPrestamo(idSeleccionado, idEjemplarAnterior);

        if (eliminado) {
            Toast.makeText(this, "Préstamo eliminado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarPrestamos();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPrestamos() {

        listaObjetosPrestamos = prestamoDAO.mostrarPrestamos();
        listaTextoPrestamos = new ArrayList<>();

        for (Prestamo p : listaObjetosPrestamos) {
            listaTextoPrestamos.add(
                    p.getIdPrestamo() + " - " + p.getTituloLibro() +
                            "\nEjemplar: " + p.getCodigoInventario() +
                            "\nUsuario: " + p.getNombreUsuario() +
                            "\nBibliotecario: " + p.getNombreBibliotecario() +
                            "\nFecha: " + p.getFechaPrestamo() +
                            " | Vence: " + p.getFechaVencimiento() +
                            "\nEstado: " + p.getEstado()
            );
        }

        adaptadorPrestamos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTextoPrestamos
        );

        listaPrestamos.setAdapter(adaptadorPrestamos);
    }

    private void seleccionarUsuario(int idUsuario) {
        for (int i = 0; i < listaUsuarios.size(); i++) {
            if (listaUsuarios.get(i).getIdUsuario() == idUsuario) {
                spUsuarioPrestamo.setSelection(i + 1);
                return;
            }
        }
    }

    private void seleccionarEjemplar(int idEjemplar) {
        for (int i = 0; i < listaEjemplares.size(); i++) {
            if (listaEjemplares.get(i).getIdEjemplar() == idEjemplar) {
                spEjemplarPrestamo.setSelection(i + 1);
                return;
            }
        }
    }

    private void seleccionarEstado(String estado) {
        if (estado.equals("Activo")) {
            spEstadoPrestamo.setSelection(0);
        } else {
            spEstadoPrestamo.setSelection(1);
        }
    }

    public void limpiarCampos(View v) {
        txtFechaPrestamo.setText("");
        txtFechaVencimiento.setText("");
        txtObservacionesPrestamo.setText("");

        idSeleccionado = 0;
        idDetalleSeleccionado = 0;
        idEjemplarAnterior = 0;

        cargarSpinners(0);
        spUsuarioPrestamo.setSelection(0);
        spEjemplarPrestamo.setSelection(0);
        spEstadoPrestamo.setSelection(0);

        txtFechaPrestamo.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}