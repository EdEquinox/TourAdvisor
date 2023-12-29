package pt.isec.touradvisor.utils.firebase

import android.content.res.AssetManager
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import pt.isec.touradvisor.data.Avaliacao
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FStorageUtil {
    companion object {

        fun removeDataFromFirestore(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val v = db.collection("Scores").document("Level1")

            v.delete()
                .addOnCompleteListener { onResult(it.exception) }
        }

        private var listenerRegistrationCategorias: ListenerRegistration? = null
        private var listenerRegistrationPOIs: ListenerRegistration? = null
        private var listenerRegistrationLocations: ListenerRegistration? = null

        @OptIn(DelicateCoroutinesApi::class)
        fun startObserver(onNewValues: (MutableList<Category>, MutableList<POI>, MutableList<Local>) -> Unit, onReady: () -> Unit) {
            stopObserver()

            val categories = mutableListOf<Category>()
            val pois = mutableListOf<POI>()
            val locations = mutableListOf<Local>()

            val db = Firebase.firestore

            var completedListeners = 0

            fun checkAllListenersCompleted() {
                completedListeners++
                if (completedListeners == 3) {
                    onNewValues(categories, pois, locations)
                    onReady()
                }
            }

            suspend fun DocumentReference.getSuspended(): DocumentSnapshot? = suspendCancellableCoroutine { continuation ->
                this.get().addOnSuccessListener { document ->
                    continuation.resume(document)
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
            }

            listenerRegistrationCategorias = db.collection("Categorias")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        return@addSnapshotListener
                    } else {
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            querySnapshot.documents.forEach {
                                val name = it.getString("nome") ?: ""
                                val description = it.getString("descricao") ?: ""
                                val image = it.getString("imagem") ?: ""
                                categories.add(Category(name, description, image))
                            }
                        }
                    }
                    checkAllListenersCompleted()
                }
            listenerRegistrationPOIs = db.collection("POI")
                .addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        return@addSnapshotListener
                    } else {
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            GlobalScope.launch{
                                querySnapshot.documents.forEach {
                                    val docRefCat = it.getDocumentReference("categoria")
                                    val docRefLoc = it.getDocumentReference("location")
                                    val documentCat = docRefCat?.get()?.await()
                                    val documentLoc = docRefLoc?.get()?.await()
                                    val nomeLoc = documentLoc?.getString("nome") ?: ""
                                    val descLoc = documentLoc?.getString("descricao") ?: ""
                                    val imageLoc = documentLoc?.getString("imagem") ?: ""
                                    val geoPointLoc = documentLoc?.getGeoPoint("geopoint")
                                    val location = Local(nomeLoc, descLoc, imageLoc, geoPointLoc)
                                    val category = documentCat?.toObject(Category::class.java)
                                    val name = it.getString("nome") ?: ""
                                    val description = it.getString("descricao") ?: ""
                                    val geoPoint = it.getGeoPoint("geoPoint")
                                    val image = it.getString("imagem") ?: ""

                                    pois.add(POI(name, description, geoPoint, category,  location, image))
                                }
                                checkAllListenersCompleted()
                            }
                        }
                    }
                }
            listenerRegistrationLocations = db.collection("Localizacao")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        return@addSnapshotListener
                    } else {
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            querySnapshot.documents.forEach {
                                val name = it.getString("nome") ?: ""
                                val description = it.getString("descricao") ?: ""
                                val geoPoint = it.getGeoPoint("geopoint")
                                val image = it.getString("imagem") ?: ""
                                locations.add(Local(name, description, image, geoPoint))
                            }
                        }
                    }
                    checkAllListenersCompleted()
                }

        }

        @OptIn(DelicateCoroutinesApi::class)
        fun getUserPOIS(user: String, onNewValues: (MutableList<POI>) -> Unit) {
            val db = Firebase.firestore
            val pois = mutableListOf<POI>()

            suspend fun DocumentReference.getSuspended(): DocumentSnapshot? =
                suspendCancellableCoroutine { continuation ->
                    this.get().addOnSuccessListener { document ->
                        continuation.resume(document)
                    }.addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
                }

            db.collection("POI")
                .addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        return@addSnapshotListener
                    } else {
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            GlobalScope.launch {
                                querySnapshot.documents.forEach {
                                    val docRefCat = it.getDocumentReference("categoria")
                                    val docRefLoc = it.getDocumentReference("location")
                                    val documentCat = docRefCat?.get()?.await()
                                    val documentLoc = docRefLoc?.get()?.await()
                                    val nomeLoc = documentLoc?.getString("nome") ?: ""
                                    val descLoc = documentLoc?.getString("descricao") ?: ""
                                    val imageLoc = documentLoc?.getString("imagem") ?: ""
                                    val geoPointLoc = documentLoc?.getGeoPoint("geopoint")
                                    val location = Local(nomeLoc, descLoc, imageLoc, geoPointLoc)
                                    val category = documentCat?.toObject(Category::class.java)
                                    val name = it.getString("nome") ?: ""
                                    val description = it.getString("descricao") ?: ""
                                    val geoPoint = it.getGeoPoint("geoPoint")
                                    val image = it.getString("imagem") ?: ""
                                    val nuser = it.getString("user") ?: ""
                                    if (nuser == user){
                                        pois.add(POI(name, description, geoPoint, category, location, image, nuser))
                                    }
                                }
                                onNewValues(pois)
                            }
                        }
                    }
                }
        }

        fun stopObserver() {
            listenerRegistrationPOIs?.remove()
            listenerRegistrationCategorias?.remove()
            listenerRegistrationLocations?.remove()
        }

// Storage

        fun getFileFromAsset(assetManager: AssetManager, strName: String): InputStream? {
            var istr: InputStream? = null
            try {
                istr = assetManager.open(strName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return istr
        }

//https://firebase.google.com/docs/storage/android/upload-files

        fun uploadFile(inputStream: InputStream,path:String, imgFile: String, onUploadComplete: (String) -> Unit) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child(path)
            val ref3 = ref2.child(imgFile)

            val uploadTask = ref3.putStream(inputStream)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref3.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println(downloadUri.toString())
                    onUploadComplete(downloadUri.toString())
                } else {
                    // Handle failures
                    // ...
                }
            }


        }

        fun addPOIToFirestore(data: HashMap<String, Any>, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            db.collection("POI").document(data["nome"].toString()).set(data)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }

        }

        fun addCategoryToFirestore(
            data: HashMap<String, Any>,
            onResult: (Throwable?) -> Unit
        ) {
            val db = Firebase.firestore
            db.collection("Categorias").document(data["nome"].toString()).set(data)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }

        }

        fun addLocationToFirestore(
            data: HashMap<String, Any>,
            onResult: (Throwable?) -> Unit
        ) {
            val db = Firebase.firestore
            db.collection("Localizacao").document(data["nome"].toString()).set(data)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }

        }

        fun addAvaliacao(avaliacao: Avaliacao, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            db.collection("Avaliacoes").document("${avaliacao.poi}.${avaliacao.user}").set(avaliacao)
                .addOnCompleteListener{result ->
                    onResult(result.exception)
                }
        }

    }
}