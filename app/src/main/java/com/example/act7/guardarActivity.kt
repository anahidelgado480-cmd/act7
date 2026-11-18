package com.example.act7

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GuardarActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardar)

        // Inicializamos nuestra base de datos SQLite
        dbHelper = DatabaseHelper(this)

        val tvPalabra = findViewById<TextView>(R.id.tvPalabraRecibida)
        val tvFonetica = findViewById<TextView>(R.id.tvFoneticaRecibida)
        val tvCategoria = findViewById<TextView>(R.id.tvCategoriaRecibida)
        val btnConfirmarBD = findViewById<Button>(R.id.btnConfirmarBD)

        // Recibir la información proveniente del Intent de DictionaryActivity
        val palabra = intent.getStringExtra("EXTRA_PALABRA") ?: ""
        val fonetica = intent.getStringExtra("EXTRA_FONETICA") ?: ""
        val categoria = intent.getStringExtra("EXTRA_CATEGORIA") ?: ""

        // Generamos un ID único numérico basado en la palabra (para cumplir con las consultas por ID)
        val idApi = palabra.hashCode().toLong()

        // Mostrar la información de la API recibida en pantalla
        tvPalabra.text = "Palabra: $palabra"
        tvFonetica.text = "Fonética: $fonetica"
        tvCategoria.text = "Categoría: $categoria"

        // ==========================================
        // 1. VERIFICACIÓN INICIAL (Método del profesor)
        // ==========================================
        // Comprobamos si esta palabra del API ya se había guardado antes como favorita
        val yaExiste = dbHelper.existeFavorito(idApi)
        if (yaExiste) {
            btnConfirmarBD.text = "Eliminar de Favoritos"
        } else {
            btnConfirmarBD.text = "Guardar en Favoritos"
        }

        // Botón para realizar la acción en la Base de Datos SQLite
        btnConfirmarBD.setOnClickListener {
            // Verificamos el estado actual al hacer clic
            val existeActual = dbHelper.existeFavorito(idApi)

            if (existeActual) {
                // ==========================================
                // 2. MÉTODO DE ELIMINACIÓN (Del profesor)
                // ==========================================
                val eliminado = dbHelper.eliminarFavorito(idApi)
                if (eliminado) {
                    Toast.makeText(this, "Eliminado de tus favoritos", Toast.LENGTH_SHORT).show()
                    btnConfirmarBD.text = "Guardar en Favoritos"
                } else {
                    Toast.makeText(this, "Error al eliminar de la BD", Toast.LENGTH_SHORT).show()
                }
            } else {
                // ==========================================
                // 3. MÉTODO DE REGISTRO (Del profesor)
                // ==========================================
                // Guardamos únicamente lo más relevante del API: Palabra, Traducción y Fonética
                val guardado = dbHelper.guardarFavorito(
                    idApi = idApi,
                    palabra = palabra,
                    traduccion = categoria,
                    fonetica = fonetica // ¡El dato clave de tu API de fonética!
                )

                if (guardado) {
                    Toast.makeText(this, "¡Guardado en favoritos con éxito!", Toast.LENGTH_SHORT).show()
                    btnConfirmarBD.text = "Eliminar de Favoritos"
                } else {
                    Toast.makeText(this, "Error al guardar en la BD", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}