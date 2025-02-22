package com.example.mobileappgamedevelopment

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil3.Bitmap
import com.google.firebase.database.ValueEventListener
import kotlinx.serialization.Serializable
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue



class MainViewModel() : ViewModel() {
    var entityManager = EntityManager()
    var sceneManager = SceneManager(entityManager)
    var audioManager = AudioManager()

    lateinit var currentUserId : String

    private val _textInfoList = MutableLiveData<MutableList<TextInfo>>(mutableListOf())
    val textInfoList: LiveData<MutableList<TextInfo>> = _textInfoList

    private val _foodItems = MutableLiveData<MutableList<String>>(mutableListOf())
    val foodItems: MutableLiveData<MutableList<String>> = _foodItems

    fun addTextInfo(textInfo: TextInfo) {
        val currentList = _textInfoList.value?.toMutableList() ?: mutableListOf()
        currentList.add(textInfo)
        _textInfoList.value = currentList
    }

    fun isFoodItemExists(food: String): Boolean {
        return _foodItems.value?.contains(food) == true
    }

    fun addFoodItem(food: String) {
        val currentList = _foodItems.value ?: mutableListOf()
        currentList.add(food)
        _foodItems.value = currentList
    }

    fun removeFoodItem(food: String) {
        val currentList = _foodItems.value ?: mutableListOf()
        currentList.remove(food)
        _foodItems.value = currentList
    }

    fun clearFoodItems() {
        _foodItems.value = mutableListOf()
    }

    fun removeTextInfo(textInfo: TextInfo) {
        val currentList = _textInfoList.value ?: mutableListOf()
        currentList.remove(textInfo)
        _textInfoList.value = currentList
    }

    private val database = Database()

    fun addUser(userId: String, email: String, coins: Int) {
        database.addUser(userId, email, coins)
    }

    fun deleteUser(userId: String) {
        database.deleteUser(userId)
    }

    fun updateUserCoins(coins: Int) {
        database.updateUserCoins(currentUserId, coins)
    }

    fun getUser(userId: String, onSuccess: (Map<String, Any?>?) -> Unit, onFailure: (Exception) -> Unit) {
        database.getUser(userId, onSuccess, onFailure)
        currentUserId = userId
    }

    fun getAllUsers(onSuccess: (List<Map<String, Any?>>) -> Unit, onFailure: (Exception) -> Unit) {
        database.getAllUsers(onSuccess, onFailure)
    }

    fun uploadImageToFirebase(file: File, onUploadComplete: (String?) -> Unit){
        database.uploadImageToFirebase(currentUserId, file, onUploadComplete)
    }
}

class MainViewModelFactory() : ViewModelProvider.Factory{
    override fun<T: ViewModel> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom((MainViewModel::class.java))){
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}