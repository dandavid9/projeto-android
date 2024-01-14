package com.example.projeto

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class TelaUtilizador : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var btnDeslogar: Button
    private lateinit var userImage: ImageView
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var utilizadorID: String
    private val captureImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data: Intent? = it.data
                var imageBitmap: Bitmap? = null

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imageBitmap = data?.extras?.getParcelable("data", Parcelable::class.java) as? Bitmap
                }
                else {
                    imageBitmap = data?.extras?.getParcelable("data") as? Bitmap
                }
                userImage.setImageBitmap(imageBitmap)

                if (imageBitmap != null) {
                    guardarFotoPerfil(imageBitmap)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_utilizador)

        iniciarComponentes()

        userImage.setOnClickListener {
            abrirCamera()
        }

        btnDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent: Intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }

        exibirListaInteresses()


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
                val fotoPerfilUrl = value.getString("fotoPerfilUrl")
                if (fotoPerfilUrl != null) {
                    carregarImagem(fotoPerfilUrl)
                }
            }
        }

    }


    private fun abrirCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureImage.launch(cameraIntent)
    }


    private fun guardarFotoPerfil(bitmap: Bitmap) {

        utilizadorID = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
            .child("imagens_perfil/$utilizadorID.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        storageRef.putBytes(data)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    atualizarDocumentoUtilizador(utilizadorID, it.toString())
                }
            }
            .addOnFailureListener {
                Log.e("TelaUtilizador", "Erro ao fazer upload da imagem: ${it.message}")
            }

    }

    private fun atualizarDocumentoUtilizador(utilizadorID: String, imageUrl: String) {

        val documentReference: DocumentReference =
            db.collection("Utilizadores").document(utilizadorID)
        documentReference.update("fotoPerfilUrl", imageUrl)
            .addOnSuccessListener {
                Log.d("TelaUtilizador", "URL da imagem salva no Firestore com sucesso.")
            }
            .addOnFailureListener {
                Log.e(
                    "TelaUtilizador",
                    "Erro ao salvar URL da imagem no Firestore: ${it.message}"
                )
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
    }

    private fun carregarImagem(url: String) {
        if (!isDestroyed) {
            Glide.with(this)
                .load(url)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(userImage)
        }
    }

    private fun iniciarComponentes() {
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        btnDeslogar = findViewById(R.id.btnDeslogar)
        userImage = findViewById(R.id.userImage)
    }
}