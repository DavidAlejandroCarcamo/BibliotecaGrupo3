package com.david.bibliotecagrupo3;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CatalogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalogo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void abrirAutores(View v) {
        Toast.makeText(this, "Aquí se registrarán los autores", Toast.LENGTH_SHORT).show();
    }

    public void abrirEditoriales(View v) {
        Toast.makeText(this, "Aquí se registrarán las editoriales", Toast.LENGTH_SHORT).show();
    }

    public void abrirCategorias(View v) {
        Intent abrir = new Intent(this, CategoriasActivity.class);
        startActivity(abrir);
    }

    public void abrirLibros(View v) {
        Toast.makeText(this, "Aquí se registrarán los libros", Toast.LENGTH_SHORT).show();
    }

    public void abrirEjemplares(View v) {
        Toast.makeText(this, "Aquí se registrarán los ejemplares", Toast.LENGTH_SHORT).show();
    }

    public void regresar(View v) {
        finish();
    }
}