package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class EditorialDAO {

    private DBHelper dbHelper;

    public EditorialDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertarEditorial(String nombre, String direccion, String telefono) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("direccion", direccion);
        valores.put("telefono", telefono);

        long resultado = db.insert("editoriales", null, valores);

        db.close();

        return resultado != -1;
    }

    public ArrayList<Editorial> mostrarEditoriales() {

        ArrayList<Editorial> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_editorial, nombre, direccion, telefono FROM editoriales ORDER BY id_editorial DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Editorial editorial = new Editorial();

                editorial.setIdEditorial(cursor.getInt(0));
                editorial.setNombre(cursor.getString(1));
                editorial.setDireccion(cursor.getString(2));
                editorial.setTelefono(cursor.getString(3));

                lista.add(editorial);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarEditorial(int idEditorial, String nombre, String direccion, String telefono) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("direccion", direccion);
        valores.put("telefono", telefono);

        int resultado = db.update(
                "editoriales",
                valores,
                "id_editorial = ?",
                new String[]{String.valueOf(idEditorial)}
        );

        db.close();

        return resultado > 0;
    }

    public boolean eliminarEditorial(int idEditorial) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete(
                "editoriales",
                "id_editorial = ?",
                new String[]{String.valueOf(idEditorial)}
        );

        db.close();

        return resultado > 0;
    }
}