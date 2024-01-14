package com.example.projeto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.search.Search
import com.tomtom.sdk.search.SearchOptions
import com.tomtom.sdk.search.online.OnlineSearch
import com.tomtom.sdk.search.ui.SearchFragment
import com.tomtom.sdk.search.ui.model.SearchApiParameters
import com.tomtom.sdk.search.ui.model.SearchProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TelaPrincipal : AppCompatActivity() {
    val apiKey = BuildConfig.TOMTOM_API_KEY
    private lateinit var search: Search
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        search = OnlineSearch.create(this, apiKey)
        iniciarPesquisa()

    }

    fun iniciarPesquisa() {
        val searchApiParameters = SearchApiParameters(
            limit = 5,
            position = GeoPoint(52.377956, 4.897070)
        )
        val searchProperties = SearchProperties(
            searchApiKey = apiKey,
            searchApiParameters = searchApiParameters,
            commands = listOf("TomTom")
        )

        val searchFragment = SearchFragment.newInstance(searchProperties)
        supportFragmentManager.beginTransaction()
            .replace(R.id.search_fragment_container, searchFragment)
            .commitNow()

        searchFragment.setSearchApi(pesquisar())

    }

    fun pesquisar(): Search {
        val query = "TomTom"
        val searchOptions =
            SearchOptions(query, countryCodes = setOf("NLD", "POL"), limit = 5)

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                search.search(searchOptions)

            }
        }

        return search
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