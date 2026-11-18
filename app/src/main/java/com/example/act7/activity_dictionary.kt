package com.example.act7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DictionaryActivity : AppCompatActivity() {

    private lateinit var tvApiResult: TextView
    private lateinit var btnGuardarApi: Button

    // Variables globales para almacenar lo que responda la API
    private var palabraActual = ""
    private var foneticaActual = ""
    private var categoriaActual = "Animals" // Como tus botones son de animales

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        tvApiResult = findViewById(R.id.tvApiResult)
        btnGuardarApi = findViewById(R.id.btnGuardarApi)

        val btncat: ImageButton = findViewById(R.id.btncat)
        val btnDog: ImageButton = findViewById(R.id.btnDog)
        val btnmonkey: ImageButton = findViewById(R.id.btnmonkey)
        val btnfish: ImageButton = findViewById(R.id.btnfish)
        val btnhorse: ImageButton = findViewById(R.id.btnhorse)
        val btnjif: ImageButton = findViewById(R.id.btnjif)

        // Eventos para buscar la palabra al presionar cada animal
        btncat.setOnClickListener { fetchWordData("cat") }
        btnDog.setOnClickListener { fetchWordData("dog") }
        btnmonkey.setOnClickListener { fetchWordData("monkey") }
        btnfish.setOnClickListener { fetchWordData("fish") }
        btnhorse.setOnClickListener { fetchWordData("horse") }
        btnjif.setOnClickListener { fetchWordData("giraffe") }

        // =========================================================================
        // AQUÍ CONFIGURAMOS EL BOTÓN PARA ENVIAR LOS DATOS A GuardarActivity (FUERA)
        // =========================================================================
        btnGuardarApi.setOnClickListener {
            if (palabraActual.isNotEmpty()) {
                val intent = Intent(this, GuardarActivity::class.java).apply {
                    putExtra("EXTRA_PALABRA", palabraActual)
                    putExtra("EXTRA_FONETICA", foneticaActual)
                    putExtra("EXTRA_CATEGORIA", categoriaActual)
                }
                startActivity(intent)
            } else {
                tvApiResult.text = "Por favor, selecciona un animal primero."
            }
        }
    }

    private fun fetchWordData(word: String) {
        tvApiResult.text = "Buscando $word..."

        lifecycleScope.launch {
            try {
                // Petición GET a la API usando Retrofit
                val response = RetrofitClient.apiService.getWordDetails(word)

                if (response.isNotEmpty()) {
                    val wordInfo = response[0]

                    // Guardamos en las variables globales
                    palabraActual = wordInfo.word ?: word
                    foneticaActual = wordInfo.phonetic ?: "Fonética no disponible"

                    // Mostramos el resultado en pantalla
                    tvApiResult.text = "Palabra: $palabraActual\n\nFonética: $foneticaActual"
                }
            } catch (e: Exception) {
                tvApiResult.text = "Error al conectar con la API: ${e.message}"
            }
        }
    }
}