package com.example.mobileappgamedevelopment

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainViewModel() : ViewModel() {
    var entityManager = EntityManager()
    var sceneManager = SceneManager(entityManager)
    var audioManager = AudioManager()

    lateinit var currentUserId : String
    val coinyFont = FontFamily(
        Font(R.font.coiny)
    )
    private val _textInfoList = MutableLiveData<MutableList<TextInfo>>(mutableListOf())
    val textInfoList: LiveData<MutableList<TextInfo>> = _textInfoList

    private val _coins = MutableLiveData<Long>(0) // Use LiveData to track changes
    val coins: LiveData<Long> = _coins

    private val _foodItems = MutableLiveData<MutableList<String>>(mutableListOf())
    val foodItems: MutableLiveData<MutableList<String>> = _foodItems

    private val _foodItemCounts = MutableLiveData<Map<String, Int>>(mapOf(
        "cake" to 0,
        "cupcake" to 0,
        "latte" to 0
    ))
    val foodItemCounts: LiveData<Map<String, Int>> = _foodItemCounts

    fun addTextInfo(textInfo: TextInfo) {
        CoroutineScope(Dispatchers.Main).launch {
            val currentList = _textInfoList.value?.toMutableList() ?: mutableListOf()
            currentList.add(textInfo)
            _textInfoList.value = currentList // Safe to use setValue on the main thread
        }
    }

    fun isFoodItemExists(food: String): Boolean {
        return _foodItems.value?.contains(food) == true
    }

    fun getFoodItemCount(food: String): Int {
        return _foodItemCounts.value?.get(food) ?: 0
    }

    fun addFoodItem(food: String) {
        val currentList = _foodItems.value ?: mutableListOf()
        currentList.add(food)
        _foodItems.value = currentList

        val quantityToAdd = when (food) {
            "cake" -> 5
            "cupcake" -> 10
            "latte" -> 15
            else -> 1
        }

        _foodItemCounts.value = _foodItemCounts.value?.toMutableMap()?.apply {
            this[food] = (this[food] ?: 0) + quantityToAdd
        }

        println("added ${quantityToAdd} ${food}! current ${food} count = ${getFoodItemCount(food)}")
    }

    fun removeFoodItem(food: String) {
        val currentList = _foodItems.value ?: mutableListOf()
        currentList.remove(food)
        _foodItems.value = currentList
    }

    fun decreaseFoodCount(food: String) {
        _foodItemCounts.value = _foodItemCounts.value?.toMutableMap()?.apply {
            val currentCount = this[food] ?: 0
            if (currentCount > 0) {
                this[food] = currentCount - 1
                println("removed 1 ${food}! current ${food} count = ${getFoodItemCount(food)}")
            }
        }
    }

    fun clearFoodItems() {
        _foodItems.value = mutableListOf()

        _foodItemCounts.value = mapOf(
            "cake" to 0,
            "cupcake" to 0,
            "latte" to 0
        )
    }

    fun removeTextInfo(textInfo: TextInfo) {
        val currentList = _textInfoList.value?.toMutableList() ?: mutableListOf()
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

    fun updateUserCoins(newCoins: Long) {
        _coins.value = newCoins // Update UI immediately
        database.updateUserCoins(currentUserId, newCoins.toInt()) // Sync with database
    }

    /** Retrieve Coins from Firebase **/
    fun getCurrentUserCoins() {
        getUser(currentUserId, { userData ->
            val retrievedCoins = (userData?.get("coins") as? Number)?.toInt() ?: 0
            _coins.value = retrievedCoins.toLong() // Update LiveData
            println("🪙 Coins updated: ${_coins.value}")
        }, { error ->
            println("Error fetching user coins: ${error.message}")
            _coins.value = 0 // Default to 0 in case of failure
        })
    }

    /** Add and Subtract Coins **/
    fun addCoins(amount: Long) {
        val newCoins = (_coins.value ?: 0) + amount.toLong()
        updateUserCoins(newCoins)
        println("added $amount coins")
    }

    fun subtractCoins(amount: Int): Boolean {
        val currentCoins = _coins.value ?: 0
        return if (currentCoins >= amount) {
            val newCoins = currentCoins - amount.toLong()
            updateUserCoins(newCoins)
            true // Successfully deducted
        } else {
            println("Not enough coins!") // Prevent negative coins
            false
        }
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