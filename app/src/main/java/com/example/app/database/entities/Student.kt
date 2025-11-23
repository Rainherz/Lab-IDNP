package com.example.app.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cui: String,
    val nombres: String,
    val apellidos: String,
    val carreraProfesional: String
)
