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

public class EjemplaresActivity extends AppCompatActivity {

    private Spinner spLibro, spEstadoEjemplar;
    private EditText txtCodigoInventario, txtUbicacion, txtFechaRegistro;
    private ListView listaEjemplares;

    private LibroDAO libroDAO;
    private EjemplarDAO ejemplarDAO;

    private ArrayList<Libro> listaLibros;
    private ArrayList<Ejemplar> listaObjetosEjemplares;
    private ArrayList<String> listaTextoEjemplares;

    private ArrayAdapter<String> adaptadorEjemplares;

    private int idSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ejemplares);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spLibro = findViewById(R.id.spLibro);
        spEstadoEjemplar = findViewById(R.id.spEstadoEjemplar);

        txtCodigoInventario = findViewById(R.id.txtCodigoInventario);
        txtUbicacion = findViewById(R.id.txtUbicacion);
        txtFechaRegistro = findViewById(R.id.txtFechaRegistro);

        listaEjemplares = findViewById(R.id.listaEjemplares);

        libroDAO = new LibroDAO(this);
        ejemplarDAO = new EjemplarDAO(this);

        cargarSpinners();
        cargarEjemplares();

        listaEjemplares.setOnItemClickListener((parent, view, position, id) -> {

            Ejemplar ejemplar = listaObjetosEjemplares.get(position);

            idSeleccionado = ejemplar.getIdEjemplar();

            txtCodigoInventario.setText(ejemplar.getCodigoInventario());
            txtUbicacion.setText(ejemplar.getUbicacion());
            txtFechaRegistro.setText(ejemplar.getFechaRegistro());

            seleccionarLibro(ejemplar.getIdLibro());
            seleccionarEstado(ejemplar.getEstado());

            Toast.makeText(this, "Ejemplar seleccionado", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarSpinners() {

        listaLibros = libroDAO.mostrarLibros();

        ArrayList<String> librosTexto = new ArrayList<>();
        librosTexto.add("Seleccione libro");

        for (Libro l : listaLibros) {
            librosTexto.add(l.getTitulo());
        }

        ArrayAdapter<String> adaptadorLibros = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                librosTexto
        );

        adaptadorLibros.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLibro.setAdapter(adaptadorLibros);


        ArrayList<String> estados = new ArrayList<>();
        estados.add("Disponible");
        estados.add("Prestado");
        estados.add("Dañado");

        ArrayAdapter<String> adaptadorEstados = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );

        adaptadorEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstadoEjemplar.setAdapter(adaptadorEstados);
    }

    public void guardarEjemplar(View v) {

        String codigo = txtCodigoInventario.getText().toString();
        String ubicacion = txtUbicacion.getText().toString();
        String fecha = txtFechaRegistro.getText().toString();

        if (spLibro.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un libro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (codigo.isEmpty() || ubicacion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idLibro = listaLibros.get(spLibro.getSelectedItemPosition() - 1).getIdLibro();
        String estado = spEstadoEjemplar.getSelectedItem().toString();

        boolean guardado = ejemplarDAO.insertarEjemplar(idLibro, codigo, ubicacion, estado, fecha);

        if (guardado) {
            Toast.makeText(this, "Ejemplar guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarEjemplares();
        } else {
            Toast.makeText(this, "No se pudo guardar. Puede que el código ya exista", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarEjemplar(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un ejemplar de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String codigo = txtCodigoInventario.getText().toString();
        String ubicacion = txtUbicacion.getText().toString();
        String fecha = txtFechaRegistro.getText().toString();

        if (spLibro.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un libro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (codigo.isEmpty() || ubicacion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idLibro = listaLibros.get(spLibro.getSelectedItemPosition() - 1).getIdLibro();
        String estado = spEstadoEjemplar.getSelectedItem().toString();

        boolean actualizado = ejemplarDAO.actualizarEjemplar(
                idSeleccionado,
                idLibro,
                codigo,
                ubicacion,
                estado,
                fecha
        );

        if (actualizado) {
            Toast.makeText(this, "Ejemplar actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarEjemplares();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarEjemplar(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un ejemplar de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = ejemplarDAO.eliminarEjemplar(idSeleccionado);

        if (eliminado) {
            Toast.makeText(this, "Ejemplar eliminado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarEjemplares();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEjemplares() {

        listaObjetosEjemplares = ejemplarDAO.mostrarEjemplares();
        listaTextoEjemplares = new ArrayList<>();

        for (Ejemplar e : listaObjetosEjemplares) {
            listaTextoEjemplares.add(
                    e.getIdEjemplar() + " - " + e.getTituloLibro() +
                            "\nCódigo: " + e.getCodigoInventario() +
                            "\nUbicación: " + e.getUbicacion() +
                            "\nEstado: " + e.getEstado() +
                            "\nFecha: " + e.getFechaRegistro()
            );
        }

        adaptadorEjemplares = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTextoEjemplares
        );

        listaEjemplares.setAdapter(adaptadorEjemplares);
    }

    private void seleccionarLibro(int idLibro) {
        for (int i = 0; i < listaLibros.size(); i++) {
            if (listaLibros.get(i).getIdLibro() == idLibro) {
                spLibro.setSelection(i + 1);
                return;
            }
        }
    }

    private void seleccionarEstado(String estado) {
        if (estado.equals("Disponible")) {
            spEstadoEjemplar.setSelection(0);
        } else if (estado.equals("Prestado")) {
            spEstadoEjemplar.setSelection(1);
        } else {
            spEstadoEjemplar.setSelection(2);
        }
    }

    public void limpiarCampos(View v) {
        spLibro.setSelection(0);
        spEstadoEjemplar.setSelection(0);

        txtCodigoInventario.setText("");
        txtUbicacion.setText("");
        txtFechaRegistro.setText("");

        idSeleccionado = 0;
        txtCodigoInventario.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}