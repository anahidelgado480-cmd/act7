package com.example.act7

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DictionaryActivity : AppCompatActivity() {

    private lateinit var tvApiResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        tvApiResult = findViewById(R.id.tvApiResult)

        val btncat: ImageButton = findViewById(R.id.btncat)
        val btnDog: ImageButton = findViewById(R.id.btnDog)
        val btnmonkey: ImageButton = findViewById(R.id.btnmonkey)
        val btnfish: ImageButton = findViewById(R.id.btnfish)
        val btnhorse: ImageButton = findViewById(R.id.btnhorse)
        val btnjif: ImageButton = findViewById(R.id.btnjif)


        btncat.setOnClickListener {
            fetchWordData("cat")
        }


        btnDog.setOnClickListener {
            fetchWordData("dog")
        }


        btnmonkey.setOnClickListener {
            fetchWordData("monkey")
        }

        btnfish.setOnClickListener {
            fetchWordData("fish")
        }

        btnhorse.setOnClickListener {
            fetchWordData("horse")
        }

        btnjif.setOnClickListener {
            fetchWordData("giraffe")
        }
    }

    private fun fetchWordData(word: String) {
        tvApiResult.text = "Buscando $word..."

        lifecycleScope.launch {
            try {
                // Aquí se hace la petición GET a la API usando Retrofit
                val response = RetrofitClient.apiService.getWordDetails(word)

                if (response.isNotEmpty()) {
                    val wordInfo = response[0]
                    val phonetic = wordInfo.phonetic ?: "Fonética no disponible"

                    tvApiResult.text = "Palabra: ${wordInfo.word}\n\nFonética: $phonetic"
                }
            } catch (e: Exception) {
                tvApiResult.text = "Error al conectar con la API: ${e.message}"
            }
        }
    }
}