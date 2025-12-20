package com.example.healthproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthproject.data.repository.PharmaAssistantRepository

class PharmaChatViewModel : ViewModel() {

    private val repo = PharmaAssistantRepository()
    val response = MutableLiveData<String>()

    fun sendMedicationName(name: String) {
        repo.askMedication(name) {
            response.postValue(it)
        }
    }
}
