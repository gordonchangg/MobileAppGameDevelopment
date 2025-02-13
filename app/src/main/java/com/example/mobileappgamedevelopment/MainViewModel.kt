package com.example.mobileappgamedevelopment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel() : ViewModel() {
    var sceneManager = SceneManager()
    var entityManager = EntityManager()
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