package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CategoriaDAO {

    private DBHelper dbHelper;

    public CategoriaDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertarCategoria(String nombre, String descripcion) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("descripcion", descripcion);

        long resultado = db.insert("categorias", null, valores);

        db.close();

        return resultado != -1;
    }

    public ArrayList<Categoria> mostrarCategorias() {

        ArrayList<Categoria> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_categoria, nombre, descripcion FROM categorias ORDER BY id_categoria DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Categoria categoria = new Categoria();

                categoria.setIdCategoria(cursor.getInt(0));
                categoria.setNombre(cursor.getString(1));
                categoria.setDescripcion(cursor.getString(2));

                lista.add(categoria);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarCategoria(int idCategoria, String nombre, String descripcion) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("descripcion", descripcion);

        int resultado = db.update(
                "categorias",
                valores,
                "id_categoria = ?",
                new String[]{String.valueOf(idCategoria)}
        );

        db.close();

        return resultado > 0;
    }

    public boolean eliminarCategoria(int idCategoria) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete(
                "categorias",
                "id_categoria = ?",
                new String[]{String.valueOf(idCategoria)}
        );

        db.close();

        return resultado > 0;
    }
}