package cn.wantu.apitest.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog

@Composable
fun DownloadProgressDialog(
    showDialog: Boolean,
    title: String = "下载中...",
    progress: Float,
    onCancel: () -> Unit = {}
) {
    if (showDialog) {
        Dialog(onDismissRequest = onCancel) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "${(progress * 100).toInt()}%")
                }
            }
        }
    }
}

@Composable
fun DownloadScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            showDialog = true
            progress = 0f
            coroutineScope.launch {
                while (progress < 1f) {
                    delay(100)
                    progress += 0.01f
                }
                showDialog = false
            }
        }) {
            Text("开始下载")
        }
    }

    DownloadProgressDialog(
        showDialog = showDialog,
        progress = progress,
        onCancel = {
        }
    )
}

@Composable
@Preview
fun PreviewDownloadScreen() {
    DownloadScreen()
}
