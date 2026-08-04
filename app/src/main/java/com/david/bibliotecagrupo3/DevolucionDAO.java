package com.david.bibliotecagrupo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DevolucionDAO {

    private DBHelper dbHelper;

    public DevolucionDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public ArrayList<Prestamo> mostrarPrestamosPendientes(int idDetalleActual) {

        ArrayList<Prestamo> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        if (idDetalleActual == 0) {
            cursor = db.rawQuery(
                    "SELECT p.id_prestamo, p.id_usuario, u.nombres || ' ' || u.apellidos, " +
                            "dp.id_detalle, dp.id_ejemplar, l.titulo, ej.codigo_inventario " +
                            "FROM prestamos p " +
                            "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                            "INNER JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                            "INNER JOIN ejemplares ej ON dp.id_ejemplar = ej.id_ejemplar " +
                            "INNER JOIN libros l ON ej.id_libro = l.id_libro " +
                            "WHERE p.estado = 'Activo' AND dp.estado = 'Prestado' " +
                            "ORDER BY p.id_prestamo DESC",
                    null
            );
        } else {
            cursor = db.rawQuery(
                    "SELECT p.id_prestamo, p.id_usuario, u.nombres || ' ' || u.apellidos, " +
                            "dp.id_detalle, dp.id_ejemplar, l.titulo, ej.codigo_inventario " +
                            "FROM prestamos p " +
                            "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                            "INNER JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                            "INNER JOIN ejemplares ej ON dp.id_ejemplar = ej.id_ejemplar " +
                            "INNER JOIN libros l ON ej.id_libro = l.id_libro " +
                            "WHERE (p.estado = 'Activo' AND dp.estado = 'Prestado') OR dp.id_detalle = ? " +
                            "ORDER BY p.id_prestamo DESC",
                    new String[]{String.valueOf(idDetalleActual)}
            );
        }

        if (cursor.moveToFirst()) {
            do {
                Prestamo prestamo = new Prestamo();

                prestamo.setIdPrestamo(cursor.getInt(0));
                prestamo.setIdUsuario(cursor.getInt(1));
                prestamo.setNombreUsuario(cursor.getString(2));
                prestamo.setIdDetalle(cursor.getInt(3));
                prestamo.setIdEjemplar(cursor.getInt(4));
                prestamo.setTituloLibro(cursor.getString(5));
                prestamo.setCodigoInventario(cursor.getString(6));

                lista.add(prestamo);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean insertarDevolucion(int idDetalle, int idPrestamo, int idEjemplar,
                                      String fechaDevolucion, String condicion,
                                      String observaciones) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            ContentValues valores = new ContentValues();
            valores.put("id_detalle", idDetalle);
            valores.put("fecha_devolucion", fechaDevolucion);
            valores.put("condicion_ejemplar", condicion);
            valores.put("observaciones", observaciones);

            long resultado = db.insert("devoluciones", null, valores);

            if (resultado != -1) {

                ContentValues detalle = new ContentValues();
                detalle.put("estado", "Devuelto");

                db.update(
                        "detalle_prestamo",
                        detalle,
                        "id_detalle = ?",
                        new String[]{String.valueOf(idDetalle)}
                );

                ContentValues prestamo = new ContentValues();
                prestamo.put("estado", "Devuelto");

                db.update(
                        "prestamos",
                        prestamo,
                        "id_prestamo = ?",
                        new String[]{String.valueOf(idPrestamo)}
                );

                ContentValues ejemplar = new ContentValues();

                if (condicion.equals("Bueno")) {
                    ejemplar.put("estado", "Disponible");
                } else {
                    ejemplar.put("estado", "Dañado");
                }

                db.update(
                        "ejemplares",
                        ejemplar,
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

    public ArrayList<Devolucion> mostrarDevoluciones() {

        ArrayList<Devolucion> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT d.id_devolucion, d.id_detalle, p.id_prestamo, dp.id_ejemplar, " +
                        "l.titulo, ej.codigo_inventario, " +
                        "u.nombres || ' ' || u.apellidos AS usuario, " +
                        "d.fecha_devolucion, d.condicion_ejemplar, d.observaciones " +
                        "FROM devoluciones d " +
                        "INNER JOIN detalle_prestamo dp ON d.id_detalle = dp.id_detalle " +
                        "INNER JOIN prestamos p ON dp.id_prestamo = p.id_prestamo " +
                        "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                        "INNER JOIN ejemplares ej ON dp.id_ejemplar = ej.id_ejemplar " +
                        "INNER JOIN libros l ON ej.id_libro = l.id_libro " +
                        "ORDER BY d.id_devolucion DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Devolucion devolucion = new Devolucion();

                devolucion.setIdDevolucion(cursor.getInt(0));
                devolucion.setIdDetalle(cursor.getInt(1));
                devolucion.setIdPrestamo(cursor.getInt(2));
                devolucion.setIdEjemplar(cursor.getInt(3));
                devolucion.setTituloLibro(cursor.getString(4));
                devolucion.setCodigoInventario(cursor.getString(5));
                devolucion.setNombreUsuario(cursor.getString(6));
                devolucion.setFechaDevolucion(cursor.getString(7));
                devolucion.setCondicionEjemplar(cursor.getString(8));
                devolucion.setObservaciones(cursor.getString(9));

                lista.add(devolucion);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarDevolucion(int idDevolucion, int idEjemplar,
                                        String fechaDevolucion, String condicion,
                                        String observaciones) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            ContentValues valores = new ContentValues();
            valores.put("fecha_devolucion", fechaDevolucion);
            valores.put("condicion_ejemplar", condicion);
            valores.put("observaciones", observaciones);

            int resultado = db.update(
                    "devoluciones",
                    valores,
                    "id_devolucion = ?",
                    new String[]{String.valueOf(idDevolucion)}
            );

            ContentValues ejemplar = new ContentValues();

            if (condicion.equals("Bueno")) {
                ejemplar.put("estado", "Disponible");
            } else {
                ejemplar.put("estado", "Dañado");
            }

            db.update(
                    "ejemplares",
                    ejemplar,
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

    public boolean eliminarDevolucion(int idDevolucion, int idDetalle, int idPrestamo, int idEjemplar) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean correcto = false;

        db.beginTransaction();

        try {
            int resultado = db.delete(
                    "devoluciones",
                    "id_devolucion = ?",
                    new String[]{String.valueOf(idDevolucion)}
            );

            ContentValues detalle = new ContentValues();
            detalle.put("estado", "Prestado");

            db.update(
                    "detalle_prestamo",
                    detalle,
                    "id_detalle = ?",
                    new String[]{String.valueOf(idDetalle)}
            );

            ContentValues prestamo = new ContentValues();
            prestamo.put("estado", "Activo");

            db.update(
                    "prestamos",
                    prestamo,
                    "id_prestamo = ?",
                    new String[]{String.valueOf(idPrestamo)}
            );

            ContentValues ejemplar = new ContentValues();
            ejemplar.put("estado", "Prestado");

            db.update(
                    "ejemplares",
                    ejemplar,
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