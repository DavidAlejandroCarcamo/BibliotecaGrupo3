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

public class LibrosActivity extends AppCompatActivity {

    private EditText txtTituloLibro, txtISBNLibro, txtAnioLibro, txtDescripcionLibro, txtStockLibro;
    private Spinner spAutor, spEditorial, spCategoria, spEstado;
    private ListView listaLibros;

    private AutorDAO autorDAO;
    private EditorialDAO editorialDAO;
    private CategoriaDAO categoriaDAO;
    private LibroDAO libroDAO;

    private ArrayList<Autor> listaAutores;
    private ArrayList<Editorial> listaEditoriales;
    private ArrayList<Categoria> listaCategorias;
    private ArrayList<Libro> listaObjetosLibros;

    private ArrayList<String> listaTextoLibros;
    private ArrayAdapter<String> adaptadorLibros;

    private int idSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_libros);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTituloLibro = findViewById(R.id.txtTituloLibro);
        txtISBNLibro = findViewById(R.id.txtISBNLibro);
        txtAnioLibro = findViewById(R.id.txtAnioLibro);
        txtDescripcionLibro = findViewById(R.id.txtDescripcionLibro);
        txtStockLibro = findViewById(R.id.txtStockLibro);

        spAutor = findViewById(R.id.spAutor);
        spEditorial = findViewById(R.id.spEditorial);
        spCategoria = findViewById(R.id.spCategoria);
        spEstado = findViewById(R.id.spEstado);

        listaLibros = findViewById(R.id.listaLibros);

        autorDAO = new AutorDAO(this);
        editorialDAO = new EditorialDAO(this);
        categoriaDAO = new CategoriaDAO(this);
        libroDAO = new LibroDAO(this);

        cargarSpinners();
        cargarLibros();

        listaLibros.setOnItemClickListener((parent, view, position, id) -> {

            Libro libro = listaObjetosLibros.get(position);

            idSeleccionado = libro.getIdLibro();

            txtTituloLibro.setText(libro.getTitulo());
            txtISBNLibro.setText(libro.getIsbn());
            txtAnioLibro.setText(String.valueOf(libro.getAnio()));
            txtDescripcionLibro.setText(libro.getDescripcion());
            txtStockLibro.setText(String.valueOf(libro.getStock()));

            seleccionarAutor(libro.getIdAutor());
            seleccionarEditorial(libro.getIdEditorial());
            seleccionarCategoria(libro.getIdCategoria());
            seleccionarEstado(libro.getEstado());

            Toast.makeText(this, "Libro seleccionado", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarSpinners() {

        listaAutores = autorDAO.mostrarAutores();
        ArrayList<String> autoresTexto = new ArrayList<>();
        autoresTexto.add("Seleccione autor");

        for (Autor a : listaAutores) {
            autoresTexto.add(a.getNombres() + " " + a.getApellidos());
        }

        ArrayAdapter<String> adaptadorAutores = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                autoresTexto
        );
        adaptadorAutores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAutor.setAdapter(adaptadorAutores);


        listaEditoriales = editorialDAO.mostrarEditoriales();
        ArrayList<String> editorialesTexto = new ArrayList<>();
        editorialesTexto.add("Seleccione editorial");

        for (Editorial e : listaEditoriales) {
            editorialesTexto.add(e.getNombre());
        }

        ArrayAdapter<String> adaptadorEditoriales = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                editorialesTexto
        );
        adaptadorEditoriales.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditorial.setAdapter(adaptadorEditoriales);


        listaCategorias = categoriaDAO.mostrarCategorias();
        ArrayList<String> categoriasTexto = new ArrayList<>();
        categoriasTexto.add("Seleccione categoría");

        for (Categoria c : listaCategorias) {
            categoriasTexto.add(c.getNombre());
        }

        ArrayAdapter<String> adaptadorCategorias = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoriasTexto
        );
        adaptadorCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adaptadorCategorias);


        ArrayList<String> estados = new ArrayList<>();
        estados.add("Activo");
        estados.add("Inactivo");

        ArrayAdapter<String> adaptadorEstados = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );
        adaptadorEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adaptadorEstados);
    }

    public void guardarLibro(View v) {

        String titulo = txtTituloLibro.getText().toString();
        String isbn = txtISBNLibro.getText().toString();
        String anioTexto = txtAnioLibro.getText().toString();
        String descripcion = txtDescripcionLibro.getText().toString();
        String stockTexto = txtStockLibro.getText().toString();

        if (titulo.isEmpty() || isbn.isEmpty() || anioTexto.isEmpty() || descripcion.isEmpty() || stockTexto.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spAutor.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un autor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spEditorial.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione una editorial", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        int anio;
        int stock;

        try {
            anio = Integer.parseInt(anioTexto);
            stock = Integer.parseInt(stockTexto);
        } catch (Exception e) {
            Toast.makeText(this, "Año y stock deben ser números", Toast.LENGTH_SHORT).show();
            return;
        }

        int idAutor = listaAutores.get(spAutor.getSelectedItemPosition() - 1).getIdAutor();
        int idEditorial = listaEditoriales.get(spEditorial.getSelectedItemPosition() - 1).getIdEditorial();
        int idCategoria = listaCategorias.get(spCategoria.getSelectedItemPosition() - 1).getIdCategoria();
        String estado = spEstado.getSelectedItem().toString();

        boolean guardado = libroDAO.insertarLibro(
                titulo, isbn, anio, descripcion, stock, estado,
                idAutor, idEditorial, idCategoria
        );

        if (guardado) {
            Toast.makeText(this, "Libro guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarLibros();
        } else {
            Toast.makeText(this, "No se pudo guardar. Puede que el ISBN ya exista", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarLibro(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un libro de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = txtTituloLibro.getText().toString();
        String isbn = txtISBNLibro.getText().toString();
        String anioTexto = txtAnioLibro.getText().toString();
        String descripcion = txtDescripcionLibro.getText().toString();
        String stockTexto = txtStockLibro.getText().toString();

        if (titulo.isEmpty() || isbn.isEmpty() || anioTexto.isEmpty() || descripcion.isEmpty() || stockTexto.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spAutor.getSelectedItemPosition() == 0 || spEditorial.getSelectedItemPosition() == 0 || spCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Debe seleccionar autor, editorial y categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        int anio;
        int stock;

        try {
            anio = Integer.parseInt(anioTexto);
            stock = Integer.parseInt(stockTexto);
        } catch (Exception e) {
            Toast.makeText(this, "Año y stock deben ser números", Toast.LENGTH_SHORT).show();
            return;
        }

        int idAutor = listaAutores.get(spAutor.getSelectedItemPosition() - 1).getIdAutor();
        int idEditorial = listaEditoriales.get(spEditorial.getSelectedItemPosition() - 1).getIdEditorial();
        int idCategoria = listaCategorias.get(spCategoria.getSelectedItemPosition() - 1).getIdCategoria();
        String estado = spEstado.getSelectedItem().toString();

        boolean actualizado = libroDAO.actualizarLibro(
                idSeleccionado, titulo, isbn, anio, descripcion, stock, estado,
                idAutor, idEditorial, idCategoria
        );

        if (actualizado) {
            Toast.makeText(this, "Libro actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarLibros();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarLibro(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un libro de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = libroDAO.eliminarLibro(idSeleccionado);

        if (eliminado) {
            Toast.makeText(this, "Libro eliminado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarLibros();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarLibros() {

        listaObjetosLibros = libroDAO.mostrarLibros();
        listaTextoLibros = new ArrayList<>();

        for (Libro l : listaObjetosLibros) {
            listaTextoLibros.add(
                    l.getIdLibro() + " - " + l.getTitulo() +
                            "\nISBN: " + l.getIsbn() +
                            "\nAutor: " + l.getNombreAutor() +
                            "\nEditorial: " + l.getNombreEditorial() +
                            "\nCategoría: " + l.getNombreCategoria() +
                            "\nStock: " + l.getStock() + " | Estado: " + l.getEstado()
            );
        }

        adaptadorLibros = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTextoLibros
        );

        listaLibros.setAdapter(adaptadorLibros);
    }

    private void seleccionarAutor(int idAutor) {
        for (int i = 0; i < listaAutores.size(); i++) {
            if (listaAutores.get(i).getIdAutor() == idAutor) {
                spAutor.setSelection(i + 1);
                return;
            }
        }
    }

    private void seleccionarEditorial(int idEditorial) {
        for (int i = 0; i < listaEditoriales.size(); i++) {
            if (listaEditoriales.get(i).getIdEditorial() == idEditorial) {
                spEditorial.setSelection(i + 1);
                return;
            }
        }
    }

    private void seleccionarCategoria(int idCategoria) {
        for (int i = 0; i < listaCategorias.size(); i++) {
            if (listaCategorias.get(i).getIdCategoria() == idCategoria) {
                spCategoria.setSelection(i + 1);
                return;
            }
        }
    }

    private void seleccionarEstado(String estado) {
        if (estado.equals("Activo")) {
            spEstado.setSelection(0);
        } else {
            spEstado.setSelection(1);
        }
    }

    public void limpiarCampos(View v) {
        txtTituloLibro.setText("");
        txtISBNLibro.setText("");
        txtAnioLibro.setText("");
        txtDescripcionLibro.setText("");
        txtStockLibro.setText("");

        spAutor.setSelection(0);
        spEditorial.setSelection(0);
        spCategoria.setSelection(0);
        spEstado.setSelection(0);

        idSeleccionado = 0;
        txtTituloLibro.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}