package com.david.bibliotecagrupo3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReporteDAO {

    private DBHelper dbHelper;

    public ReporteDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    private int obtenerCantidad(String consulta) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(consulta, null);

        int cantidad = 0;

        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return cantidad;
    }

    public int totalLibros() {
        return obtenerCantidad("SELECT COUNT(*) FROM libros");
    }

    public int totalAutores() {
        return obtenerCantidad("SELECT COUNT(*) FROM autores");
    }

    public int totalEditoriales() {
        return obtenerCantidad("SELECT COUNT(*) FROM editoriales");
    }

    public int totalCategorias() {
        return obtenerCantidad("SELECT COUNT(*) FROM categorias");
    }

    public int usuariosActivos() {
        return obtenerCantidad("SELECT COUNT(*) FROM usuarios WHERE estado = 'Activo'");
    }

    public int usuariosInactivos() {
        return obtenerCantidad("SELECT COUNT(*) FROM usuarios WHERE estado = 'Inactivo'");
    }

    public int ejemplaresDisponibles() {
        return obtenerCantidad("SELECT COUNT(*) FROM ejemplares WHERE estado = 'Disponible'");
    }

    public int ejemplaresPrestados() {
        return obtenerCantidad("SELECT COUNT(*) FROM ejemplares WHERE estado = 'Prestado'");
    }

    public int ejemplaresDanados() {
        return obtenerCantidad("SELECT COUNT(*) FROM ejemplares WHERE estado = 'Dañado'");
    }

    public int prestamosActivos() {
        return obtenerCantidad("SELECT COUNT(*) FROM prestamos WHERE estado = 'Activo'");
    }

    public int prestamosDevueltos() {
        return obtenerCantidad("SELECT COUNT(*) FROM prestamos WHERE estado = 'Devuelto'");
    }

    public int prestamosCancelados() {
        return obtenerCantidad("SELECT COUNT(*) FROM prestamos WHERE estado = 'Cancelado'");
    }

    public int totalDevoluciones() {
        return obtenerCantidad("SELECT COUNT(*) FROM devoluciones");
    }
}