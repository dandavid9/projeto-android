package com.example.projeto

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.postDelayed
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser

class FormLogin : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textoCadastro: TextView
    var mensagens = arrayOf("Preencha todos os campos", "Login efetuado com sucesso")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_login)

        iniciarComponentes()

        textoCadastro.setOnClickListener {
            val i = Intent(this, FormCadastro::class.java)
            startActivity(i)
            finish()
        }

        btnEntrar.setOnClickListener {
            var email: String = editEmail.text.toString()
            var senha: String = editSenha.text.toString()

            if (email.isNullOrEmpty() || senha.isNullOrEmpty()) {
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
        var senha: String = editSenha.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener {
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
                    erro = "Erro ao logar utilizador"
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
        val intent: Intent = Intent(this, TelaUtilizador::class.java)
        startActivity(intent)
        finish()
    }

    private fun iniciarComponentes() {
        editEmail = findViewById(R.id.editEmail)
        editSenha = findViewById(R.id.editSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        progressBar = findViewById(R.id.progressBar)
        textoCadastro = findViewById(R.id.textTelaCadastro)
    }

}