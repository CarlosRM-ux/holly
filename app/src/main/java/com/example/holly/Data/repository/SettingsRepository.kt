package com.example.holly.Data.repository

import android.net.Uri
import com.example.holly.Domain.model.Photo
import com.example.holly.Domain.model.Result
import com.example.holly.Domain.model.rasgosPersonalidad.InterestCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage) {

    //------------__________________________________________________________________________________________________________
    // Obtiene todas las fotos del usuario desde Realtime Database
    //______________________________________________________________________________________________________________________

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
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------

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

    //------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------
    //_____CREA LA CARPETA INTERESTS Y GUARDA LOS INTEREESES SELECCIONADOS___________________________________________________________________

    fun saveInterest (interest: Set<InterestCategory>) : Flow<Result<Unit>> = flow{
        emit(Result.Loading)
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
            val userDocRef = database.collection("users").document(userId)
            val interestName = interest.map { it.name }

            val dataToSave = mapOf("interests" to interestName)
            userDocRef.set(dataToSave, SetOptions.merge()).await()

            emit(Result.Success(Unit))
        }catch (e: Exception){
            emit(Result.Error(e.message ?:"Error al guardar los intereses"))
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------------------------------
    //______________OBTIENE LOS INTERESES GUARDADOS Y LOS CONVIERTE EN UNA LISTA DE STRINGS_________________________________________________

    fun loadUserInterest(): Flow<Result<Set<InterestCategory>>> = flow{
        emit(Result.Loading)
        try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado") //Guarda el Id del Usuario Actual
            val userDocRef = database.collection("users")
                .document(userId)// accede a la carpeta users del id del usuario

            val documentSnapshot = userDocRef.get().await()//obtiene el documento del usuario

            val interestList = documentSnapshot.get("interests") as? List<String> ?: emptyList()

            val interestSet = interestList.mapNotNull { name ->
                InterestCategory.entries.find { it.name == name }
            }.toSet()
            emit(Result.Success(interestSet))
        }catch (e: Exception){
            emit(Result.Error(e.message ?: "Error al cargar los intereses del usuario."))
        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------
    //________Crea y Guarda la descripcion del Usuario___________________________________________________________________________________

    fun saveDescription (description: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("usuario no autenticado")
            val userDocRef = database.collection("users").document(userId)

            userDocRef.set(mapOf("description" to description), SetOptions.merge()).await()

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Error al guardar descripcion"))
        }
    }

    fun loadDescription(): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("usuario no encontrado")
            val userDocRef = database.collection("users").document(userId).get().await()

            val  description = userDocRef.getString("description") ?: ""

            emit(Result.Success(description))

        }catch (e: Exception){
            emit(Result.Error(e.message ?: "Error al cargar la descripción."))

        }
    }
}