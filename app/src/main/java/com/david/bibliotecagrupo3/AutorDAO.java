package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class AutorDAO {

    private DBHelper dbHelper;

    public AutorDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertarAutor(String nombres, String apellidos, String nacionalidad) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombres", nombres);
        valores.put("apellidos", apellidos);
        valores.put("nacionalidad", nacionalidad);

        long resultado = db.insert("autores", null, valores);

        db.close();

        return resultado != -1;
    }

    public ArrayList<Autor> mostrarAutores() {

        ArrayList<Autor> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_autor, nombres, apellidos, nacionalidad FROM autores ORDER BY id_autor DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Autor autor = new Autor();

                autor.setIdAutor(cursor.getInt(0));
                autor.setNombres(cursor.getString(1));
                autor.setApellidos(cursor.getString(2));
                autor.setNacionalidad(cursor.getString(3));

                lista.add(autor);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarAutor(int idAutor, String nombres, String apellidos, String nacionalidad) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombres", nombres);
        valores.put("apellidos", apellidos);
        valores.put("nacionalidad", nacionalidad);

        int resultado = db.update(
                "autores",
                valores,
                "id_autor = ?",
                new String[]{String.valueOf(idAutor)}
        );

        db.close();

        return resultado > 0;
    }

    public boolean eliminarAutor(int idAutor) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete(
                "autores",
                "id_autor = ?",
                new String[]{String.valueOf(idAutor)}
        );

        db.close();

        return resultado > 0;
    }
}