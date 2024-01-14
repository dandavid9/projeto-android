package com.example.projeto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.tomtom.sdk.search.ui.SearchView
import com.tomtom.sdk.search.ui.SearchViewListener

class TelaPrincipal : AppCompatActivity() {
    val apiKey = BuildConfig.TOMTOM_API_KEY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

    //iniciarMapa()
        pesquisar()

    }

    fun pesquisar() {
        val searchView: SearchView by lazy { findViewById(R.id.search_view) }

        searchView.searchViewListener = object : SearchViewListener {
            override fun onSearchQueryCancel() {
                /* YOUR CODE GOES HERE */
            }

            override fun onSearchQueryChanged(input: String) {
                /* YOUR CODE GOES HERE */
            }

            override fun onCommandInsert(command: String) {
                /* YOUR CODE GOES HERE */
            }
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
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

    fun abrirMapa(view: View) {
        val i: Intent = Intent(this, Mapa::class.java)
        startActivity(i)
    }

    fun abrirCamera(view: View) {
        val i: Intent = Intent(this, Camera::class.java)
        startActivity(i)
    }
}