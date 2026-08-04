package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class PrestamoDAO {

    private DBHelper dbHelper;

    public PrestamoDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertarPrestamo(int idUsuario, int idBibliotecario, int idEjemplar,
                                    String fechaPrestamo, String fechaVencimiento,
                                    String estado, String observaciones) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            ContentValues valoresPrestamo = new ContentValues();
            valoresPrestamo.put("id_usuario", idUsuario);
            valoresPrestamo.put("id_bibliotecario", idBibliotecario);
            valoresPrestamo.put("fecha_prestamo", fechaPrestamo);
            valoresPrestamo.put("fecha_vencimiento", fechaVencimiento);
            valoresPrestamo.put("estado", estado);
            valoresPrestamo.put("observaciones", observaciones);

            long idPrestamo = db.insert("prestamos", null, valoresPrestamo);

            if (idPrestamo != -1) {
                ContentValues valoresDetalle = new ContentValues();
                valoresDetalle.put("id_prestamo", idPrestamo);
                valoresDetalle.put("id_ejemplar", idEjemplar);
                valoresDetalle.put("estado", "Prestado");

                db.insert("detalle_prestamo", null, valoresDetalle);

                ContentValues valoresEjemplar = new ContentValues();
                valoresEjemplar.put("estado", "Prestado");

                db.update(
                        "ejemplares",
                        valoresEjemplar,
                        "id_ejemplar = ?",
                        new String[]{String.valueOf(idEjemplar)}
                );

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

    public ArrayList<Prestamo> mostrarPrestamos() {

        ArrayList<Prestamo> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT p.id_prestamo, p.id_usuario, " +
                        "u.nombres || ' ' || u.apellidos AS usuario, " +
                        "p.id_bibliotecario, " +
                        "b.nombres || ' ' || b.apellidos AS bibliotecario, " +
                        "p.fecha_prestamo, p.fecha_vencimiento, p.estado, p.observaciones, " +
                        "dp.id_detalle, dp.id_ejemplar, l.titulo, ej.codigo_inventario " +
                        "FROM prestamos p " +
                        "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                        "INNER JOIN usuarios b ON p.id_bibliotecario = b.id_usuario " +
                        "INNER JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                        "INNER JOIN ejemplares ej ON dp.id_ejemplar = ej.id_ejemplar " +
                        "INNER JOIN libros l ON ej.id_libro = l.id_libro " +
                        "ORDER BY p.id_prestamo DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Prestamo prestamo = new Prestamo();

                prestamo.setIdPrestamo(cursor.getInt(0));
                prestamo.setIdUsuario(cursor.getInt(1));
                prestamo.setNombreUsuario(cursor.getString(2));
                prestamo.setIdBibliotecario(cursor.getInt(3));
                prestamo.setNombreBibliotecario(cursor.getString(4));
                prestamo.setFechaPrestamo(cursor.getString(5));
                prestamo.setFechaVencimiento(cursor.getString(6));
                prestamo.setEstado(cursor.getString(7));
                prestamo.setObservaciones(cursor.getString(8));
                prestamo.setIdDetalle(cursor.getInt(9));
                prestamo.setIdEjemplar(cursor.getInt(10));
                prestamo.setTituloLibro(cursor.getString(11));
                prestamo.setCodigoInventario(cursor.getString(12));

                lista.add(prestamo);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public ArrayList<Ejemplar> mostrarEjemplaresDisponibles(int idEjemplarActual) {

        ArrayList<Ejemplar> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        if (idEjemplarActual == 0) {
            cursor = db.rawQuery(
                    "SELECT ej.id_ejemplar, ej.id_libro, l.titulo, ej.codigo_inventario, " +
                            "ej.ubicacion, ej.estado, ej.fecha_registro " +
                            "FROM ejemplares ej INNER JOIN libros l ON ej.id_libro = l.id_libro " +
                            "WHERE ej.estado = 'Disponible' " +
                            "ORDER BY ej.id_ejemplar DESC",
                    null
            );
        } else {
            cursor = db.rawQuery(
                    "SELECT ej.id_ejemplar, ej.id_libro, l.titulo, ej.codigo_inventario, " +
                            "ej.ubicacion, ej.estado, ej.fecha_registro " +
                            "FROM ejemplares ej INNER JOIN libros l ON ej.id_libro = l.id_libro " +
                            "WHERE ej.estado = 'Disponible' OR ej.id_ejemplar = ? " +
                            "ORDER BY ej.id_ejemplar DESC",
                    new String[]{String.valueOf(idEjemplarActual)}
            );
        }

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

    public boolean actualizarPrestamo(int idPrestamo, int idDetalle, int idUsuario,
                                      int idBibliotecario, int idEjemplarNuevo,
                                      int idEjemplarAnterior, String fechaPrestamo,
                                      String fechaVencimiento, String estado,
                                      String observaciones) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            ContentValues valoresPrestamo = new ContentValues();
            valoresPrestamo.put("id_usuario", idUsuario);
            valoresPrestamo.put("id_bibliotecario", idBibliotecario);
            valoresPrestamo.put("fecha_prestamo", fechaPrestamo);
            valoresPrestamo.put("fecha_vencimiento", fechaVencimiento);
            valoresPrestamo.put("estado", estado);
            valoresPrestamo.put("observaciones", observaciones);

            int resultado = db.update(
                    "prestamos",
                    valoresPrestamo,
                    "id_prestamo = ?",
                    new String[]{String.valueOf(idPrestamo)}
            );

            ContentValues valoresDetalle = new ContentValues();
            valoresDetalle.put("id_ejemplar", idEjemplarNuevo);
            valoresDetalle.put("estado", "Prestado");

            db.update(
                    "detalle_prestamo",
                    valoresDetalle,
                    "id_detalle = ?",
                    new String[]{String.valueOf(idDetalle)}
            );

            if (idEjemplarNuevo != idEjemplarAnterior) {
                ContentValues anterior = new ContentValues();
                anterior.put("estado", "Disponible");

                db.update(
                        "ejemplares",
                        anterior,
                        "id_ejemplar = ?",
                        new String[]{String.valueOf(idEjemplarAnterior)}
                );
            }

            ContentValues nuevo = new ContentValues();

            if (estado.equals("Cancelado")) {
                nuevo.put("estado", "Disponible");
            } else {
                nuevo.put("estado", "Prestado");
            }

            db.update(
                    "ejemplares",
                    nuevo,
                    "id_ejemplar = ?",
                    new String[]{String.valueOf(idEjemplarNuevo)}
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

    public boolean eliminarPrestamo(int idPrestamo, int idEjemplar) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            db.delete(
                    "detalle_prestamo",
                    "id_prestamo = ?",
                    new String[]{String.valueOf(idPrestamo)}
            );

            int resultado = db.delete(
                    "prestamos",
                    "id_prestamo = ?",
                    new String[]{String.valueOf(idPrestamo)}
            );

            ContentValues valoresEjemplar = new ContentValues();
            valoresEjemplar.put("estado", "Disponible");

            db.update(
                    "ejemplares",
                    valoresEjemplar,
                    "id_ejemplar = ?",
                    new String[]{String.valueOf(idEjemplar)}
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