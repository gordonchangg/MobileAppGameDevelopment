package com.example.mobileappgamedevelopment

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Database {
    private val database: FirebaseFirestore = Firebase.firestore

    private val userCollection = "users"
    private val TAG = "FirebaseHelper"

    fun addUser(userId: String, email: String, coins: Int, cake: Int, cupcake: Int, latte: Int) {
        val userData = mapOf(
            "email" to email,
            "coins" to coins,
            "cake" to cake,
            "cupcake" to cupcake,
            "latte" to latte
        )
        database.collection(userCollection)
            .document(userId)
            .set(userData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun deleteUser(userId: String) {
        database.collection(userCollection)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "User successfully deleted: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting user", e)
            }
    }

    fun addEntity(userId: String, entity: Entity) {
        val entityData = mapOf(
            "id" to entity.id,
            "position" to entity.position,
            "scale" to entity.scale,
            "rotation" to entity.rotation,
            "layerId" to entity.layerId,
            "textureId" to entity.textureId,
            "userData" to entity.userData
        )
        database.collection(userCollection)
            .document(userId)
            .collection("entities")
            .add(entityData)
            .addOnSuccessListener {
                Log.d(TAG, "Entity successfully added!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding entity", e)
            }

    }

    fun deleteEntity(userId: String, entityId: String) {
        val userRef = database.collection(userCollection).document(userId)
        val entitiesRef = userRef.collection("entities")

        entitiesRef.whereEqualTo("id", entityId).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    for (doc in snapshot.documents) {
                        val entityRef = entitiesRef.document(doc.id)

                        entityRef.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Entity with ID $entityId deleted successfully.")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error deleting entity", e)
                            }
                    }
                } else {
                    Log.d(TAG, "No entity found with ID: $entityId")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error retrieving entity", e)
            }
    }

    fun updateEntity(userId: String, updatedEntity: Entity) {
        val userRef = database.collection(userCollection).document(userId)
        val entitiesRef = userRef.collection("entities")

        entitiesRef.whereEqualTo("id", updatedEntity.id).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    for (doc in snapshot.documents) {
                        val entityRef = entitiesRef.document(doc.id)
                        val entityData = mapOf(
                            "id" to updatedEntity.id,
                            "position" to updatedEntity.position,
                            "scale" to updatedEntity.scale,
                            "rotation" to updatedEntity.rotation,
                            "layerId" to updatedEntity.layerId,
                            "textureId" to updatedEntity.textureId,
                            "userData" to updatedEntity.userData
                        )

                        entityRef.set(entityData, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d(TAG, "Entity updated successfully.")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating entity", e)
                            }
                    }
                } else {
                    Log.d(TAG, "No entity found with ID: ${updatedEntity.id}")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error retrieving entity", e)
            }
    }

    fun loadEntities(userId: String, entityList: MutableList<Entity>,
        onSuccess: () -> Unit, onFailure: (Exception) -> Unit
    ) {
        database.collection(userCollection)
            .document(userId)
            .collection("entities")
            .get()
            .addOnSuccessListener { result ->
                synchronized(entityList)
                {
                    entityList.clear()

                    if (result.isEmpty) {
                        Log.d(TAG, "Entities collection exists but is empty")
                    } else {
                        entityList.addAll(result.documents.mapNotNull { it.toObject(Entity::class.java) })
                    }
                }
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting entities", e)
                onFailure(e)
            }
    }

    fun updateUserCoins(userId: String, coins: Int) {
        val updates = mapOf(
            "coins" to coins
        )
        database.collection(userCollection)
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "User coins successfully updated: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating user coins", e)
            }
    }

    fun updateCake(userId: String, cake: Int) {
        val updates = mapOf(
            "cake" to cake
        )
        database.collection(userCollection)
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Cake successfully updated: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating cake", e)
            }
    }

    fun updateCupcake(userId: String, cupcake: Int) {
        val updates = mapOf(
            "cupcake" to cupcake
        )
        database.collection(userCollection)
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "cupcake successfully updated: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating cupcake", e)
            }
    }

    fun updateLatte(userId: String, latte: Int) {
        val updates = mapOf(
            "latte" to latte
        )
        database.collection(userCollection)
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Latte successfully updated: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating latte", e)
            }
    }

    fun getUser(userId: String, onSuccess: (Map<String, Any?>?) -> Unit, onFailure: (Exception) -> Unit) {
        database.collection(userCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "User data retrieved: ${document.data}")
                    onSuccess(document.data)
                } else {
                    Log.d(TAG, "No such user: $userId")
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting user", e)
                onFailure(e)
            }
    }

    fun getAllUsers(onSuccess: (List<Map<String, Any?>>) -> Unit, onFailure: (Exception) -> Unit) {
        database.collection(userCollection)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = querySnapshot.documents.mapNotNull { document ->
                    document.data?.also { data ->
                        data["id"] = document.id // Optionally include the document ID
                    }
                }
                Log.d(TAG, "All users retrieved: $users")
                onSuccess(users)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting all users", e)
                onFailure(e)
            }
    }

    fun uploadImageToFirebase(
        currentUserId: String,
        file: File,
        onUploadComplete: (String?) -> Unit
    ) {
        if (!file.exists()) {
            println("File does not exist")
            onUploadComplete(null)
            return
        }

        val storageRef = FirebaseStorage.getInstance().getReference("Images")
        val imageRef = storageRef.child("images/${file.name}")

        imageRef.putFile(file.toUri())
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    println("Image uploaded successfully: $imageUrl")

                    val userDocRef = Firebase.firestore.collection("users").document(currentUserId)
                    userDocRef.update("imageUrl", imageUrl)
                        .addOnSuccessListener {
                            println("Firestore updated successfully with image URL")
                            onUploadComplete(imageUrl)
                        }
                        .addOnFailureListener { firestoreException ->
                            println("Failed to update Firestore: ${firestoreException.message}")
                            onUploadComplete(null)
                        }
                }.addOnFailureListener { exception ->
                    println("Failed to get download URL: ${exception.message}")
                    onUploadComplete(null)
                }
            }
            .addOnFailureListener { exception ->
                println("Failed to upload image: ${exception.message}")
                onUploadComplete(null)
            }
    }
}