package com.example.lab2_20206156;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TeleMemoActivity extends AppCompatActivity {

    private String temaActual;
    private List<String> oracionActual;
    private List<Button> palabrasSeleccionadas = new ArrayList<>();
    private List<Button> botonesPalabras = new ArrayList<>();
    private int intentos = 0;
    private long tiempoInicio;
    private int indiceSiguientePalabra = 0;
    private Handler handler = new Handler();

    // aquí si he usado Inteligencia Artificial debido a la dificultad de cambiar
    // de estado del botón de accion "Jugar" a "Nuevo Juego"
    // es por eso que le agrego un boton de accion y un booleano que indica cuando inicio el juego
    private GridLayout contenedorPalabras;
    private Button botonAccion;
    private boolean juegoIniciado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telememo); // nos dirigiamos al xml del juego

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // En la vista del juego usaremos el boton de acción y el botón con el ícono de estadisticas
        // como está botonAccion y boton de estadisitcas btn
        botonAccion = findViewById(R.id.boton_accion);
        contenedorPalabras = findViewById(R.id.contenedor_palabras);
        ImageView btnEstadisticas = findViewById(R.id.btn_estadisticas);

        // Aquí usamos un listener  e intent , eso si lo aplique normal
        // ya que es parte de movilizarnos a otra vista que es de tu estadistica de juego
        btnEstadisticas.setOnClickListener(v -> {
            startActivity(new Intent(this, StatsActivity.class));
        });

        botonAccion.setOnClickListener(v -> {
            if (botonAccion.getText().toString().equals("Jugar")) {
                comenzarJuego();
            } else {
                if (juegoIniciado) {
                    // Registrar como cancelado antes de salir
                    long tiempoTranscurrido = (SystemClock.elapsedRealtime() - tiempoInicio) / 1000;
                    ResultActivity resultado = new ResultActivity(false, tiempoTranscurrido, intentos, temaActual);
                    resultado.setCancelado(true);
                    StatsActivity.agregarResultado(resultado);
                }
                finish();// Volver al menú principal
            }
        });

        // Estado inicial
        contenedorPalabras.setVisibility(View.INVISIBLE);
        deshabilitarCeldas();
        temaActual = getIntent().getStringExtra("TEMA");
        configurarJuego();

    }

    private void comenzarJuego() {
        juegoIniciado = true;
        // 1. Cambiar texto del botón
        botonAccion.setText("Nuevo Juego");

        // 2. Mostrar y habilitar el grid
        contenedorPalabras.setVisibility(View.VISIBLE);
        habilitarCeldas();

        // 3. Iniciar temporizador
        tiempoInicio = SystemClock.elapsedRealtime();

        // 4. Reiniciar variables del juego
        palabrasSeleccionadas.clear();
        intentos = 0;
        indiceSiguientePalabra = 0;

        // 5. Configurar las palabras (si es necesario)
        configurarJuego();
    }

    private void habilitarCeldas() {
        for (int i = 0; i < contenedorPalabras.getChildCount(); i++) {
            View child = contenedorPalabras.getChildAt(i);
            child.setEnabled(true);
            if (child instanceof Button) {
                ((Button) child).setText("?"); // Resetear texto
            }
        }
    }

    private void deshabilitarCeldas() {
        for (int i = 0; i < contenedorPalabras.getChildCount(); i++) {
            View child = contenedorPalabras.getChildAt(i);
            child.setEnabled(false);
        }
    }





    private void configurarJuego() {
        oracionActual = obtenerOracionAleatoria(temaActual);
        List<String> todasPalabras = new ArrayList<>(oracionActual);

        while(todasPalabras.size() < 12) {
            todasPalabras.add("palabra" + (todasPalabras.size() + 1));
        }

        Collections.shuffle(todasPalabras);

        GridLayout contenedor = findViewById(R.id.contenedor_palabras);
        contenedor.removeAllViews();
        botonesPalabras.clear();
        palabrasSeleccionadas.clear();
        indiceSiguientePalabra = 0;
        intentos = 0;

        TextView textoMensaje = findViewById(R.id.texto_mensaje);
        textoMensaje.setText("");

        for (String palabra : todasPalabras) {
            Button boton = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            boton.setLayoutParams(params);
            boton.setTag(palabra);
            boton.setText("?");
            boton.setOnClickListener(this::manejarClicPalabra);
            contenedor.addView(boton);
            botonesPalabras.add(boton);
        }
    }


    private void manejarClicPalabra(View vista) {
        Button botonClicado = (Button) vista;
        String palabraClicada = (String) botonClicado.getTag();

        // Mostrar la palabra seleccionada
        botonClicado.setText(palabraClicada);

        // Verificar si es la siguiente palabra correcta en la secuencia
        if (palabraClicada.equals(oracionActual.get(indiceSiguientePalabra))) {
            // PALABRA CORRECTA
            botonClicado.setEnabled(false);
            palabrasSeleccionadas.add(botonClicado);
            indiceSiguientePalabra++;

            // Verificar si completó toda la oración
            if (indiceSiguientePalabra == oracionActual.size()) {
                terminarJuego(false); // Ganó el juego
            }
        } else {
            // PALABRA INCORRECTA
            intentos++;
            TextView textoMensaje = findViewById(R.id.texto_mensaje);
            textoMensaje.setText("Te quedan " + (3 - intentos) + " intentos");

            // Ocultar TODAS las palabras después de 500ms
            handler.postDelayed(() -> {
                for (Button boton : botonesPalabras) {
                    boton.setText("?");
                    boton.setEnabled(true);
                }

                // Reiniciar el progreso
                palabrasSeleccionadas.clear();
                indiceSiguientePalabra = 0;

                // Verificar si perdió
                if (intentos >= 3) {
                    terminarJuego(false); // Perdió el juego
                }
            }, 500);
        }
    }

    private List<String> obtenerOracionAleatoria(String tema) {
        String[] oraciones;

        switch (tema) {
            case "Software":
                oraciones = new String[]{
                        "La fibra óptica envía datos a gran velocidad evitando cualquier interferencia eléctrica",
                        "Los amplificadores EDFA mejoran la señal óptica en redes de larga distancia"
                };
                break;
            case "Ciberseguridad":
                oraciones = new String[]{
                        "Una VPN encripta tu conexión para navegar de forma anónima y segura",
                        "El ataque DDoS satura servidores con tráfico falso y causa caídas masivas"
                };
                break;
            case "Ópticas":
                oraciones = new String[]{
                        "Los fragmentos reutilizan partes de pantalla en distintas actividades de la app",
                        "Los intents permiten acceder a apps como la cámara o WhatsApp directamente"
                };
                break;
            default:
                oraciones = new String[]{""};
        }

        String seleccionada = oraciones[new Random().nextInt(oraciones.length)];
        return new ArrayList<>(List.of(seleccionada.split(" ")));
    }

    private void terminarJuego(boolean cancelado) {
        long tiempoTranscurrido = (SystemClock.elapsedRealtime() - tiempoInicio) / 1000;
        TextView textoMensaje = findViewById(R.id.texto_mensaje);

        if (cancelado) {
            ResultActivity resultado = new ResultActivity(false, tiempoTranscurrido, intentos, temaActual);
            resultado.setCancelado(true);
            StatsActivity.agregarResultado(resultado);
            finish();
        } else if (indiceSiguientePalabra == oracionActual.size()) {
            textoMensaje.setText("Ganó / Terminó en " + tiempoTranscurrido + "s Intentos: " + (intentos + 1));
            ResultActivity resultado = new ResultActivity(true, tiempoTranscurrido, intentos + 1, temaActual);
            StatsActivity.agregarResultado(resultado);
        } else {
            textoMensaje.setText("Perdió / Terminó en " + tiempoTranscurrido + "s");
            ResultActivity resultado = new ResultActivity(false, tiempoTranscurrido, intentos + 1, temaActual);
            StatsActivity.agregarResultado(resultado);
        }

        for (Button boton : botonesPalabras) {
            boton.setEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}