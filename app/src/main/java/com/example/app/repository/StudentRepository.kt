package com.example.app.repository

import com.example.app.database.dao.StudentDao
import com.example.app.database.entities.Student
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {
    
    fun getAllStudents(): Flow<List<Student>> = studentDao.getAllStudents()
    
    suspend fun getStudentById(id: Long): Student? = studentDao.getStudentById(id)
    
    suspend fun getStudentByCui(cui: String): Student? = studentDao.getStudentByCui(cui)
    
    suspend fun insertStudent(student: Student): Long {
        val result = studentDao.insertStudent(student)
        println("Student inserted with ID: $result - ${student.nombres} ${student.apellidos}")
        return result
    }
    
    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
        println("Student updated: ${student.nombres} ${student.apellidos}")
    }
    
    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
        println("Student deleted: ${student.nombres} ${student.apellidos}")
    }
    
    suspend fun deleteStudentById(id: Long) {
        studentDao.deleteStudentById(id)
        println("Student deleted with ID: $id")
    }
    
    suspend fun getStudentCount(): Int = studentDao.getStudentCount()
    
    fun searchStudents(query: String): Flow<List<Student>> = studentDao.searchStudents(query)
    
    suspend fun isStudentExists(cui: String): Boolean {
        return studentDao.getStudentByCui(cui) != null
    }
}
