package com.david.bibliotecagrupo3;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReportesActivity extends AppCompatActivity {

    private TextView txtTotalLibros, txtTotalAutores, txtTotalEditoriales, txtTotalCategorias;
    private TextView txtEjemplaresDisponibles, txtEjemplaresPrestados, txtEjemplaresDanados;
    private TextView txtPrestamosActivos, txtPrestamosDevueltos, txtPrestamosCancelados;
    private TextView txtTotalDevoluciones, txtUsuariosActivos, txtUsuariosInactivos;

    private ReporteDAO reporteDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reportes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTotalLibros = findViewById(R.id.txtTotalLibros);
        txtTotalAutores = findViewById(R.id.txtTotalAutores);
        txtTotalEditoriales = findViewById(R.id.txtTotalEditoriales);
        txtTotalCategorias = findViewById(R.id.txtTotalCategorias);

        txtEjemplaresDisponibles = findViewById(R.id.txtEjemplaresDisponibles);
        txtEjemplaresPrestados = findViewById(R.id.txtEjemplaresPrestados);
        txtEjemplaresDanados = findViewById(R.id.txtEjemplaresDanados);

        txtPrestamosActivos = findViewById(R.id.txtPrestamosActivos);
        txtPrestamosDevueltos = findViewById(R.id.txtPrestamosDevueltos);
        txtPrestamosCancelados = findViewById(R.id.txtPrestamosCancelados);

        txtTotalDevoluciones = findViewById(R.id.txtTotalDevoluciones);
        txtUsuariosActivos = findViewById(R.id.txtUsuariosActivos);
        txtUsuariosInactivos = findViewById(R.id.txtUsuariosInactivos);

        reporteDAO = new ReporteDAO(this);

        cargarReporte();
    }

    private void cargarReporte() {

        txtTotalLibros.setText("Libros registrados: " + reporteDAO.totalLibros());
        txtTotalAutores.setText("Autores registrados: " + reporteDAO.totalAutores());
        txtTotalEditoriales.setText("Editoriales registradas: " + reporteDAO.totalEditoriales());
        txtTotalCategorias.setText("Categorías registradas: " + reporteDAO.totalCategorias());

        txtEjemplaresDisponibles.setText("Ejemplares disponibles: " + reporteDAO.ejemplaresDisponibles());
        txtEjemplaresPrestados.setText("Ejemplares prestados: " + reporteDAO.ejemplaresPrestados());
        txtEjemplaresDanados.setText("Ejemplares dañados: " + reporteDAO.ejemplaresDanados());

        txtPrestamosActivos.setText("Préstamos activos: " + reporteDAO.prestamosActivos());
        txtPrestamosDevueltos.setText("Préstamos devueltos: " + reporteDAO.prestamosDevueltos());
        txtPrestamosCancelados.setText("Préstamos cancelados: " + reporteDAO.prestamosCancelados());

        txtTotalDevoluciones.setText("Devoluciones registradas: " + reporteDAO.totalDevoluciones());

        txtUsuariosActivos.setText("Usuarios activos: " + reporteDAO.usuariosActivos());
        txtUsuariosInactivos.setText("Usuarios inactivos: " + reporteDAO.usuariosInactivos());
    }

    public void actualizarReporte(View v) {
        cargarReporte();
        Toast.makeText(this, "Reporte actualizado", Toast.LENGTH_SHORT).show();
    }

    public void regresar(View v) {
        finish();
    }
}