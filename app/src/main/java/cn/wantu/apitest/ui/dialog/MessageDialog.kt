package cn.wantu.apitest.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun MessageDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    confirmText: String = "确认",
    dismissText: String = "取消",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissRequest: () -> Unit = {}
) {
    if (showDialog) {
        Dialog(onDismissRequest = dismissRequest) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(dismissText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onConfirm) {
                            Text(confirmText)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MessageDialogPreview() {
    MessageDialog(
        showDialog = true,
        title = "你好",
        message = "我是弹窗测试",
        onConfirm = {  },
        onDismiss = {  }) {

    }
}