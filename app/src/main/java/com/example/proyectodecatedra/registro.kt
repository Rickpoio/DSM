package com.example.proyectodecatedra

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectodecatedra.data.SupabaseProvider
import com.example.proyectodecatedra.data.usuariosData
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var btnregistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        auth = FirebaseAuth.getInstance()
        btnregistro = findViewById(R.id.btnregistro)

       registrarUsuario()
    }

    fun registrarUsuario(){
        btnregistro.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.et_nombre).text.toString()
            val email = findViewById<EditText>(R.id.correoedit).text.toString()
            val password = findViewById<EditText>(R.id.et_pass).text.toString()
            this.registro(email, password, nombre)
        }
    }

    private fun registro (email: String, password: String, nombre: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (uid != null) {
                    lifecycleScope.launch {
                        try {
                            val nuevoUsuario = usuariosData(
                                        id = uid,
                                        nombre = nombre,
                                        correo = email
                                    )
                            SupabaseProvider.client.from("usuarios").insert(nuevoUsuario)
                            Log.d("SUPABASE_EXITO", "se inserto")
                            val intent = Intent(this@registro, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }catch (e: Exception){
                            Toast.makeText(this@registro, "error en DB: ${e.message}", Toast.LENGTH_SHORT ).show()
                            Log.e("SUPABASE_ERROR", "error ${e.localizedMessage}")
                            e.printStackTrace()
                        }
                    }

                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                applicationContext,
                exception.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}