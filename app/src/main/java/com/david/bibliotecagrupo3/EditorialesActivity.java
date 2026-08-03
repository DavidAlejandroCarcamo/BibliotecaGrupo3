package com.david.bibliotecagrupo3;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EditorialesActivity extends AppCompatActivity {

    private EditText txtNombreEditorial, txtDireccionEditorial, txtTelefonoEditorial;
    private ListView listaEditoriales;

    private EditorialDAO editorialDAO;
    private ArrayList<Editorial> listaObjetos;
    private ArrayList<String> listaTexto;
    private ArrayAdapter<String> adaptador;

    private int idSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editoriales);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombreEditorial = findViewById(R.id.txtNombreEditorial);
        txtDireccionEditorial = findViewById(R.id.txtDireccionEditorial);
        txtTelefonoEditorial = findViewById(R.id.txtTelefonoEditorial);
        listaEditoriales = findViewById(R.id.listaEditoriales);

        editorialDAO = new EditorialDAO(this);

        cargarEditoriales();

        listaEditoriales.setOnItemClickListener((parent, view, position, id) -> {

            Editorial editorial = listaObjetos.get(position);

            idSeleccionado = editorial.getIdEditorial();
            txtNombreEditorial.setText(editorial.getNombre());
            txtDireccionEditorial.setText(editorial.getDireccion());
            txtTelefonoEditorial.setText(editorial.getTelefono());

            Toast.makeText(this, "Editorial seleccionada", Toast.LENGTH_SHORT).show();
        });
    }

    public void guardarEditorial(View v) {

        String nombre = txtNombreEditorial.getText().toString();
        String direccion = txtDireccionEditorial.getText().toString();
        String telefono = txtTelefonoEditorial.getText().toString();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre de la editorial", Toast.LENGTH_SHORT).show();
            return;
        }

        if (direccion.isEmpty()) {
            Toast.makeText(this, "Ingrese la dirección", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telefono.isEmpty()) {
            Toast.makeText(this, "Ingrese el teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean guardado = editorialDAO.insertarEditorial(nombre, direccion, telefono);

        if (guardado) {
            Toast.makeText(this, "Editorial guardada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarEditoriales();
        } else {
            Toast.makeText(this, "No se pudo guardar. Puede que ya exista esa editorial", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarEditorial(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione una editorial de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = txtNombreEditorial.getText().toString();
        String direccion = txtDireccionEditorial.getText().toString();
        String telefono = txtTelefonoEditorial.getText().toString();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean actualizado = editorialDAO.actualizarEditorial(idSeleccionado, nombre, direccion, telefono);

        if (actualizado) {
            Toast.makeText(this, "Editorial actualizada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarEditoriales();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarEditorial(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione una editorial de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = editorialDAO.eliminarEditorial(idSeleccionado);

        if (eliminado) {
            Toast.makeText(this, "Editorial eliminada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarEditoriales();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEditoriales() {

        listaObjetos = editorialDAO.mostrarEditoriales();
        listaTexto = new ArrayList<>();

        for (Editorial e : listaObjetos) {
            listaTexto.add(e.getIdEditorial() + " - " + e.getNombre()
                    + "\nDirección: " + e.getDireccion()
                    + "\nTeléfono: " + e.getTelefono());
        }

        adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTexto
        );

        listaEditoriales.setAdapter(adaptador);
    }

    public void limpiarCampos(View v) {
        txtNombreEditorial.setText("");
        txtDireccionEditorial.setText("");
        txtTelefonoEditorial.setText("");
        idSeleccionado = 0;
        txtNombreEditorial.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}