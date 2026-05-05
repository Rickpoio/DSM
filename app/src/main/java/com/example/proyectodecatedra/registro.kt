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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
            val email = findViewById<EditText>(R.id.correoedit).text.toString()
            val password = findViewById<EditText>(R.id.et_pass).text.toString()
            this.registro(email, password)
        }
    }

    private fun registro (email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
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