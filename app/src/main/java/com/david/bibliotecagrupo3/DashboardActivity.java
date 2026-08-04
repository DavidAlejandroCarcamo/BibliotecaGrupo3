package com.david.bibliotecagrupo3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    private Button btnMenu;
    private TextView txtBienvenida, txtRol, txtContenido;
    private String nombre, rol, usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnMenu = findViewById(R.id.btnMenu);
        txtBienvenida = findViewById(R.id.txtBienvenida);
        txtRol = findViewById(R.id.txtRol);
        txtContenido = findViewById(R.id.txtContenido);

        nombre = getIntent().getStringExtra("nombre");
        rol = getIntent().getStringExtra("rol");
        usuario = getIntent().getStringExtra("usuario");

        txtBienvenida.setText("Bienvenido: " + nombre);
        txtRol.setText("Rol: " + rol);
        txtContenido.setText("Seleccione una opción del menú");
    }

    public void mostrarMenu(View v) {

        PopupMenu menu = new PopupMenu(this, btnMenu);

        menu.getMenu().add("Inicio");
        menu.getMenu().add("Usuarios");
        menu.getMenu().add("Catálogo");
        menu.getMenu().add("Préstamos");
        menu.getMenu().add("Devoluciones");
        menu.getMenu().add("Reportes");
        menu.getMenu().add("Cerrar sesión");

        menu.setOnMenuItemClickListener(item -> {

            String opcion = item.getTitle().toString();

            if (opcion.equals("Inicio")) {

                txtContenido.setText("Inicio: Bienvenido al sistema de biblioteca.");

            } else if (opcion.equals("Usuarios")) {

                Intent abrir = new Intent(this, UsuariosActivity.class);
                startActivity(abrir);

            } else if (opcion.equals("Catálogo")) {

                Intent abrir = new Intent(this, CatalogoActivity.class);
                startActivity(abrir);

            } else if (opcion.equals("Préstamos")) {

                Intent abrir = new Intent(this, PrestamosActivity.class);
                abrir.putExtra("usuario", usuario);
                startActivity(abrir);

            }  else if (opcion.equals("Devoluciones")) {

                Intent abrir = new Intent(this, DevolucionesActivity.class);
                startActivity(abrir);

            } else if (opcion.equals("Reportes")) {

                Intent abrir = new Intent(this, ReportesActivity.class);
                startActivity(abrir);

            } else if (opcion.equals("Cerrar sesión")) {

                Intent regresar = new Intent(this, MainActivity.class);
                startActivity(regresar);
                finish();
            }

            return true;
        });

        menu.show();
    }
}