package cn.wantu.apitest.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.tooling.preview.Preview
import cn.wantu.apitest.ui.activity.ui.theme.ApiTestTheme

class ProjectManager : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApiTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Project(icon: PointerIcon, name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {

    }
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
