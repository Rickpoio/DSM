package com.example.proyectodecatedra

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.content.Intent
import android.graphics.Paint
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import android.widget.ImageView
import android.widget.PopupMenu
import java.lang.Exception
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,0)
            insets

        }
        loadFragment(DashBoardFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)!!
        bottomNav.setOnItemSelectedListener {
           when(it.itemId) {
               R.id.home -> {
                   loadFragment(DashBoardFragment())
                   true
               }
               R.id.perfil -> {
                   loadFragment(PerfilFragment())
                   true
               }
               R.id.noticias -> {
                   loadFragment(NoticiasFragment())
                   true
               }
               R.id.presupuestos -> {
                   loadFragment(PresupuestosFragment())
                   true
               }
               R.id.movimientos -> {
                   loadFragment(MovimientosFragment())
                   true
               }
               R.id.Mismovimientos -> {
                   loadFragment(MisMovimientosFragment())
                   true
               }
               else -> false
           }
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }


}