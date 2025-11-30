package com.example.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.*
import com.example.app.workers.StudentMonitorWorker
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    
    var workStatus by remember { mutableStateOf("Desconocido") }
    var lastExecution by remember { mutableStateOf("Nunca") }
    var isMonitoring by remember { mutableStateOf(false) }
    
    // Observar el estado del trabajo
    LaunchedEffect(Unit) {
        workManager.getWorkInfosForUniqueWorkLiveData(StudentMonitorWorker.WORK_NAME)
            .observeForever { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val workInfo = workInfos.first()
                    workStatus = when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> "En cola"
                        WorkInfo.State.RUNNING -> "Ejecutándose"
                        WorkInfo.State.SUCCEEDED -> "Completado"
                        WorkInfo.State.FAILED -> "Falló"
                        WorkInfo.State.BLOCKED -> "Bloqueado"
                        WorkInfo.State.CANCELLED -> "Cancelado"
                    }
                    isMonitoring = workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
                }
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitor de Sistema") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Control de Monitoreo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Gestiona las tareas de monitoreo en segundo plano que registran información sobre los estudiantes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Estado actual
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Estado del Monitor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estado: $workStatus",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Última ejecución: $lastExecution",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Monitoreo activo: ${if (isMonitoring) "Sí" else "No"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Controles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        // Ejecutar monitoreo inmediato
                        val immediateWork = OneTimeWorkRequestBuilder<StudentMonitorWorker>()
                            .addTag("manual_monitoring")
                            .build()
                        workManager.enqueue(immediateWork)
                        
                        lastExecution = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            .format(Date())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ejecutar Ahora")
                }
                
                OutlinedButton(
                    onClick = {
                        // Cancelar monitoreo periódico
                        workManager.cancelUniqueWork(StudentMonitorWorker.WORK_NAME)
                        workStatus = "Cancelado"
                        isMonitoring = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detener")
                }
            }
            
            Button(
                onClick = {
                    // Reiniciar monitoreo periódico
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()

                    val monitoringWork = PeriodicWorkRequestBuilder<StudentMonitorWorker>(
                        15, java.util.concurrent.TimeUnit.MINUTES
                    )
                        .setConstraints(constraints)
                        .addTag("student_monitoring")
                        .build()

                    workManager.enqueueUniquePeriodicWork(
                        StudentMonitorWorker.WORK_NAME,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        monitoringWork
                    )
                    
                    workStatus = "Reiniciado"
                    isMonitoring = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar Monitor Periódico")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información del Monitor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• El monitor se ejecuta cada 15 minutos automáticamente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "• Registra información sobre estudiantes en la consola",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "• Muestra estadísticas de la base de datos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "• Funciona en segundo plano incluso si la app está cerrada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
