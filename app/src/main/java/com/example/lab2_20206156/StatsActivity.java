package com.example.lab2_20206156;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.Toolbar;
public class StatsActivity extends AppCompatActivity {

    private static final List<ResultActivity> resultados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Configurar Toolbar correctamente con AppCompat
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        LinearLayout contenedorHistorial = findViewById(R.id.contenedor_historial);
        contenedorHistorial.removeAllViews();

        for (int i = 0; i < resultados.size(); i++) {
            ResultActivity resultado = resultados.get(i);
            TextView texto = new TextView(this);

            String estado = resultado.isGanado() ? "Ganó" :
                    resultado.isCancelado() ? "Canceló" : "Perdió";

            String textoHistorial = "Juego " + (i + 1) + ": " + estado;
            if (!resultado.isCancelado()) {
                textoHistorial += " / Terminó en " + resultado.getTiempo() + "s";
                if (resultado.isGanado()) {
                    textoHistorial += " Intentos: " + resultado.getIntentos();
                }
            }

            texto.setText(textoHistorial);
            texto.setPadding(0, 8, 0, 8);
            contenedorHistorial.addView(texto);
        }

        Button botonNuevoJuego = findViewById(R.id.boton_nuevo_juego);
        botonNuevoJuego.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void agregarResultado(ResultActivity resultado) {
        resultados.add(resultado);
    }
}