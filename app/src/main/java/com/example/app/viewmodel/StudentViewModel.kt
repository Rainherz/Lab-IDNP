package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.database.entities.Student
import com.example.app.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StudentUiState(
    val students: List<Student> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.getAllStudents().collect { students ->
                    _uiState.value = _uiState.value.copy(
                        students = students,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun addStudent(student: Student, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar si el CUI ya existe
                if (repository.isStudentExists(student.cui)) {
                    onError("Ya existe un estudiante con el CUI: ${student.cui}")
                    return@launch
                }

                val id = repository.insertStudent(student)
                if (id > 0) {
                    onSuccess()
                } else {
                    onError("Error al guardar el estudiante")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateStudent(student: Student, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateStudent(student)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al actualizar el estudiante")
            }
        }
    }

    fun deleteStudent(student: Student, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteStudent(student)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al eliminar el estudiante")
            }
        }
    }

    fun searchStudents(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, searchQuery = query)
            try {
                if (query.isBlank()) {
                    loadStudents()
                } else {
                    repository.searchStudents(query).collect { students ->
                        _uiState.value = _uiState.value.copy(
                            students = students,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadStudents()
    }

    suspend fun getStudentCount(): Int {
        return repository.getStudentCount()
    }
}
