package com.example.projeto

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FormCadastro : AppCompatActivity() {

    private lateinit var editNome: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPass: EditText
    private lateinit var btnRegistar: Button
    var mensagens = arrayOf("Preencha todos os campos", "Registo realizado com sucesso")
    lateinit var utilizadorID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_cadastro)

        iniciarComponentes()

        btnRegistar.setOnClickListener {

            var nome: String = editNome.text.toString()
            var email: String = editEmail.text.toString()
            var password: String = editPass.text.toString()

            if (nome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                var snackbar: Snackbar = Snackbar.make(it, mensagens[0], Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            } else {
                cadastrarUtilizador(it)
            }
        }
    }

    private fun cadastrarUtilizador(view: View) {
        var email: String = editEmail.text.toString()
        var password: String = editPass.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    salvarDadosUtilizador()

                    var snackbar: Snackbar =
                        Snackbar.make(view, mensagens[1], Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()

                    val intent = Intent(this, InteressesUser::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    var erro: String
                    try {
                        throw it.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        erro = "Digite uma password com no mínimo 6 caracteres"
                    } catch (e: FirebaseAuthUserCollisionException) {
                        erro = "Esta conta já foi registada"
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        erro = "Email inválido"
                    } catch (e: Exception) {
                        erro = "Erro ao registar utilizador"
                    }

                    var snackbar: Snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()

                }
            }
    }

    private fun salvarDadosUtilizador() {
        val nome: String = editNome.text.toString()

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        val utilizadores: MutableMap<String, Any> = HashMap()

        utilizadores.put("nome", nome)

        utilizadorID = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val documentReference: DocumentReference =
            db.collection("Utilizadores").document(utilizadorID)
        documentReference.set(utilizadores).addOnSuccessListener {
            Log.d("db", "Sucesso ao guardar os dados do utilizadorS")
        }.addOnFailureListener {
            Log.d("db_erorr", "Erro ao guardar os dados do utilizador: $it")
        }

    }

    private fun iniciarComponentes() {
        editNome = findViewById(R.id.editNome)
        editEmail = findViewById(R.id.editEmail)
        editPass = findViewById(R.id.editPass)
        btnRegistar = findViewById(R.id.btnRegistar)
    }
}