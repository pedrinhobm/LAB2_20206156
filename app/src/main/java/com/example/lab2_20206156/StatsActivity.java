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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // aqui encontramos la accion para el retroseso de vista
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        LinearLayout contenedorHistorial = findViewById(R.id.contenedor_historial);
        contenedorHistorial.removeAllViews();

        for (int i = 0; i < resultados.size(); i++) {
            ResultActivity resultado = resultados.get(i); // aqui obtenemos el numero de resultado y juegos
            TextView texto = new TextView(this);  // por eso se usa un for para contabilizar las partidas

            String estado = resultado.isGanado() ? "Ganó" : // como texto indica que ha sucedo al culminar el juego
                    resultado.isCancelado() ? "Canceló" : "Perdió"; // si ganamos o se cancelo o perdio

            String textoHistorial = "Juego " + (i + 1) + ": " + estado; // de la iterativa anterior contabilizan el numero de partidas
            if (!resultado.isCancelado()) {
                textoHistorial += " / Terminó en " + resultado.getTiempo() + "s"; // aqui se registra el tiempo de demora
                if (resultado.isGanado()) { // aqui se registra el numero de intentos
                    textoHistorial += " Intentos: " + resultado.getIntentos();
                }
            }

            texto.setText(textoHistorial);
            texto.setPadding(0, 8, 0, 8);
            contenedorHistorial.addView(texto);
        }

        Button botonNuevoJuego = findViewById(R.id.boton_nuevo_juego); // tambien encontramos el nuevo juego
        botonNuevoJuego.setOnClickListener(v -> {  // con el boton que te permite cambiar de vista al menu principal
            Intent intent = new Intent(this, MainActivity.class); // por eso usamos intent para cambiar de vista
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // y regresamos para elegir otra vez la opcion de juego
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void agregarResultado(ResultActivity resultado) {
        resultados.add(resultado); // aqui esta el resultado obtenido terminado del juego
    }
}