package com.example.act7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPass :  EditText
    private lateinit var btnInicio :  Button
    private lateinit var dbHelper :  DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvcrearcuenta = findViewById<TextView>(R.id.tvcrearcuenta)

        etCorreo = findViewById(R.id.etcorreo)
        etPass = findViewById(R.id.etpassword)
        btnInicio = findViewById(R.id.btningresar)

        dbHelper = DatabaseHelper(this)

        btnInicio.setOnClickListener {
            iniciarSesion() // Llamamos al método que ya tienes creado abajo
        }

        tvcrearcuenta.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }
    }
    private fun iniciarSesion(){

            //Acceder a la información que escribe el usuario
        val correo = etCorreo.text.toString().trim()
        val pass = etPass.text.toString().trim()

        if (correo.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return
        }

        //LLAMAR AL MÉTODO DE INICIO DE SESIÓN EN LA BD
        val resultado = dbHelper.iniciarSesion(correo, pass)

        if (resultado){
            //Si la sesión es correcta
            Toast.makeText(this, "BIENVENIDO <3", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            //Cerrar la pantalla actual
            finish()
            }else{
                Toast.makeText(this, "Datos: INCORRECTOS", Toast.LENGTH_SHORT).show()

            }
    }


}