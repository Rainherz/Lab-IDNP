package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.ThemePreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(private val themeRepository: ThemePreferencesRepository) : ViewModel() {
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    init {
        // Observar cambios en el tema del repositorio
        viewModelScope.launch {
            themeRepository.isDarkTheme.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }
    
    fun toggleTheme() {
        viewModelScope.launch {
            val newTheme = !_isDarkTheme.value
            themeRepository.setDarkTheme(newTheme)
        }
    }
    
    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkTheme(isDark)
        }
    }
}
