package com.vitalforge.watch.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.vitalforge.watch.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ExportScreen(vm: MainViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Export Monthly Report")
        Button(onClick = {
            scope.launch {
                val result = vm.exportReport()
                status = if (result != null) "Export successful" else "Export failed or no consent"
            }
        }) {
            Text("Export")
        }
        status?.let { Text(it) }
    }
}
