package com.example.act7

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GuardarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardar)

        val tvPalabra = findViewById<TextView>(R.id.tvPalabraRecibida)
        val tvFonetica = findViewById<TextView>(R.id.tvFoneticaRecibida)
        val tvCategoria = findViewById<TextView>(R.id.tvCategoriaRecibida)
        val btnConfirmarBD = findViewById<Button>(R.id.btnConfirmarBD)

        // Recibir la información proveniente del Intent de DictionaryActivity
        val palabra = intent.getStringExtra("EXTRA_PALABRA") ?: ""
        val fonetica = intent.getStringExtra("EXTRA_FONETICA") ?: ""
        val categoria = intent.getStringExtra("EXTRA_CATEGORIA") ?: ""

        // Mostrar la información de la API recibida
        tvPalabra.text = "Palabra: $palabra"
        tvFonetica.text = "Fonética: $fonetica"
        tvCategoria.text = "Categoría: $categoria"

        // Botón simulado para la Base de Datos
        btnConfirmarBD.setOnClickListener {
            Toast.makeText(
                this,
                "Información de '$palabra' lista para guardar en la BD",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}