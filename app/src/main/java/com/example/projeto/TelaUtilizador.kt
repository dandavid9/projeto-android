package com.example.projeto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRegistrar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class TelaUtilizador : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var btnDeslogar: Button
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var utilizadorID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_utilizador)

        iniciarComponentes()

        btnDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent: Intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        val email: String = FirebaseAuth.getInstance().currentUser?.email.toString()
        utilizadorID = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val documentReference: DocumentReference =
            db.collection("Utilizadores").document(utilizadorID)
        documentReference.addSnapshotListener { value, error ->
            if (value != null) {
                userName.setText(value.getString("nome"))
                userEmail.setText(email)
            }
        }

    }

    private fun iniciarComponentes() {
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        btnDeslogar = findViewById(R.id.btnDeslogar)
    }
}