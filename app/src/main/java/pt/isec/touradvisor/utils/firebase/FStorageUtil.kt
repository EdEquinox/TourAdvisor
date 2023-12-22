package pt.isec.touradvisor.utils.firebase

import android.content.res.AssetManager
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Localizacao
import pt.isec.touradvisor.data.POI
import java.io.IOException
import java.io.InputStream

class FStorageUtil {
    companion object {
        fun addDataToFirestore(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val scores = hashMapOf(
                "nrgames" to 0,
                "topscore" to 0
            )
            db.collection("Scores").document("Level1").set(scores)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun updateDataInFirestore(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val v = db.collection("Scores").document("Level1")

            v.get(Source.SERVER)
                .addOnSuccessListener {
                    val exists = it.exists()
                    Log.i("Firestore", "updateDataInFirestore: Success? $exists")
                    if (!exists) {
                        onResult(Exception("Doesn't exist"))
                        return@addOnSuccessListener
                    }
                    val value = it.getLong("nrgames") ?: 0
                    v.update("nrgames", value + 1)
                    onResult(null)
                }
                .addOnFailureListener { e ->
                    onResult(e)
                }
        }

        fun updateDataInFirestoreTrans(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val v = db.collection("Scores").document("Level1")

            db.runTransaction { transaction ->
                val doc = transaction.get(v)
                if (doc.exists()) {
                    val newnrgames = (doc.getLong("nrgames") ?: 0) + 1
                    val newtopscore = (doc.getLong("topscore") ?: 0) + 100
                    transaction.update(v, "nrgames", newnrgames)
                    transaction.update(v, "topscore", newtopscore)
                    null
                } else
                    throw FirebaseFirestoreException(
                        "Doesn't exist",
                        FirebaseFirestoreException.Code.UNAVAILABLE
                    )
            }.addOnCompleteListener { result ->
                onResult(result.exception)
            }
        }

        fun removeDataFromFirestore(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val v = db.collection("Scores").document("Level1")

            v.delete()
                .addOnCompleteListener { onResult(it.exception) }
        }

        private var listenerRegistrationCategorias: ListenerRegistration? = null
        private var listenerRegistrationPOIs: ListenerRegistration? = null
        private var listenerRegistrationLocations: ListenerRegistration? = null

        fun startObserver(onNewValues: (Category, POI, Localizacao) -> Unit) {
            stopObserver()
            var categoria = Category()
            var poi = POI()
            var location = Localizacao()
            val db = Firebase.firestore
            listenerRegistrationCategorias = db.collection("Categorias")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        return@addSnapshotListener
                    } else {
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            querySnapshot.documents.forEach {
                                val nome = it.getString("nome") ?: ""
                                val descricao = it.getString("descricao") ?: ""
                                val imagem = it.getString("imagem") ?: ""
                                Log.i(
                                    "Firestore",
                                    "startObserver: $nome $descricao $imagem"
                                )
                                categoria = Category(nome, descricao, imagem, "")
                            }
                        }
                    }
                }
            listenerRegistrationPOIs = db.collection("POI")
                .addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        return@addSnapshotListener
                    } else {
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            querySnapshot.documents.forEach(){
                                val nome = it.getString("nome") ?: ""
                                val descricao = it.getString("descricao") ?: ""
                                val latitude = it.getDouble("latitude") ?: 0.0
                                val longitude = it.getDouble("longitude") ?: 0.0
                                val categoriaPOI = it.toObject(Category::class.java) ?: Category()
                                val imagem = it.getString("imagem") ?: ""
                                Log.i(
                                    "Firestore",
                                    "startObserver: $nome $descricao $latitude $longitude $categoriaPOI $imagem"
                                )
                                poi = POI(nome, descricao, latitude, longitude, categoriaPOI, imagem, "")
                            }
                        }
                    }
                }
            listenerRegistrationLocations = db.collection("Localizacao")
                .addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        return@addSnapshotListener
                    } else {
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            querySnapshot.documents.forEach(){
                                val nome = it.getString("nome") ?: ""
                                val descricao = it.getString("descricao") ?: ""
                                val latitude = it.getDouble("latitude") ?: 0.0
                                val longitude = it.getDouble("longitude") ?: 0.0
                                val imagem = it.getString("imagem") ?: ""
                                Log.i(
                                    "Firestore",
                                    "startObserver: $nome $descricao $latitude $longitude $imagem"
                                )
                                location = Localizacao(nome, descricao, imagem, latitude, longitude, "")
                            }
                        }
                    }
                }

            onNewValues(categoria, poi, location)
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

        fun uploadFile(inputStream: InputStream, imgFile: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child("images")
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
                } else {
                    // Handle failures
                    // ...
                }
            }


        }

        fun addPOIToFirestore(data: HashMap<String, Any>, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            db.collection("Data").document("POI").set(data)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }

        }

        fun addCategoryToFirestore(
            data: HashMap<String, Any>,
            countCategoria: Int,
            onResult: (Throwable?) -> Unit
        ) {
            val db = Firebase.firestore
            db.collection("Categorias").document(countCategoria.toString()).set(data)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }

        }

    }
}