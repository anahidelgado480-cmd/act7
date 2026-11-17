package com.example.act7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPass :  EditText
    private lateinit var btnInicio :  Button
    private lateinit var dbHelper :  DatabaseHelper
    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager : CredentialManager
    private lateinit var btnGoogle : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvcrearcuenta = findViewById<TextView>(R.id.tvcrearcuenta)

        etCorreo = findViewById(R.id.etcorreo)
        etPass = findViewById(R.id.etpassword)
        btnInicio = findViewById(R.id.btningresar)

        dbHelper = DatabaseHelper(this)

        btnGoogle = findViewById(R.id.btnGoogle)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        //EVENTO PARA INICIAR SESIÓN
        btnInicio.setOnClickListener {
            //TODO: Agregsar método de inicio de sesión
            iniciarSesion()
        }

        btnGoogle.setOnClickListener {
            iniciarSesionGoogle() // Llamamos al método que ya tienes creado abajo
        }

        tvcrearcuenta.setOnClickListener {
            val intent = Intent(this, registro::class.java)
            startActivity(intent)
        }
    }
    //MÉTODO PARA INCIAR SESEIÓN
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
    // 3. MÉTODO PARA AUTENTICAR EN FIREBSAE
    private fun autenticarFirebase(idToken: String){

        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) {

                inicio ->

            //Validar si el inicio de sesión con Firebase es exitoso
            if (inicio.isSuccessful){

                //Variable para obtener toda la info del usuario
                val usuario = auth.currentUser

                Toast.makeText(this, "Bienvenido, ${usuario?.displayName}", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)

                startActivity(intent)

                finish()
            }else{
                Toast.makeText(this, "Error añ validar con Firebase", Toast.LENGTH_SHORT).show()
            }

        }


    }

    // 4. MÉTODO PARA PROCESAR LA CREDENCIAL
    private fun procesarCredencial(credential: Credential){

        if(credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){

            try {

                //variable para obtener la credencial de Google
                val credencialGoogle = GoogleIdTokenCredential.createFrom(
                    credential.data

                )

                autenticarFirebase(credencialGoogle.idToken)


            }catch (e : GoogleIdTokenParsingException){
                Toast.makeText(this, "Credencial válida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 5. MÉTODO PARA INICIAR SESIÓN
    private fun iniciarSesionGoogle(){

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        lifecycleScope.launch {

            try {

                //Validar que el resultado del inicio sea correcto
                val resultado = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = solicitud
                )

                procesarCredencial(resultado.credential)

            } catch (e : GetCredentialException){
                Toast.makeText(this@MainActivity, "Error al inciar sesion", Toast.LENGTH_SHORT).show()
            }
        }


    }

    //MÉTODO PARA MANTENER LA SESIÓN ACTIVA
    override fun onStart() {
        super.onStart()

        //Variable  ue guarde el usuario actual
        val usuarioActual = auth.currentUser

        //Validar que el usuario diferente nulo
        if (usuarioActual != null ){
            val intent = Intent(this, HomeActivity::class.java)

            startActivity(intent)
            finish()
        }
    }
}