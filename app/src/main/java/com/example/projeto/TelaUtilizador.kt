package com.example.projeto

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRegistrar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class TelaUtilizador : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userImage: ImageView
    private lateinit var btnDeslogar: Button
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var utilizadorID: String
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                userImage.setImageURI(imageUri)

                val utilizadorID = FirebaseAuth.getInstance().currentUser?.uid
                val imageDrawable = userImage.drawable
                val imageBitmap = (imageDrawable as BitmapDrawable).bitmap

                guardarFotoPerfil(utilizadorID, imageBitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_utilizador)

        iniciarComponentes()

        userImage.setOnClickListener {
            abrirGaleria()
        }

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

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }


    private fun guardarFotoPerfil(utilizadorID: String?, bitmap: Bitmap) {
        if (utilizadorID != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance().reference
                .child("imagens_perfil/$utilizadorID.jpg")

            // Converta a imagem bitmap em um array de bytes
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Faça o upload da imagem para o Firebase Storage
            storageRef.putBytes(data)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        atualizarDocumentoUtilizador(utilizadorID, uri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    // Ocorreu um erro durante o upload
                    // Lide com o erro adequadamente
                    Log.e("TelaUtilizador", "Erro ao fazer upload da imagem: ${exception.message}")
                }
        }
    }

    private fun atualizarDocumentoUtilizador(utilizadorID: String, imageUrl: String) {
        // Atualize o documento do utilizador no Firestore com a URL da imagem
        val documentReference: DocumentReference = db.collection("Utilizadores").document(utilizadorID)
        documentReference.update("fotoPerfilUrl", imageUrl)
            .addOnSuccessListener {
                Log.d("TelaUtilizador", "URL da imagem salva no Firestore com sucesso.")
            }
            .addOnFailureListener { exception ->
                Log.e("TelaUtilizador", "Erro ao salvar URL da imagem no Firestore: ${exception.message}")
            }
    }


    private fun iniciarComponentes() {
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userImage = findViewById(R.id.userImage)
        btnDeslogar = findViewById(R.id.btnDeslogar)
    }
}