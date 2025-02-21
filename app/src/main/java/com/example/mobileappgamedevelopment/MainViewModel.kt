package com.example.mobileappgamedevelopment

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ValueEventListener

class MainViewModel() : ViewModel() {
    var entityManager = EntityManager()
    var sceneManager = SceneManager(entityManager)
    var audioManager = AudioManager()

    lateinit var currentUserId : String

    private val _textInfoList = MutableLiveData<MutableList<TextInfo>>(mutableListOf())
    val textInfoList: LiveData<MutableList<TextInfo>> = _textInfoList

    fun addTextInfo(textInfo: TextInfo) {
        val currentList = _textInfoList.value?.toMutableList() ?: mutableListOf()
        currentList.add(textInfo)
        _textInfoList.value = currentList
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