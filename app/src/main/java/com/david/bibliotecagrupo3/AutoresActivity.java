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

public class AutoresActivity extends AppCompatActivity {

    private EditText txtNombresAutor, txtApellidosAutor, txtNacionalidadAutor;
    private ListView listaAutores;

    private AutorDAO autorDAO;
    private ArrayList<Autor> listaObjetos;
    private ArrayList<String> listaTexto;
    private ArrayAdapter<String> adaptador;

    private int idSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_autores);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombresAutor = findViewById(R.id.txtNombresAutor);
        txtApellidosAutor = findViewById(R.id.txtApellidosAutor);
        txtNacionalidadAutor = findViewById(R.id.txtNacionalidadAutor);
        listaAutores = findViewById(R.id.listaAutores);

        autorDAO = new AutorDAO(this);

        cargarAutores();

        listaAutores.setOnItemClickListener((parent, view, position, id) -> {

            Autor autor = listaObjetos.get(position);

            idSeleccionado = autor.getIdAutor();
            txtNombresAutor.setText(autor.getNombres());
            txtApellidosAutor.setText(autor.getApellidos());
            txtNacionalidadAutor.setText(autor.getNacionalidad());

            Toast.makeText(this, "Autor seleccionado", Toast.LENGTH_SHORT).show();
        });
    }

    public void guardarAutor(View v) {

        String nombres = txtNombresAutor.getText().toString();
        String apellidos = txtApellidosAutor.getText().toString();
        String nacionalidad = txtNacionalidadAutor.getText().toString();

        if (nombres.isEmpty()) {
            Toast.makeText(this, "Ingrese los nombres del autor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (apellidos.isEmpty()) {
            Toast.makeText(this, "Ingrese los apellidos del autor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nacionalidad.isEmpty()) {
            Toast.makeText(this, "Ingrese la nacionalidad", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean guardado = autorDAO.insertarAutor(nombres, apellidos, nacionalidad);

        if (guardado) {
            Toast.makeText(this, "Autor guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarAutores();
        } else {
            Toast.makeText(this, "No se pudo guardar el autor", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarAutor(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un autor de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombres = txtNombresAutor.getText().toString();
        String apellidos = txtApellidosAutor.getText().toString();
        String nacionalidad = txtNacionalidadAutor.getText().toString();

        if (nombres.isEmpty() || apellidos.isEmpty() || nacionalidad.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean actualizado = autorDAO.actualizarAutor(idSeleccionado, nombres, apellidos, nacionalidad);

        if (actualizado) {
            Toast.makeText(this, "Autor actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarAutores();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarAutor(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un autor de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado = autorDAO.eliminarAutor(idSeleccionado);

        if (eliminado) {
            Toast.makeText(this, "Autor eliminado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarAutores();
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarAutores() {

        listaObjetos = autorDAO.mostrarAutores();
        listaTexto = new ArrayList<>();

        for (Autor a : listaObjetos) {
            listaTexto.add(a.getIdAutor() + " - " + a.getNombres() + " " + a.getApellidos()
                    + "\nNacionalidad: " + a.getNacionalidad());
        }

        adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTexto
        );

        listaAutores.setAdapter(adaptador);
    }

    public void limpiarCampos(View v) {
        txtNombresAutor.setText("");
        txtApellidosAutor.setText("");
        txtNacionalidadAutor.setText("");
        idSeleccionado = 0;
        txtNombresAutor.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}