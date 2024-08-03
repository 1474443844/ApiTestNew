package cn.wantu.apitest

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.wantu.apitest.data.ApiTestConfig
import cn.wantu.apitest.ui.dialog.DownloadProgressDialog
import cn.wantu.apitest.ui.dialog.MessageDialog
import cn.wantu.apitest.utils.HttpUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Request

class MainActivity : ComponentActivity() {

    private var showUpdateDialog by mutableStateOf(false)
    private var showDownloadDialog by mutableStateOf(false)
    private var progress by  mutableFloatStateOf(0f)
    private val client = ApiApp.okHttpClient()

    companion object {
        lateinit var json: ApiTestConfig
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessageDialog(
                showDialog = showUpdateDialog,
                title = "更新",
                message = "这是一个带有两个按钮的消息提示弹窗。",
                confirmText = "更新",
                onConfirm = {
                    showUpdateDialog = false
                    showDownloadDialog = true
                },
                onDismiss = { showUpdateDialog= false }
            )
            DownloadProgressDialog(
                showDialog = showDownloadDialog,
                progress = progress,
                onCancel = {
                }
            )
        }
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            json = Json.decodeFromString<ApiTestConfig>(
                HttpUtils.get("https://docs.wty5.cn/App/ApiTestConfig.json")
                    .excuteString()!!)
            val pi = packageManager.getPackageInfo(packageName, 0)
            val currentVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode
            } else {
                pi.versionCode.toLong()
            }
            if (currentVersion < json.versionCode) {
                showUpdateDialog = true
            }
        }
    }
    suspend fun fetchConfig() = withContext(Dispatchers.IO){
        HttpUtils.get("https://docs.wty5.cn/App/ApiTestConfig.json").excuteString()
    }
}
