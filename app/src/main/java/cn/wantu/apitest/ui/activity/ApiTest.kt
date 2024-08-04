package cn.wantu.apitest.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.wantu.apitest.ui.activity.ui.theme.ApiTestTheme

class ApiTest : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApiTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TextFieldExample(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TextFieldExample(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "You entered: $text")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ApiTestTheme {
        TextFieldExample()
    }
}