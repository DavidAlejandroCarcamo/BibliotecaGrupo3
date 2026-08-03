package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class EjemplarDAO {

    private DBHelper dbHelper;

    public EjemplarDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertarEjemplar(int idLibro, String codigoInventario, String ubicacion, String estado, String fechaRegistro) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("id_libro", idLibro);
        valores.put("codigo_inventario", codigoInventario);
        valores.put("ubicacion", ubicacion);
        valores.put("estado", estado);
        valores.put("fecha_registro", fechaRegistro);

        long resultado = db.insert("ejemplares", null, valores);

        db.close();

        return resultado != -1;
    }

    public ArrayList<Ejemplar> mostrarEjemplares() {

        ArrayList<Ejemplar> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT ej.id_ejemplar, ej.id_libro, l.titulo, ej.codigo_inventario, " +
                        "ej.ubicacion, ej.estado, ej.fecha_registro " +
                        "FROM ejemplares ej " +
                        "INNER JOIN libros l ON ej.id_libro = l.id_libro " +
                        "ORDER BY ej.id_ejemplar DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Ejemplar ejemplar = new Ejemplar();

                ejemplar.setIdEjemplar(cursor.getInt(0));
                ejemplar.setIdLibro(cursor.getInt(1));
                ejemplar.setTituloLibro(cursor.getString(2));
                ejemplar.setCodigoInventario(cursor.getString(3));
                ejemplar.setUbicacion(cursor.getString(4));
                ejemplar.setEstado(cursor.getString(5));
                ejemplar.setFechaRegistro(cursor.getString(6));

                lista.add(ejemplar);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarEjemplar(int idEjemplar, int idLibro, String codigoInventario, String ubicacion, String estado, String fechaRegistro) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("id_libro", idLibro);
        valores.put("codigo_inventario", codigoInventario);
        valores.put("ubicacion", ubicacion);
        valores.put("estado", estado);
        valores.put("fecha_registro", fechaRegistro);

        int resultado = db.update(
                "ejemplares",
                valores,
                "id_ejemplar = ?",
                new String[]{String.valueOf(idEjemplar)}
        );

        db.close();

        return resultado > 0;
    }

    public boolean eliminarEjemplar(int idEjemplar) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete(
                "ejemplares",
                "id_ejemplar = ?",
                new String[]{String.valueOf(idEjemplar)}
        );

        db.close();

        return resultado > 0;
    }
}