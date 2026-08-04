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

public class UsuariosActivity extends AppCompatActivity {

    private EditText txtUsuario, txtNombresUsuario, txtApellidosUsuario, txtCorreoUsuario,
            txtTelefonoUsuario, txtPasswordUsuario, txtPreguntaUsuario,
            txtRespuestaUsuario, txtFechaRegistroUsuario;

    private Spinner spRolUsuario, spEstadoUsuario;
    private ListView listaUsuarios;

    private UsuarioDAO usuarioDAO;

    private ArrayList<Usuario> listaObjetosUsuarios;
    private ArrayList<String> listaTextoUsuarios;
    private ArrayAdapter<String> adaptadorUsuarios;

    private int idSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuarios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUsuario = findViewById(R.id.txtUsuario);
        txtNombresUsuario = findViewById(R.id.txtNombresUsuario);
        txtApellidosUsuario = findViewById(R.id.txtApellidosUsuario);
        txtCorreoUsuario = findViewById(R.id.txtCorreoUsuario);
        txtTelefonoUsuario = findViewById(R.id.txtTelefonoUsuario);
        txtPasswordUsuario = findViewById(R.id.txtPasswordUsuario);
        txtPreguntaUsuario = findViewById(R.id.txtPreguntaUsuario);
        txtRespuestaUsuario = findViewById(R.id.txtRespuestaUsuario);
        txtFechaRegistroUsuario = findViewById(R.id.txtFechaRegistroUsuario);

        spRolUsuario = findViewById(R.id.spRolUsuario);
        spEstadoUsuario = findViewById(R.id.spEstadoUsuario);
        listaUsuarios = findViewById(R.id.listaUsuarios);

        usuarioDAO = new UsuarioDAO(this);

        cargarSpinners();
        cargarUsuarios();

        listaUsuarios.setOnItemClickListener((parent, view, position, id) -> {

            Usuario usuario = listaObjetosUsuarios.get(position);

            idSeleccionado = usuario.getIdUsuario();

            txtUsuario.setText(usuario.getUsuario());
            txtNombresUsuario.setText(usuario.getNombres());
            txtApellidosUsuario.setText(usuario.getApellidos());
            txtCorreoUsuario.setText(usuario.getCorreo());
            txtTelefonoUsuario.setText(usuario.getTelefono());
            txtPasswordUsuario.setText(usuario.getPassword());
            txtPreguntaUsuario.setText(usuario.getPregunta());
            txtRespuestaUsuario.setText(usuario.getRespuesta());
            txtFechaRegistroUsuario.setText(usuario.getFechaRegistro());

            seleccionarRol(usuario.getIdRol());
            seleccionarEstado(usuario.getEstado());

            Toast.makeText(this, "Usuario seleccionado", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarSpinners() {

        ArrayList<String> roles = new ArrayList<>();
        roles.add("Administrador");
        roles.add("Bibliotecario");

        ArrayAdapter<String> adaptadorRoles = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roles
        );

        adaptadorRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRolUsuario.setAdapter(adaptadorRoles);

        ArrayList<String> estados = new ArrayList<>();
        estados.add("Activo");
        estados.add("Inactivo");

        ArrayAdapter<String> adaptadorEstados = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );

        adaptadorEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstadoUsuario.setAdapter(adaptadorEstados);
    }

    public void guardarUsuario(View v) {

        String usuario = txtUsuario.getText().toString();
        String nombres = txtNombresUsuario.getText().toString();
        String apellidos = txtApellidosUsuario.getText().toString();
        String correo = txtCorreoUsuario.getText().toString();
        String telefono = txtTelefonoUsuario.getText().toString();
        String password = txtPasswordUsuario.getText().toString();
        String pregunta = txtPreguntaUsuario.getText().toString();
        String respuesta = txtRespuestaUsuario.getText().toString();
        String fecha = txtFechaRegistroUsuario.getText().toString();

        if (usuario.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() ||
                correo.isEmpty() || telefono.isEmpty() || password.isEmpty() ||
                pregunta.isEmpty() || respuesta.isEmpty() || fecha.isEmpty()) {

            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idRol = obtenerIdRol();
        String estado = spEstadoUsuario.getSelectedItem().toString();

        boolean guardado = usuarioDAO.insertarUsuario(
                idRol, usuario, nombres, apellidos, correo, telefono,
                password, pregunta, respuesta, estado, fecha
        );

        if (guardado) {
            Toast.makeText(this, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarUsuarios();
        } else {
            Toast.makeText(this, "No se pudo guardar. Puede que el usuario o correo ya exista", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarUsuario(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un usuario de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        String usuario = txtUsuario.getText().toString();
        String nombres = txtNombresUsuario.getText().toString();
        String apellidos = txtApellidosUsuario.getText().toString();
        String correo = txtCorreoUsuario.getText().toString();
        String telefono = txtTelefonoUsuario.getText().toString();
        String password = txtPasswordUsuario.getText().toString();
        String pregunta = txtPreguntaUsuario.getText().toString();
        String respuesta = txtRespuestaUsuario.getText().toString();
        String fecha = txtFechaRegistroUsuario.getText().toString();

        if (usuario.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() ||
                correo.isEmpty() || telefono.isEmpty() || password.isEmpty() ||
                pregunta.isEmpty() || respuesta.isEmpty() || fecha.isEmpty()) {

            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idRol = obtenerIdRol();
        String estado = spEstadoUsuario.getSelectedItem().toString();

        boolean actualizado = usuarioDAO.actualizarUsuario(
                idSeleccionado, idRol, usuario, nombres, apellidos,
                correo, telefono, password, pregunta, respuesta, estado, fecha
        );

        if (actualizado) {
            Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarUsuarios();
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    public void desactivarUsuario(View v) {

        if (idSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un usuario de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean desactivado = usuarioDAO.desactivarUsuario(idSeleccionado);

        if (desactivado) {
            Toast.makeText(this, "Usuario desactivado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos(v);
            cargarUsuarios();
        } else {
            Toast.makeText(this, "No se pudo desactivar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarUsuarios() {

        listaObjetosUsuarios = usuarioDAO.mostrarUsuarios();
        listaTextoUsuarios = new ArrayList<>();

        for (Usuario u : listaObjetosUsuarios) {
            listaTextoUsuarios.add(
                    u.getIdUsuario() + " - " + u.getUsuario() +
                            "\nNombre: " + u.getNombres() + " " + u.getApellidos() +
                            "\nRol: " + u.getRol() +
                            "\nCorreo: " + u.getCorreo() +
                            "\nEstado: " + u.getEstado()
            );
        }

        adaptadorUsuarios = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaTextoUsuarios
        );

        listaUsuarios.setAdapter(adaptadorUsuarios);
    }

    private int obtenerIdRol() {
        String rol = spRolUsuario.getSelectedItem().toString();

        if (rol.equals("Administrador")) {
            return 1;
        } else {
            return 2;
        }
    }

    private void seleccionarRol(int idRol) {
        if (idRol == 1) {
            spRolUsuario.setSelection(0);
        } else {
            spRolUsuario.setSelection(1);
        }
    }

    private void seleccionarEstado(String estado) {
        if (estado.equals("Activo")) {
            spEstadoUsuario.setSelection(0);
        } else {
            spEstadoUsuario.setSelection(1);
        }
    }

    public void limpiarCampos(View v) {
        txtUsuario.setText("");
        txtNombresUsuario.setText("");
        txtApellidosUsuario.setText("");
        txtCorreoUsuario.setText("");
        txtTelefonoUsuario.setText("");
        txtPasswordUsuario.setText("");
        txtPreguntaUsuario.setText("");
        txtRespuestaUsuario.setText("");
        txtFechaRegistroUsuario.setText("");

        spRolUsuario.setSelection(0);
        spEstadoUsuario.setSelection(0);

        idSeleccionado = 0;
        txtUsuario.requestFocus();
    }

    public void regresar(View v) {
        finish();
    }
}