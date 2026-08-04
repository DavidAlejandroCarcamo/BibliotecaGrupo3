package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UsuarioDAO {

    private DBHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean validarLogin(String usuario, String password) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE usuario = ? AND password_hash = ? AND estado = 'Activo'",
                new String[]{usuario, password}
        );

        boolean existe = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return existe;
    }

    public String obtenerNombreUsuario(String usuario) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT nombres || ' ' || apellidos FROM usuarios WHERE usuario = ?",
                new String[]{usuario}
        );

        String nombre = "";

        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return nombre;
    }

    public String obtenerRolUsuario(String usuario) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT r.nombre FROM usuarios u INNER JOIN roles r ON u.id_rol = r.id_rol WHERE u.usuario = ?",
                new String[]{usuario}
        );

        String rol = "";

        if (cursor.moveToFirst()) {
            rol = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return rol;
    }

    public int obtenerIdUsuario(String usuario) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_usuario FROM usuarios WHERE usuario = ?",
                new String[]{usuario}
        );

        int idUsuario = 0;

        if (cursor.moveToFirst()) {
            idUsuario = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return idUsuario;
    }

    public boolean insertarUsuario(int idRol, String usuario, String nombres, String apellidos,
                                   String correo, String telefono, String password,
                                   String pregunta, String respuesta, String estado,
                                   String fechaRegistro) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("id_rol", idRol);
        valores.put("usuario", usuario);
        valores.put("nombres", nombres);
        valores.put("apellidos", apellidos);
        valores.put("correo", correo);
        valores.put("telefono", telefono);
        valores.put("password_hash", password);
        valores.put("pregunta_recuperacion", pregunta);
        valores.put("respuesta_hash", respuesta);
        valores.put("estado", estado);
        valores.put("fecha_registro", fechaRegistro);

        long resultado = db.insert("usuarios", null, valores);

        db.close();

        return resultado != -1;
    }

    public ArrayList<Usuario> mostrarUsuarios() {

        ArrayList<Usuario> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT u.id_usuario, u.id_rol, r.nombre, u.usuario, u.nombres, u.apellidos, " +
                        "u.correo, u.telefono, u.password_hash, u.pregunta_recuperacion, " +
                        "u.respuesta_hash, u.estado, u.fecha_registro " +
                        "FROM usuarios u INNER JOIN roles r ON u.id_rol = r.id_rol " +
                        "ORDER BY u.id_usuario DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Usuario usuario = new Usuario();

                usuario.setIdUsuario(cursor.getInt(0));
                usuario.setIdRol(cursor.getInt(1));
                usuario.setRol(cursor.getString(2));
                usuario.setUsuario(cursor.getString(3));
                usuario.setNombres(cursor.getString(4));
                usuario.setApellidos(cursor.getString(5));
                usuario.setCorreo(cursor.getString(6));
                usuario.setTelefono(cursor.getString(7));
                usuario.setPassword(cursor.getString(8));
                usuario.setPregunta(cursor.getString(9));
                usuario.setRespuesta(cursor.getString(10));
                usuario.setEstado(cursor.getString(11));
                usuario.setFechaRegistro(cursor.getString(12));

                lista.add(usuario);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarUsuario(int idUsuario, int idRol, String usuario, String nombres,
                                     String apellidos, String correo, String telefono,
                                     String password, String pregunta, String respuesta,
                                     String estado, String fechaRegistro) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("id_rol", idRol);
        valores.put("usuario", usuario);
        valores.put("nombres", nombres);
        valores.put("apellidos", apellidos);
        valores.put("correo", correo);
        valores.put("telefono", telefono);
        valores.put("password_hash", password);
        valores.put("pregunta_recuperacion", pregunta);
        valores.put("respuesta_hash", respuesta);
        valores.put("estado", estado);
        valores.put("fecha_registro", fechaRegistro);

        int resultado = db.update(
                "usuarios",
                valores,
                "id_usuario = ?",
                new String[]{String.valueOf(idUsuario)}
        );

        db.close();

        return resultado > 0;
    }

    public boolean desactivarUsuario(int idUsuario) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("estado", "Inactivo");

        int resultado = db.update(
                "usuarios",
                valores,
                "id_usuario = ?",
                new String[]{String.valueOf(idUsuario)}
        );

        db.close();

        return resultado > 0;
    }
    public String obtenerPreguntaRecuperacion(String usuario) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT pregunta_recuperacion FROM usuarios WHERE usuario = ? AND estado = 'Activo'",
                new String[]{usuario}
        );

        String pregunta = "";

        if (cursor.moveToFirst()) {
            pregunta = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return pregunta;
    }

    public boolean validarRespuestaRecuperacion(String usuario, String respuesta) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE usuario = ? AND respuesta_hash = ? AND estado = 'Activo'",
                new String[]{usuario, respuesta}
        );

        boolean existe = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return existe;
    }

    public boolean cambiarPassword(String usuario, String nuevaPassword) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("password_hash", nuevaPassword);

        int resultado = db.update(
                "usuarios",
                valores,
                "usuario = ?",
                new String[]{usuario}
        );

        db.close();

        return resultado > 0;
    }
}