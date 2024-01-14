package com.example.projeto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class InteressesUser : AppCompatActivity() {

    private lateinit var editTextInteresse: EditText
    private lateinit var buttonAdicionar: Button
    private lateinit var btnVerPerfil: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interesses_user)

        iniciarComponentes()

        buttonAdicionar.setOnClickListener {
            adicionarInteresse()
        }

        btnVerPerfil.setOnClickListener {
            val inte = Intent(this, TelaUtilizador::class.java)
            startActivity(inte)
        }
    }

    private fun adicionarInteresse() {
        val interesse: String = editTextInteresse.text.toString()

        if (interesse.isNotEmpty()) {
            guardarInteresseNoFirebase(interesse)
            exibirListaInteresses()
            limparCampoInteresse()
        } else {
            exibirSnackbar("Por favor, preencha o campo de interesses.")
        }
    }

    private fun exibirListaInteresses() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val utilizadorID: String = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val collectionReference = db.collection("Utilizadores").document(utilizadorID)
            .collection("Interesses")

        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                val interesses = StringBuilder()

                for (document in querySnapshot) {
                    val interesse = document.getString("interesse")
                    interesses.append("$interesse\n")
                }

                val textViewListaInteresses: TextView = findViewById(R.id.textViewInteresses)
                textViewListaInteresses.text = interesses.toString()
            }
            .addOnFailureListener { e ->
                exibirSnackbar("Erro ao obter interesses: $e")
            }
    }


    private fun guardarInteresseNoFirebase(interesse: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val utilizadorID: String = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val interesses: MutableMap<String, Any> = HashMap()
        interesses["interesse"] = interesse

        val documentReference: DocumentReference =
            db.collection("Utilizadores").document(utilizadorID).collection("Interesses").document()

        documentReference.set(interesses)
            .addOnSuccessListener {
                exibirSnackbar("Interesse adicionado com sucesso.")
            }
            .addOnFailureListener { e ->
                exibirSnackbar("Erro ao adicionar interesse: $e")
            }
    }

    private fun limparCampoInteresse() {
        editTextInteresse.text.clear()
    }

    private fun exibirSnackbar(mensagem: String) {
        val snackbar: Snackbar = Snackbar.make(
            buttonAdicionar,
            mensagem,
            Snackbar.LENGTH_SHORT
        )
        snackbar.show()
    }

    private fun iniciarComponentes() {
        editTextInteresse = findViewById(R.id.editTextInteresse)
        buttonAdicionar = findViewById(R.id.buttonAdicionar)
        btnVerPerfil = findViewById(R.id.buttonVerPerfil)
    }
}
