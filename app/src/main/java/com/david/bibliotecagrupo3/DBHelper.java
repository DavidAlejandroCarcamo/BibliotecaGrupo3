package com.david.bibliotecagrupo3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "biblioteca.db";
    private static final int VERSION_BD = 2;

    public DBHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE roles (" +
                "id_rol INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE, " +
                "descripcion TEXT)");

        db.execSQL("CREATE TABLE usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_rol INTEGER NOT NULL, " +
                "usuario TEXT NOT NULL UNIQUE, " +
                "nombres TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, " +
                "correo TEXT NOT NULL UNIQUE, " +
                "telefono TEXT, " +
                "password_hash TEXT NOT NULL, " +
                "pregunta_recuperacion TEXT, " +
                "respuesta_hash TEXT, " +
                "estado TEXT NOT NULL, " +
                "fecha_registro TEXT NOT NULL, " +
                "FOREIGN KEY(id_rol) REFERENCES roles(id_rol))");

        db.execSQL("CREATE TABLE sesiones (" +
                "id_sesion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "token_sesion TEXT NOT NULL UNIQUE, " +
                "fecha_inicio TEXT NOT NULL, " +
                "fecha_expiracion TEXT, " +
                "estado TEXT NOT NULL, " +
                "FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario))");

        db.execSQL("CREATE TABLE autores (" +
                "id_autor INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombres TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, " +
                "nacionalidad TEXT)");

        db.execSQL("CREATE TABLE editoriales (" +
                "id_editorial INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE, " +
                "direccion TEXT, " +
                "telefono TEXT)");

        db.execSQL("CREATE TABLE categorias (" +
                "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE, " +
                "descripcion TEXT)");

        db.execSQL("CREATE TABLE libros (" +
                "id_libro INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_editorial INTEGER, " +
                "id_categoria INTEGER, " +
                "isbn TEXT UNIQUE, " +
                "titulo TEXT NOT NULL, " +
                "anio_publicacion INTEGER, " +
                "descripcion TEXT, " +
                "estado TEXT NOT NULL, " +
                "stock INTEGER NOT NULL, " +
                "FOREIGN KEY(id_editorial) REFERENCES editoriales(id_editorial), " +
                "FOREIGN KEY(id_categoria) REFERENCES categorias(id_categoria))");

        db.execSQL("CREATE TABLE libro_autor (" +
                "id_libro INTEGER NOT NULL, " +
                "id_autor INTEGER NOT NULL, " +
                "PRIMARY KEY(id_libro, id_autor), " +
                "FOREIGN KEY(id_libro) REFERENCES libros(id_libro), " +
                "FOREIGN KEY(id_autor) REFERENCES autores(id_autor))");

        db.execSQL("CREATE TABLE ejemplares (" +
                "id_ejemplar INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_libro INTEGER NOT NULL, " +
                "codigo_inventario TEXT NOT NULL UNIQUE, " +
                "ubicacion TEXT, " +
                "estado TEXT NOT NULL, " +
                "fecha_registro TEXT NOT NULL, " +
                "FOREIGN KEY(id_libro) REFERENCES libros(id_libro))");

        db.execSQL("CREATE TABLE prestamos (" +
                "id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "id_bibliotecario INTEGER NOT NULL, " +
                "fecha_prestamo TEXT NOT NULL, " +
                "fecha_vencimiento TEXT NOT NULL, " +
                "estado TEXT NOT NULL, " +
                "observaciones TEXT, " +
                "FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario), " +
                "FOREIGN KEY(id_bibliotecario) REFERENCES usuarios(id_usuario))");

        db.execSQL("CREATE TABLE detalle_prestamo (" +
                "id_detalle INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_prestamo INTEGER NOT NULL, " +
                "id_ejemplar INTEGER NOT NULL, " +
                "estado TEXT NOT NULL, " +
                "FOREIGN KEY(id_prestamo) REFERENCES prestamos(id_prestamo), " +
                "FOREIGN KEY(id_ejemplar) REFERENCES ejemplares(id_ejemplar))");

        db.execSQL("CREATE TABLE devoluciones (" +
                "id_devolucion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_detalle INTEGER NOT NULL UNIQUE, " +
                "fecha_devolucion TEXT NOT NULL, " +
                "condicion_ejemplar TEXT NOT NULL, " +
                "observaciones TEXT, " +
                "FOREIGN KEY(id_detalle) REFERENCES detalle_prestamo(id_detalle))");

        insertarDatosIniciales(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {

        db.execSQL("INSERT INTO roles(nombre, descripcion) VALUES " +
                "('Administrador', 'Usuario con acceso completo al sistema')");

        db.execSQL("INSERT INTO roles(nombre, descripcion) VALUES " +
                "('Bibliotecario', 'Usuario encargado de prestamos y devoluciones')");

        db.execSQL("INSERT INTO usuarios(id_rol, usuario, nombres, apellidos, correo, telefono, password_hash, pregunta_recuperacion, respuesta_hash, estado, fecha_registro) VALUES " +
                "(1, 'admin', 'Admin', 'Principal', 'admin@biblioteca.com', '99999999', '1234', 'Color favorito', 'azul', 'Activo', '2026-08-03')");

        db.execSQL("INSERT INTO usuarios(id_rol, usuario, nombres, apellidos, correo, telefono, password_hash, pregunta_recuperacion, respuesta_hash, estado, fecha_registro) VALUES " +
                "(2, 'bibliotecario', 'Bibliotecario', 'General', 'bibliotecario@biblioteca.com', '88888888', '2026', 'Color favorito', 'verde', 'Activo', '2026-08-03')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS devoluciones");
        db.execSQL("DROP TABLE IF EXISTS detalle_prestamo");
        db.execSQL("DROP TABLE IF EXISTS prestamos");
        db.execSQL("DROP TABLE IF EXISTS ejemplares");
        db.execSQL("DROP TABLE IF EXISTS libro_autor");
        db.execSQL("DROP TABLE IF EXISTS libros");
        db.execSQL("DROP TABLE IF EXISTS categorias");
        db.execSQL("DROP TABLE IF EXISTS editoriales");
        db.execSQL("DROP TABLE IF EXISTS autores");
        db.execSQL("DROP TABLE IF EXISTS sesiones");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS roles");

        onCreate(db);
    }
}