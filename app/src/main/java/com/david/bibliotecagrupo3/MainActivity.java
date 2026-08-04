package com.david.bibliotecagrupo3;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private EditText txtUsuario, txtContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBHelper dbHelper = new DBHelper(this);
        dbHelper.getWritableDatabase();

        txtUsuario = findViewById(R.id.txtUsuario);
        txtContrasena = findViewById(R.id.txtContrasena);
    }
    public void iniciarSesion(View v) {

        String usuario = txtUsuario.getText().toString();
        String contrasena = txtContrasena.getText().toString();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO(this);

        boolean existe = usuarioDAO.validarLogin(usuario, contrasena);

        if (existe) {

            String nombre = usuarioDAO.obtenerNombreUsuario(usuario);
            String rol = usuarioDAO.obtenerRolUsuario(usuario);

            Toast.makeText(this, "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();

            Intent abrir = new Intent(this, DashboardActivity.class);
            abrir.putExtra("nombre", nombre);
            abrir.putExtra("rol", rol);
            abrir.putExtra("usuario", usuario);

            startActivity(abrir);
            finish();

        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
    public void abrirRecuperar(View v) {
        Intent abrir = new Intent(this, RecuperarActivity.class);
        startActivity(abrir);
    }
}