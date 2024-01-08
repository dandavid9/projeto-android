package com.example.projeto

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import com.example.projeto.Camera.Companion.REQUEST_IMAGE_CAPTURE
import com.google.firebase.Firebase
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


class FotoPerfil : AppCompatActivity() {

    private lateinit var btnCarregarFoto: android.widget.Button
    private lateinit var btnTirarFoto: android.widget.Button
    private lateinit var imgFotoPerfil: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto_perfil)

        iniciarComponentes()

        btnCarregarFoto.setOnClickListener {
            abrirGaleria()
        }

        btnTirarFoto.setOnClickListener {
            tirarFoto()
        }
    }

    private fun iniciarComponentes() {
        btnCarregarFoto = findViewById(R.id.btnCarregarFoto)
        btnTirarFoto = findViewById(R.id.btnTirarFoto)
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun tirarFoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imgFotoPerfil.setImageURI(selectedImageUri)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            imgFotoPerfil.setImageBitmap(imageBitmap)
        }
    }

    private fun proximaActivity() {
        val inte = Intent(this, InteressesUser::class.java)

        val utilizadorID = FirebaseAuth.getInstance().currentUser?.uid

        val imageDrawable = imgFotoPerfil.drawable
        val imageBitmap = (imageDrawable as BitmapDrawable).bitmap

        guardarFotoPerfil(utilizadorID, imageBitmap)

        startActivity(inte)
    }

    private fun guardarFotoPerfil(utilizadorID: String?, bitmap: Bitmap) {
        if (utilizadorID != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val photoRef = storageRef.child("fotos_perfil/$utilizadorID.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = photoRef.putBytes(data)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.storage.downloadUrl
            }.addOnFailureListener {
                Log.e("FotoPerfil", "Error uploading photo: $it")
            }
        }
    }

}