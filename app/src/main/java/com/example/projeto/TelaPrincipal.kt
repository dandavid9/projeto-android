package com.example.projeto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.tomtom.sdk.search.Search
import com.tomtom.sdk.search.SearchCallback
import com.tomtom.sdk.search.SearchOptions
import com.tomtom.sdk.search.SearchResponse
import com.tomtom.sdk.search.common.error.SearchFailure
import com.tomtom.sdk.search.online.OnlineSearch
import com.tomtom.sdk.search.ui.SearchResultsView
import com.tomtom.sdk.search.ui.SearchSuggestionView
import com.tomtom.sdk.search.ui.SearchView
import com.tomtom.sdk.search.ui.SearchViewListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TelaPrincipal : AppCompatActivity() {
    val apiKey = BuildConfig.TOMTOM_API_KEY
    private lateinit var search: Search
    private val searchView: SearchView by lazy { findViewById(R.id.search_view) }
    private val searchResultsView: SearchResultsView by lazy { findViewById(R.id.search_results_view) }
    private val searchSuggestionView: SearchSuggestionView by lazy { findViewById(R.id.search_suggestion_view) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        search = OnlineSearch.create(this, apiKey)
        iniciarPesquisa()

    }

    fun iniciarPesquisa() {

        searchView.searchViewListener = object : SearchViewListener {

            override fun onCommandInsert(command: String) {
                //
            }

            override fun onSearchQueryCancel() {
                //
            }

            override fun onSearchQueryChanged(input: String) {
                pesquisar()
            }
        }
    }

    fun pesquisar() {
        val query = "TomTom"
        val searchOptions =
            SearchOptions(query, countryCodes = setOf("NLD", "POL"), limit = 5)

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                search.search(searchOptions,
                    object : SearchCallback {
                        override fun onFailure(failure: SearchFailure) {
                            Toast.makeText(
                                this@TelaPrincipal,
                                failure.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onSuccess(result: SearchResponse) {
                            addSearchResultsToSearchResultsView(result)
                        }
                    })

            }
        }
    }



    fun addSearchResultsToSearchResultsView(result: SearchResponse) {
        //val places: List<PlaceDetails> = result.results.map { it }

        //searchResultsView.update(places, result.summary.query)
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