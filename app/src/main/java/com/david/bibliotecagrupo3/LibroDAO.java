package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class LibroDAO {

    private DBHelper dbHelper;

    public LibroDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertarLibro(String titulo, String isbn, int anio, String descripcion,
                                 int stock, String estado, int idAutor,
                                 int idEditorial, int idCategoria) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long idLibro = -1;

        db.beginTransaction();

        try {
            ContentValues valores = new ContentValues();
            valores.put("titulo", titulo);
            valores.put("isbn", isbn);
            valores.put("anio_publicacion", anio);
            valores.put("descripcion", descripcion);
            valores.put("stock", stock);
            valores.put("estado", estado);
            valores.put("id_editorial", idEditorial);
            valores.put("id_categoria", idCategoria);

            idLibro = db.insert("libros", null, valores);

            if (idLibro != -1) {
                ContentValues valoresAutor = new ContentValues();
                valoresAutor.put("id_libro", idLibro);
                valoresAutor.put("id_autor", idAutor);

                db.insert("libro_autor", null, valoresAutor);
                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            idLibro = -1;
        } finally {
            db.endTransaction();
            db.close();
        }

        return idLibro != -1;
    }

    public ArrayList<Libro> mostrarLibros() {

        ArrayList<Libro> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT l.id_libro, l.titulo, l.isbn, l.anio_publicacion, l.descripcion, " +
                        "l.stock, l.estado, " +
                        "a.id_autor, a.nombres || ' ' || a.apellidos AS autor, " +
                        "e.id_editorial, e.nombre AS editorial, " +
                        "c.id_categoria, c.nombre AS categoria " +
                        "FROM libros l " +
                        "LEFT JOIN libro_autor la ON l.id_libro = la.id_libro " +
                        "LEFT JOIN autores a ON la.id_autor = a.id_autor " +
                        "LEFT JOIN editoriales e ON l.id_editorial = e.id_editorial " +
                        "LEFT JOIN categorias c ON l.id_categoria = c.id_categoria " +
                        "ORDER BY l.id_libro DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Libro libro = new Libro();

                libro.setIdLibro(cursor.getInt(0));
                libro.setTitulo(cursor.getString(1));
                libro.setIsbn(cursor.getString(2));
                libro.setAnio(cursor.getInt(3));
                libro.setDescripcion(cursor.getString(4));
                libro.setStock(cursor.getInt(5));
                libro.setEstado(cursor.getString(6));

                libro.setIdAutor(cursor.getInt(7));
                libro.setNombreAutor(cursor.getString(8));

                libro.setIdEditorial(cursor.getInt(9));
                libro.setNombreEditorial(cursor.getString(10));

                libro.setIdCategoria(cursor.getInt(11));
                libro.setNombreCategoria(cursor.getString(12));

                lista.add(libro);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarLibro(int idLibro, String titulo, String isbn, int anio,
                                   String descripcion, int stock, String estado,
                                   int idAutor, int idEditorial, int idCategoria) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            ContentValues valores = new ContentValues();
            valores.put("titulo", titulo);
            valores.put("isbn", isbn);
            valores.put("anio_publicacion", anio);
            valores.put("descripcion", descripcion);
            valores.put("stock", stock);
            valores.put("estado", estado);
            valores.put("id_editorial", idEditorial);
            valores.put("id_categoria", idCategoria);

            int resultado = db.update(
                    "libros",
                    valores,
                    "id_libro = ?",
                    new String[]{String.valueOf(idLibro)}
            );

            db.delete(
                    "libro_autor",
                    "id_libro = ?",
                    new String[]{String.valueOf(idLibro)}
            );

            ContentValues valoresAutor = new ContentValues();
            valoresAutor.put("id_libro", idLibro);
            valoresAutor.put("id_autor", idAutor);

            db.insert("libro_autor", null, valoresAutor);

            if (resultado > 0) {
                correcto = true;
                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            correcto = false;
        } finally {
            db.endTransaction();
            db.close();
        }

        return correcto;
    }

    public boolean eliminarLibro(int idLibro) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            db.delete(
                    "libro_autor",
                    "id_libro = ?",
                    new String[]{String.valueOf(idLibro)}
            );

            int resultado = db.delete(
                    "libros",
                    "id_libro = ?",
                    new String[]{String.valueOf(idLibro)}
            );

            if (resultado > 0) {
                correcto = true;
                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            correcto = false;
        } finally {
            db.endTransaction();
            db.close();
        }

        return correcto;
    }
}