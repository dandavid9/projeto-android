package com.example.projeto

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FormLogin : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPass: EditText
    private lateinit var btnEntrar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textoRegisto: TextView
    var mensagens = arrayOf("Preencha todos os campos", "Login efetuado com sucesso")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_login)

        iniciarComponentes()

        textoRegisto.setOnClickListener {
            val i = Intent(this, FormCadastro::class.java)
            startActivity(i)
        }

        btnEntrar.setOnClickListener {
            var email: String = editEmail.text.toString()
            var password: String = editPass.text.toString()

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                var snackbar: Snackbar =
                    Snackbar.make(it, mensagens[0], Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            } else {
                autenticarUtilizador(it)
            }
        }
    }

    private fun autenticarUtilizador(view: View) {
        var email: String = editEmail.text.toString()
        var password: String = editPass.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                progressBar.visibility = View.VISIBLE

                progressBar.postDelayed({
                    irParaTelaPrincipal()
                }, 3000)
            } else {
                var erro: String
                try {
                    throw it.exception!!
                } catch (e: Exception) {
                    erro = "Erro dar login com o utilizador"
                }

                var snackbar: Snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()

            }
        }
    }

    override fun onStart() {
        super.onStart()

        val utilizadorAtual: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (utilizadorAtual != null) {
            irParaTelaPrincipal()
        }

    }

    private fun irParaTelaPrincipal() {
        val intent: Intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
        finish()
    }

    private fun iniciarComponentes() {
        editEmail = findViewById(R.id.editEmail)
        editPass = findViewById(R.id.editPass)
        btnEntrar = findViewById(R.id.btnEntrar)
        progressBar = findViewById(R.id.progressBar)
        textoRegisto = findViewById(R.id.textTelaRegisto)
    }

}