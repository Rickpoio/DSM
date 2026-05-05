package com.example.proyectodecatedra

import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class login : AppCompatActivity() {

   private lateinit var auth: FirebaseAuth
   private lateinit var tvregistro: TextView
   private lateinit var btnlogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //inicializando FirebaseAuth

        auth = FirebaseAuth.getInstance()
        btnlogin = findViewById(R.id.btningresar)
        tvregistro = findViewById(R.id.registrolink)

        //estilo para el textview registrolink
        val registroLink = findViewById<TextView>(R.id.registrolink)
        val texto = registroLink.text.toString()
        val spannable = SpannableString(texto)
        spannable.setSpan(UnderlineSpan(), 0, texto.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        registroLink.text = spannable

       tvregistro.setOnClickListener {
           this.onClickRegistro()
       }

       btnlogin.setOnClickListener {
           this.Ingresar()
       }
    }

    public override fun onStart(){
        //validacion que permite ver si habia un usuario con la sesion iniciada
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser != null){
            refrescar()
        }
    }

    fun onClickRegistro(){
        val registrarse : TextView = findViewById(R.id.registrolink)
        registrarse.setOnClickListener {
            val llamar = Intent(this, registro::class.java )
            startActivity(llamar)
        }
    }

    //funcion para el uso del boton de iniciar sesion
    fun Ingresar(){
        btnlogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.correoedit).text.toString()
            val password = findViewById<EditText>(R.id.passhint).text.toString()
            this.login(email, password)
        }
    }

    //funcion que lee los datos para iniciar sesion
    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
        task ->
            if (task.isSuccessful){
                Log.d(Tag, "signInWithEmail:success")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                val user = auth.currentUser
                actualizarIU(user)
            }else{
                Log.w(Tag, "SignInWithEmail:failure", task.exception)
                Toast.makeText(this,"fallo la autenticacion", Toast.LENGTH_SHORT).show()
                actualizarIU(null)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                applicationContext,
                exception.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun actualizarIU(user: FirebaseUser?){

    }

    private fun refrescar(){

    }
    companion object {
        private const val Tag = "EmailPassword"
    }
}