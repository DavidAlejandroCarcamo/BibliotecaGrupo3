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
        Intent abrir = new Intent(this, AutoresActivity.class);
        startActivity(abrir);
    }

    public void abrirEditoriales(View v) {
        Intent abrir = new Intent(this, EditorialesActivity.class);
        startActivity(abrir);
    }

    public void abrirCategorias(View v) {
        Intent abrir = new Intent(this, CategoriasActivity.class);
        startActivity(abrir);
    }

    public void abrirLibros(View v) {
        Intent abrir = new Intent(this, LibrosActivity.class);
        startActivity(abrir);
    }

    public void abrirEjemplares(View v) {
        Intent abrir = new Intent(this, EjemplaresActivity.class);
        startActivity(abrir);
    }

    public void regresar(View v) {
        finish();
    }
}