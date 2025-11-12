package com.example.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.data.ThemePreferencesRepository
import com.example.app.ui.screens.ThemeSettingsScreen
import com.example.app.ui.theme.AppTheme
import com.example.app.viewmodel.ThemeViewModel

// Data class para representar un estudiante
data class Student(
    val cui: String,
    val nombres: String,
    val apellidos: String,
    val carreraProfesional: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeRepository = ThemePreferencesRepository(this)
            val themeViewModel: ThemeViewModel = viewModel {
                ThemeViewModel(themeRepository)
            }
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            
            AppTheme(darkTheme = isDarkTheme) {
                StudentApp(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun StudentApp(themeViewModel: ThemeViewModel) {
    // Lista de estudiantes con datos iniciales
    var students by remember { mutableStateOf(getSampleStudents()) }
    var currentScreen by remember { mutableStateOf("home") }
    
    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToForm = { currentScreen = "form" },
            onNavigateToList = { currentScreen = "list" },
            onNavigateToThemeSettings = { currentScreen = "theme" }
        )
        "form" -> FormScreen(
            onBack = { currentScreen = "home" },
            onAddStudent = { student ->
                students = students + student
                currentScreen = "list"
            }
        )
        "list" -> ListScreen(
            students = students,
            onBack = { currentScreen = "home" }
        )
        "theme" -> ThemeSettingsScreen(
            themeViewModel = themeViewModel,
            onBack = { currentScreen = "home" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToForm: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToThemeSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sistema de Estudiantes") },
                actions = {
                    IconButton(onClick = onNavigateToThemeSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración de Tema"
                        )
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Gestión de Estudiantes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onNavigateToForm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Estudiante", fontSize = 18.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNavigateToList,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Ver Lista",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Lista de Estudiantes", fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    onBack: () -> Unit,
    onAddStudent: (Student) -> Unit
) {
    var cui by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Estudiante") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Complete el formulario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = cui,
                onValueChange = { cui = it },
                label = { Text("CUI (Código Único de Identificación)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && cui.isBlank()
            )
            
            OutlinedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && nombres.isBlank()
            )
            
            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && apellidos.isBlank()
            )
            
            OutlinedTextField(
                value = carrera,
                onValueChange = { carrera = it },
                label = { Text("Carrera Profesional") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && carrera.isBlank()
            )
            
            if (showError) {
                Text(
                    text = "Por favor, complete todos los campos",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    if (cui.isNotBlank() && nombres.isNotBlank() && 
                        apellidos.isNotBlank() && carrera.isNotBlank()) {
                        onAddStudent(
                            Student(
                                cui = cui,
                                nombres = nombres,
                                apellidos = apellidos,
                                carreraProfesional = carrera
                            )
                        )
                        showError = false
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Guardar Estudiante", fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    students: List<Student>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Estudiantes (${students.size})") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            itemsIndexed(students) { index, student ->
                StudentCard(student = student, index = index + 1)
            }
        }
    }
}

@Composable
fun StudentCard(student: Student, index: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estudiante #$index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = student.cui,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            StudentInfoRow(label = "Nombres:", value = student.nombres)
            Spacer(modifier = Modifier.height(8.dp))
            StudentInfoRow(label = "Apellidos:", value = student.apellidos)
            Spacer(modifier = Modifier.height(8.dp))
            StudentInfoRow(label = "Carrera:", value = student.carreraProfesional)
        }
    }
}

@Composable
fun StudentInfoRow(label: String, value: String) {
    Row {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Función para generar datos de muestra
fun getSampleStudents(): List<Student> {
    return listOf(
        Student("75123456", "Juan Carlos", "García López", "Ingeniería de Sistemas"),
        Student("75234567", "María Elena", "Rodríguez Pérez", "Medicina Humana"),
        Student("75345678", "Pedro José", "Martínez Sánchez", "Derecho"),
        Student("75456789", "Ana Sofía", "Hernández Torres", "Arquitectura"),
        Student("75567890", "Luis Miguel", "González Ramírez", "Ingeniería Civil"),
        Student("75678901", "Carmen Rosa", "Díaz Flores", "Contabilidad"),
        Student("75789012", "Roberto Carlos", "Vargas Mendoza", "Administración"),
        Student("75890123", "Patricia Isabel", "Castro Ruiz", "Psicología"),
        Student("75901234", "Fernando Andrés", "Morales Gutiérrez", "Ingeniería Industrial"),
        Student("76012345", "Gabriela Beatriz", "Ortiz Jiménez", "Enfermería"),
        Student("76123456", "Diego Alejandro", "Romero Cruz", "Ingeniería Electrónica"),
        Student("76234567", "Valeria Cristina", "Silva Navarro", "Comunicación Social"),
        Student("76345678", "Javier Eduardo", "Ramos Vega", "Economía"),
        Student("76456789", "Daniela Fernanda", "Paredes Quispe", "Biología"),
        Student("76567890", "Sebastián Mateo", "Chávez Huamán", "Ingeniería Mecánica"),
        Student("76678901", "Camila Andrea", "Mendoza Ccama", "Odontología"),
        Student("76789012", "Nicolás Emilio", "Quispe Mamani", "Ingeniería Química"),
        Student("76890123", "Isabella Sofía", "Flores Condori", "Farmacia y Bioquímica"),
        Student("76901234", "Matías Benjamín", "Torres Apaza", "Ingeniería Ambiental"),
        Student("77012345", "Valentina Lucía", "Huamán Puma", "Turismo y Hotelería"),
        Student("77123456", "Santiago Rafael", "Ccama Yupanqui", "Ingeniería de Minas"),
        Student("77234567", "Renata Alejandra", "Mamani Ticona", "Trabajo Social"),
        Student("77345678", "Joaquín Martín", "Condori Layme", "Agronomía")
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStudentApp() {
    AppTheme(darkTheme = true) {
    }
}
