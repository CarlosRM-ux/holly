package com.example.holly.Data.repository

import android.net.Uri
import com.example.holly.Domain.model.Photo
import com.example.holly.Domain.model.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage) {

    // Obtiene todas las fotos del usuario desde Realtime Database
    fun getUserPhotos(): Flow<Result<List<Photo>>> = flow {
        emit(Result.Loading)
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado.")
            val photosCollection = database.collection("users").document(userId).collection("photos")

            val querySnapshot = photosCollection.get().await()

            val photos = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Photo::class.java)?.copy(id = document.id)

            }
            emit(Result.Success(photos))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Error al cargar las fotos."))
        }
    }

    // Sube una foto a Firebase Storage y guarda su URL en Realtime Database
    fun uploadPhotoAndSaveUrl(uri: Uri): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado.")
            val photoRef = storage.reference.child("photos/$userId/${System.currentTimeMillis()}.jpg")

            // Sube el archivo y espera a que la tarea termine
            val uploadTaskSnapshot = photoRef.putFile(uri).await()
            val downloadUrl = uploadTaskSnapshot.storage.downloadUrl.await().toString()

            // Crea un nuevo objeto Photo y lo guarda en Realtime Database
            val newPhotoRef = database.collection("users").document(userId).collection("photos")
             newPhotoRef.add(Photo(imageUrl = downloadUrl)).await()


            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Error al subir la foto y guardar la URL."))
        }
    }
}