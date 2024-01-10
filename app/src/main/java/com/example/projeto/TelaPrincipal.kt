package com.example.projeto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.ui.MapFragment

class TelaPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? MapFragment

        mapFragment?.getMapAsync { tomtomMap: TomTomMap ->

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_perfil -> {
                val i: Intent = Intent(this, TelaUtilizador::class.java)
                startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun abrirCamera(view: View) {
        val i: Intent = Intent(this, Camera::class.java)
        startActivity(i)
    }
}