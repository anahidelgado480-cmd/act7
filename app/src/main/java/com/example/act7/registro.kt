package com.example.act7

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class registro : AppCompatActivity() {


    private lateinit var etnombre: EditText
    private lateinit var etcorreo: EditText
    private lateinit var etpassword: EditText

    private lateinit var btnregistrar: Button

    //Crear objeto de la clase de la base de datos
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        etnombre = findViewById(R.id.etnombre)
        etcorreo = findViewById(R.id.etcorreo)
        etpassword = findViewById(R.id.etpassword)
        btnregistrar = findViewById(R.id.btnregistrar)

        //INICIAR EL OBJETO DE LA BASE DE DATOS
        dbHelper = DatabaseHelper(this)

        //Evento para registrar usuario
        btnregistrar.setOnClickListener {
            //llamar al método de inicio de registrar usuario
            registrarusuario()
        }

    }

    //MÉTODO PARA REGISTRAR A UN USUARIO
    private fun registrarusuario() {

        //obtener la inofrmación que escribe el usuario
        val nombre = etnombre.text.toString().trim()
        val correo = etcorreo.text.toString().trim()
        val password = etpassword.text.toString().trim()

        //Validar que los campos tengan inofrmación
        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return

        }

        //Llamar  a nuestra base de datos y guadar el resultado de la inserción
        val resultado = dbHelper.registrarusuario(nombre, correo, password)

        if (resultado) {
            //Si el resultado es verdadero
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            //Regresar al usuario a la pantalla anterior
            finish()

        } else {
            Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()

        }
    }

}