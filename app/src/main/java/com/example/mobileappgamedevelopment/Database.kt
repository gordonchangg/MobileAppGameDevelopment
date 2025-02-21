package com.example.mobileappgamedevelopment

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.net.toUri
import coil3.Bitmap
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import java.io.File

class Database {
    private val database: FirebaseFirestore = Firebase.firestore

    private val userCollection = "users"
    private val TAG = "FirebaseHelper"

    fun addUser(userId: String, email: String, coins: Int) {
        val userData = mapOf(
            "email" to email,
            "coins" to coins
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