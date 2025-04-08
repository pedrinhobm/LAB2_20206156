package com.example.lab2_20206156;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    // aqui asignamos la accion de los botones de la pagina principal
    // que nos llevará a la vista del juego telememo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button botonSoftware = findViewById(R.id.boton_software);
        Button botonCiberseguridad = findViewById(R.id.boton_ciberseguridad);
        Button botonOpticas = findViewById(R.id.boton_opticas);

        botonSoftware.setOnClickListener(v -> iniciarJuego("Software"));
        botonCiberseguridad.setOnClickListener(v -> iniciarJuego("Ciberseguridad"));
        botonOpticas.setOnClickListener(v -> iniciarJuego("Ópticas"));
    }

    // como vamos a cambiar de vista
    // usaremos Intent explicito con la tematica seleccionada
    // ya sea software , ciber , opticas
    private void iniciarJuego(String tema) {
        Intent intent = new Intent(this, TeleMemoActivity.class);
        intent.putExtra("TEMA", tema);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}