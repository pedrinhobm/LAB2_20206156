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
        // como está botonAccion y boton de estadisitcas
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
                comenzarJuego(); // al colocar la opcion de jugar, aparecen las 12 celdas
            } else { // y de la misma manera ...
                if (juegoIniciado) { // medimos el tiempo de la partida
                    // aqui si va medir el tiempo que te demoras en ganar, perder o detener la partida
                    // la funcion ha sido muy similar al de arquitectura de computadoras
                    // cuando se queria analizar rendimiento de programas
                    long tiempoTranscurrido = (SystemClock.elapsedRealtime() - tiempoInicio) / 1000;
                    ResultActivity resultado = new ResultActivity(false, tiempoTranscurrido, intentos, temaActual);
                    resultado.setCancelado(true); // es por eso que el resultado final se guarda y registra
                    StatsActivity.agregarResultado(resultado); // a la condicion del estado de juego
                }
                finish(); //cuando colocar "Nuevo juego", retornamos al menu principal
            }
        });

        // Como indique anterior mente, aquí si use IA
        // porque nos agregaron una opcion de cambiar estado de JUGAR -> NUEVO JUEGO
        // y lo que pense es que al hcer click en ese boton y recien se habilitan las 12 celdas 3x4
        // para elegir 1 palabra del diccionario
        contenedorPalabras.setVisibility(View.INVISIBLE);
        deshabilitarCeldas();
        temaActual = getIntent().getStringExtra("TEMA");
        configurarJuego();

    }

    // igualmente pasa para la celda
    // habilitamos las celdas de las 12 palabras al presionar "Jugar"
    // Obviamente en "Nuevo Juego" nos lleva ala pagina principal para escoger 3 opciones

    private void comenzarJuego() {
        juegoIniciado = true; // esa es la funcion que tambien se implemento
        botonAccion.setText("Nuevo Juego"); // el booleano sirve para cambiar de estado del boton del Telememo
        contenedorPalabras.setVisibility(View.VISIBLE);
        habilitarCeldas();
        tiempoInicio = SystemClock.elapsedRealtime(); // aqui iniciamos el tiempo a tardar de la partida
        palabrasSeleccionadas.clear(); // como en arquitectura
        intentos = 0; // para esta funcion agregamos el intento y si el flujo de buscar la siguiente palabra es correcto o no
        indiceSiguientePalabra = 0;
        configurarJuego();
    }

    private void habilitarCeldas() { // aqui guarda las palabras en el contenedor de las 12 celdas
        for (int i = 0; i < contenedorPalabras.getChildCount(); i++) {
            View child = contenedorPalabras.getChildAt(i);
            child.setEnabled(true);
            if (child instanceof Button) { // el signo de interrogación permite ocultar la palabra
                ((Button) child).setText("?"); // de la oracion elegida aleatoriamente del diccionario
            } // el signo ? cambia al momento que presionas la celda
        }
    }

    private void deshabilitarCeldas() {
        for (int i = 0; i < contenedorPalabras.getChildCount(); i++) {
            View child = contenedorPalabras.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void configurarJuego() { // para la configuracion del juego se contará desde el inicio de la oracion
        oracionActual = obtenerOracionAleatoria(temaActual); // eso va de acuerdo a la opcion que elejiste
        List<String> todasPalabras = new ArrayList<>(oracionActual); // y ha se selecciona una de las 2 oraciones

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

    // para el manejo del juego si use inteligencia  dado que se veia complicado
    // como distribuir los estados al culminar una partida
    // de la misma manera para distribuir la cantidad de intentos restantes
    // cuando elegia la palabra incorrecta al anterior
    private void manejarClicPalabra(View vista) {
        Button botonClicado = (Button) vista; // aqui se muestra la palabra seleccionada
        String palabraClicada = (String) botonClicado.getTag(); // de la oracion lo vamos a dividir
        botonClicado.setText(palabraClicada);

        if (palabraClicada.equals(oracionActual.get(indiceSiguientePalabra))) {
            botonClicado.setEnabled(false); // en esta selectiva revisamos si la palabra siguiente
            palabrasSeleccionadas.add(botonClicado); // es correcta a la secuencia
            indiceSiguientePalabra++;

            if (indiceSiguientePalabra == oracionActual.size()) { // verificamos si completó toda la oración
                terminarJuego(false); // Ganó el juego con la opción falsa de cancelado
            }
        } else { // aqui a selectiva indica cuando elijes una palabra incorrecta
            intentos++; // los intentos aumentan
            TextView textoMensaje = findViewById(R.id.texto_mensaje); // y se envia como texto la cantidad de intentos restantes
            textoMensaje.setText("Te quedan " + (3 - intentos) + " intentos"); // al sumar esa iterativa se descuentan hasta llegar a cero para perder el juego

            handler.postDelayed(() -> { // en esta opción ocultamos las palabras cuando te equivocas
                for (Button boton : botonesPalabras) { // es por ello que vuelvo a colocar
                    boton.setText("?"); // el signo de interrogacion como estaba en un principio
                    boton.setEnabled(true);
                }

                palabrasSeleccionadas.clear(); // la opcion de clear indica que para reiniciar el sistema del juego
                indiceSiguientePalabra = 0;

                if (intentos >= 3) { // como indique anteriormente, los intentos se incrementan
                    terminarJuego(false); // y si llegas a 3 equivocaciones, perdiste el juego
                }
            }, 500);
        }
    }

    private List<String> obtenerOracionAleatoria(String tema) {
        String[] oraciones; // es es el diccionario de las oraciones por tema

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
        // agregamos la variable seleccionada para que el juego elija la oracion aleatoria
        String seleccionada = oraciones[new Random().nextInt(oraciones.length)];
        return new ArrayList<>(List.of(seleccionada.split(" ")));
    }

    private void terminarJuego(boolean cancelado) { // este es la funcion de acabar el juego , asi ganes , canceles o pierdes
        long tiempoTranscurrido = (SystemClock.elapsedRealtime() - tiempoInicio) / 1000; // esta funcion parte desde el tiempo inicial hasta finalizar
        TextView textoMensaje = findViewById(R.id.texto_mensaje); // para medir el tiempo, use systemclock.elapsed time

        if (cancelado) { // para la opcin de cancelado , usamos el booleano de cancelado para que lo registre en las estadisticas
            ResultActivity resultado = new ResultActivity(false, tiempoTranscurrido, intentos, temaActual);
            resultado.setCancelado(true); // aqui se dirije a la funcion que agrega el resultado de cancelado
            StatsActivity.agregarResultado(resultado);
            finish(); // la siguiente seelctiva es cuando la ultima palabra llega a su instancia final
        } else if (indiceSiguientePalabra == oracionActual.size()) { // es por eso que sacan el tamaño de la orgacion actial que es 12
            textoMensaje.setText("Ganó / Terminó en " + tiempoTranscurrido + "s Intentos: " + (intentos + 1)); // y asi indica que ganamos
            ResultActivity resultado = new ResultActivity(true, tiempoTranscurrido, intentos + 1, temaActual);
            StatsActivity.agregarResultado(resultado);
        } else { // y si no sucede de los anteriore casos , perdimos de frente con los intentos
            textoMensaje.setText("Perdió / Terminó en " + tiempoTranscurrido + "s");
            ResultActivity resultado = new ResultActivity(false, tiempoTranscurrido, intentos + 1, temaActual);
            StatsActivity.agregarResultado(resultado); // en todos los casos vemos que el resultado final se guarda en la vista de estadistica
        } // por eso usamos StatsActivity java

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