package com.example.app.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app.database.AppDatabase
import com.example.app.repository.StudentRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class StudentMonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "StudentMonitorWorker"
        const val WORK_NAME = "student_monitor_work"
    }

    override suspend fun doWork(): Result {
        return try {
            // Obtener la base de datos y repositorio
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = StudentRepository(database.studentDao())
            
            // Obtener información de estudiantes
            val students = repository.getAllStudents().first()
            val studentCount = repository.getStudentCount()
            
            // Crear timestamp para el log
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
            
            // Generar log detallado
            val logMessage = buildString {
                appendLine("=== STUDENT MONITOR REPORT ===")
                appendLine("Timestamp: $timestamp")
                appendLine("Total Students: $studentCount")
                appendLine("Database Status: ${if (studentCount > 0) "Active" else "Empty"}")
                
                if (students.isNotEmpty()) {
                    appendLine("\nRecent Students:")
                    students.take(3).forEach { student ->
                        appendLine("- ${student.nombres} ${student.apellidos} (CUI: ${student.cui})")
                    }
                    if (students.size > 3) {
                        appendLine("... and ${students.size - 3} more students")
                    }
                }
                
                appendLine("\nCarreras representadas:")
                val carreras = students.map { it.carreraProfesional }.distinct()
                carreras.take(5).forEach { carrera ->
                    val count = students.count { it.carreraProfesional == carrera }
                    appendLine("- $carrera: $count estudiante(s)")
                }
                
                appendLine("==============================")
            }
            
            // Mostrar en Logcat y consola
            Log.i(TAG, logMessage)
            println(logMessage)
            
            // También mostrar un resumen corto
            val summary = "📊 Monitor: $studentCount estudiantes registrados - $timestamp"
            Log.d(TAG, summary)
            println(summary)
            
            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error en StudentMonitorWorker: ${exception.message}", exception)
            println("❌ Error en monitor de estudiantes: ${exception.message}")
            Result.retry()
        }
    }
}
