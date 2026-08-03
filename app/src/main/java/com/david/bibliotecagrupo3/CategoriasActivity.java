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

public class CategoriasActivity extends AppCompatActivity {

    private EditText txtNombreCategoria, txtDescripcionCategoria;
    private ListView listaCategorias;

    private CategoriaDAO categoriaDAO;
    private ArrayList<Categoria> listaObjetos;
    private ArrayList<String> listaTexto;
    private ArrayAdapter<String> adaptador;

    private int idSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categorias);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombreCategoria = findViewById(R.id.txtNombreCategoria);
        txtDescripcionCategoria = findViewById(R.id.txtDescripcionCategoria);
        listaCategorias = findViewById(R.id.listaCategorias);

        categoriaDAO = new CategoriaDAO(this);

        cargarCategorias();

        listaCategorias.setOnItemClickListener((parent, view, position, id) -> {

            Categoria categoria = listaObjetos.get(position);

            idSeleccionado = categoria.getIdCategoria();
            txtNombreCategoria.setText(categoria.getNombre());
            txtDescripcionCategoria.setText(categoria.getDescripcion());

            Toast.makeText(this, "Categoría seleccionada", Toast.LENGTH_SHORT).show();
        });
    }

    public void guardarCategoria(View v) {

        String nombre = txtNombreCategoria.getText().toString();
        String descripcion = txtDescripcionCategoria.getText().toString();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre de la categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Ingrese la descripción", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean guardado = categoriaDAO.insertarCategoria(nombre, descripcion);

        if (guardado) {
            Toast.makeText(this, "Categoría guardada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarCategorias();
        } else {
            Toast.makeText(this, "No se pudo guardar. Puede que ya exista esa categoría", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarCategoria(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione una categoría de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = txtNombreCategoria.getText().toString();
        String descripcion = txtDescripcionCategoria.getText().toString();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean actualizado = categoriaDAO.actualizarCategoria(idSeleccionado, nombre, descripcion);

        if (actualizado) {
            Toast.makeText(this, "Categoría actualizada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarCategorias();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarCategoria(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione una categoría de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = categoriaDAO.eliminarCategoria(idSeleccionado);

        if (eliminado) {
            Toast.makeText(this, "Categoría eliminada correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarCategorias();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarCategorias() {

        listaObjetos = categoriaDAO.mostrarCategorias();
        listaTexto = new ArrayList<>();

        for (Categoria c : listaObjetos) {
            listaTexto.add(c.getIdCategoria() + " - " + c.getNombre() + "\n" + c.getDescripcion());
        }

        adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTexto
        );

        listaCategorias.setAdapter(adaptador);
    }

    public void limpiarCampos(View v) {
        txtNombreCategoria.setText("");
        txtDescripcionCategoria.setText("");
        idSeleccionado = 0;
        txtNombreCategoria.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}