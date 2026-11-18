package com.example.act7

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,
) {

    companion object {
        private const val DATABASE_NAME = "appingles.db"
        private const val DATABASE_VERSION = 3 // Incrementamos versión por la nueva tabla

        // Tabla de usuarios (login y registro)
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

        // NUEVA TABLA: Elementos favoritos del API (Palabras en inglés y su fonética relevante)
        private const val TABLE_FAVORITOS = "favoritos_api"
        private const val COL_FAV_ID = "id" // ID que viene del API
        private const val COL_PALABRA = "palabra"
        private const val COL_TRADUCCION = "traduccion"
        private const val COL_FONETICA = "fonetica" // El dato principal que te da tu API
    }

    // MÉTODO PARA CREAR LAS TABLAS
    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        // Creación de la nueva tabla solicitada para la información del API
        val crearTablaFavoritos = """
            CREATE TABLE $TABLE_FAVORITOS (
                $COL_FAV_ID INTEGER PRIMARY KEY,
                $COL_PALABRA TEXT NOT NULL,
                $COL_TRADUCCION TEXT NOT NULL,
                $COL_FONETICA TEXT NOT NULL
            )
        """.trimIndent()

        db?.execSQL(crearTablaUsuarios)
        db?.execSQL(crearTablaFavoritos)
    }

    // MÉTODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITOS")
            onCreate(db)
        }
    }


    // MÉTODOS DE USUARIOS (LOGIN / REGISTRO)
    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {
        val db = writableDatabase
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }
        val resultado = db.insert(TABLE_USUARIOS, null, datos)
        db.close()
        return resultado != -1L
    }

    fun iniciarSesion(correo: String, password: String): Boolean {
        val db = readableDatabase
        val querySql = "SELECT * FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?"
        val cursor = db.rawQuery(querySql, arrayOf(correo, password))
        val usuarioEncontrado = cursor.count > 0
        cursor.close()
        db.close()
        return usuarioEncontrado
    }


    // MÉTODOS SOLICITADOS PARA LOS ELEMENTOS FAVORITOS DEL API (FONÉTICA)
    // 1. MÉTODO PARA REGISTRAR UN ELEMENTO FAVORITO PROVENIENTE DEL API
    fun guardarFavorito(idApi: Long, palabra: String, traduccion: String, fonetica: String): Boolean {
        val db = writableDatabase
        val datos = ContentValues().apply {
            put(COL_FAV_ID, idApi)
            put(COL_PALABRA, palabra)
            put(COL_TRADUCCION, traduccion)
            put(COL_FONETICA, fonetica) // Guardamos la fonética que te da tu API
        }

        // Usamos CONFLICT_REPLACE por si el usuario vuelve a registrar el mismo elemento
        val resultado = db.insertWithOnConflict(
            TABLE_FAVORITOS,
            null,
            datos,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
        return resultado != -1L
    }

    // 2. MÉTODO PARA ELIMINAR UN REGISTRO FAVORITO
    fun eliminarFavorito(idApi: Long): Boolean {
        val db = writableDatabase
        val filasAfectadas = db.delete(
            TABLE_FAVORITOS,
            "$COL_FAV_ID = ?",
            arrayOf(idApi.toString())
        )
        db.close()
        return filasAfectadas > 0 // Retorna true si se eliminó correctamente
    }

    // 3. MÉTODO PARA VERIFICAR SI UN REGISTRO FAVORITO YA EXISTE
    fun existeFavorito(idApi: Long): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_FAVORITOS WHERE $COL_FAV_ID = ?",
            arrayOf(idApi.toString())
        )
        val existe = cursor.moveToFirst() // Devuelve true si encontró el registro
        cursor.close()
        db.close()
        return existe
    }
}