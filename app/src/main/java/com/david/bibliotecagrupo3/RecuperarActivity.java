package com.david.bibliotecagrupo3;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecuperarActivity extends AppCompatActivity {

    private EditText txtUsuarioRecuperar, txtRespuestaRecuperar;
    private EditText txtNuevaPassword, txtConfirmarPassword;
    private TextView txtPreguntaRecuperacion;

    private UsuarioDAO usuarioDAO;
    private String usuarioEncontrado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUsuarioRecuperar = findViewById(R.id.txtUsuarioRecuperar);
        txtRespuestaRecuperar = findViewById(R.id.txtRespuestaRecuperar);
        txtNuevaPassword = findViewById(R.id.txtNuevaPassword);
        txtConfirmarPassword = findViewById(R.id.txtConfirmarPassword);
        txtPreguntaRecuperacion = findViewById(R.id.txtPreguntaRecuperacion);

        usuarioDAO = new UsuarioDAO(this);

        txtPreguntaRecuperacion.setText("Primero busque su usuario");
    }

    public void buscarUsuario(View v) {

        String usuario = txtUsuarioRecuperar.getText().toString();

        if (usuario.isEmpty()) {
            Toast.makeText(this, "Ingrese su usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        String pregunta = usuarioDAO.obtenerPreguntaRecuperacion(usuario);

        if (pregunta.isEmpty()) {
            Toast.makeText(this, "Usuario no encontrado o inactivo", Toast.LENGTH_SHORT).show();
            txtPreguntaRecuperacion.setText("Pregunta de recuperación");
            usuarioEncontrado = "";
        } else {
            usuarioEncontrado = usuario;
            txtPreguntaRecuperacion.setText("Pregunta: " + pregunta);
            Toast.makeText(this, "Usuario encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    public void cambiarPassword(View v) {

        String respuesta = txtRespuestaRecuperar.getText().toString();
        String nuevaPassword = txtNuevaPassword.getText().toString();
        String confirmarPassword = txtConfirmarPassword.getText().toString();

        if (usuarioEncontrado.isEmpty()) {
            Toast.makeText(this, "Primero busque un usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (respuesta.isEmpty() || nuevaPassword.isEmpty() || confirmarPassword.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean respuestaCorrecta = usuarioDAO.validarRespuestaRecuperacion(usuarioEncontrado, respuesta);

        if (!respuestaCorrecta) {
            Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean cambiado = usuarioDAO.cambiarPassword(usuarioEncontrado, nuevaPassword);

        if (cambiado) {
            Toast.makeText(this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "No se pudo cambiar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    public void regresar(View v) {
        finish();
    }
}