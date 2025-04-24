package com.example.epilepsytestapp.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    // État pour l'index courant des instructions
    private val _currentInstructionIndex = MutableStateFlow(0)
    val currentInstructionIndex: StateFlow<Int> get() = _currentInstructionIndex

    // État pour les logs des instructions
    private val _instructionsLog = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val instructionsLog: StateFlow<List<Pair<String, Int>>> get() = _instructionsLog

    // État pour le temps écoulé
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> get() = _elapsedTime

    // Mettre à jour l'index courant
    fun updateInstructionIndex(index: Int) {
        _currentInstructionIndex.value = index
    }

    // Ajouter une instruction au log
    fun addInstructionLog(instruction: Pair<String, Int>) {
        _instructionsLog.value = _instructionsLog.value + instruction
    }

    // Réinitialiser les logs des instructions
    fun resetInstructionsLog() {
        _instructionsLog.value = emptyList()
    }

    // Mettre à jour le temps écoulé
    fun updateElapsedTime(time: Int) {
        _elapsedTime.value = time
    }

    // Réinitialiser le temps écoulé
    fun resetElapsedTime() {
        _elapsedTime.value = 0
    }
}