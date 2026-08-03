package com.david.bibliotecagrupo3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}