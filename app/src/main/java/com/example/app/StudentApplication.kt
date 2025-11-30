package com.example.app

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.app.workers.StudentMonitorWorker
import java.util.concurrent.TimeUnit

class StudentApplication : Application() {

    companion object {
        const val TAG = "StudentApplication"
    }

    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "Inicializando StudentApplication")
        println("🚀 Iniciando aplicación de gestión de estudiantes")
        
        // Programar tarea periódica de monitoreo
        scheduleStudentMonitoring()
    }

    private fun scheduleStudentMonitoring() {
        // Crear restricciones para la tarea
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // No requiere internet
            .setRequiresBatteryNotLow(false) // Puede ejecutarse con batería baja
            .setRequiresCharging(false) // No requiere estar cargando
            .build()

        // Crear la tarea periódica
        val monitoringWork = PeriodicWorkRequestBuilder<StudentMonitorWorker>(
            15, TimeUnit.MINUTES // Intervalo mínimo permitido por Android
        )
            .setConstraints(constraints)
            .addTag("student_monitoring")
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        // Programar la tarea (reemplaza cualquier tarea existente con el mismo nombre)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            StudentMonitorWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            monitoringWork
        )

        Log.d(TAG, "Tarea de monitoreo programada cada 15 minutos")
        println("⏰ Monitor de estudiantes programado cada 15 minutos")
        
        // También ejecutar una tarea inmediata para prueba
        scheduleImmediateMonitoring()
    }

    private fun scheduleImmediateMonitoring() {
        val immediateWork = OneTimeWorkRequestBuilder<StudentMonitorWorker>()
            .addTag("immediate_monitoring")
            .build()

        WorkManager.getInstance(this).enqueue(immediateWork)
        
        Log.d(TAG, "Tarea inmediata de monitoreo programada")
        println("🔍 Ejecutando monitoreo inmediato...")
    }
}
