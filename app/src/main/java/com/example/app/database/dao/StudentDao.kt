package com.example.app.database.dao

import androidx.room.*
import com.example.app.database.entities.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    
    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>
    
    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): Student?
    
    @Query("SELECT * FROM students WHERE cui = :cui")
    suspend fun getStudentByCui(cui: String): Student?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long
    
    @Update
    suspend fun updateStudent(student: Student)
    
    @Delete
    suspend fun deleteStudent(student: Student)
    
    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)
    
    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
    
    @Query("SELECT * FROM students WHERE nombres LIKE '%' || :query || '%' OR apellidos LIKE '%' || :query || '%' OR cui LIKE '%' || :query || '%' OR carreraProfesional LIKE '%' || :query || '%'")
    fun searchStudents(query: String): Flow<List<Student>>
}
